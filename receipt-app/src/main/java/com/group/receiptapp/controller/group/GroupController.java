package com.group.receiptapp.controller.group;

import com.group.receiptapp.domain.email.EmailMessage;
import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.join.JoinRequest;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.dto.GroupResponse;
import com.group.receiptapp.dto.group.AdminGroupResponse;
import com.group.receiptapp.dto.join.JoinRequestResponse;
import com.group.receiptapp.dto.member.MemberBudgetResponse;
import com.group.receiptapp.dto.member.MemberResponse;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.email.EmailService;
import com.group.receiptapp.service.group.GroupReceiptSettingService;
import com.group.receiptapp.service.group.GroupService;
import com.group.receiptapp.service.login.LoginService;
import com.group.receiptapp.service.member.MemberService;
import com.group.receiptapp.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.group.receiptapp.dto.group.CreateGroupRequest;

@RestController
@RequestMapping("/group")
public class GroupController {

    private final MemberService memberService;
    private final GroupService groupService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final GroupReceiptSettingService groupReceiptSettingService;
    private final JwtUtil jwtUtil;
    private final LoginService loginService;

    public GroupController(MemberService memberService, GroupService groupService, EmailService emailService,
                           NotificationService notificationService, MemberRepository memberRepository,
                           GroupReceiptSettingService groupReceiptSettingService, JwtUtil jwtUtil, LoginService loginService) {
        this.memberService = memberService; // memberService 주입
        this.groupService = groupService; // groupService 주입
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.memberRepository = memberRepository;
        this.groupReceiptSettingService = groupReceiptSettingService;
        this.jwtUtil = jwtUtil;
        this.loginService = loginService;
    }

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupRequest request, Principal principal) {
        String email = principal.getName();  // 인증된 사용자의 이메일을 가져옴
        Member admin = memberService.getMemberByEmail(email);

        admin.setAdmin(true);
        Group group = groupService.createGroup(request, admin);
        // 생성된 그룹의 관리자로 설정
        admin.setGroup(group);
        admin.setAdmin(true); // 관리자로 설정
        memberService.save(admin);  // 관리자로 저장

        Group updatedGroup = groupService.getGroupById(group.getId());
        return ResponseEntity.ok(new GroupResponse(updatedGroup));
    }

    @PostMapping("/join/{groupId}")
    public ResponseEntity<JoinRequestResponse> requestToJoinGroup(@PathVariable Long groupId, Principal principal) {
        String email = principal.getName();
        Member member = memberService.getMemberByEmail(email);

        // 가입 요청 저장
        JoinRequest joinRequest = groupService.requestToJoinGroup(groupId, member);
        JoinRequestResponse response = JoinRequestResponse.fromEntity(joinRequest);

        // 관리자 조회
        Member admin = memberRepository.findAdminByGroupId(groupId)
                .orElseThrow(() -> new IllegalStateException("그룹 관리자 정보를 찾을 수 없습니다."));

        // 알림 전송
        notificationService.sendNotification(
                admin.getId(),
                member.getName() + "님이 그룹에 가입을 요청했습니다."
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/requests/approve/{joinRequestId}")
    public ResponseEntity<Map<String, String>> approveJoinRequest(@PathVariable Long joinRequestId) {
        groupService.updateJoinRequestStatus(joinRequestId, JoinRequest.Status.APPROVED);

        // 2. JoinRequest에서 Member와 Group 가져오기
        JoinRequest joinRequest = groupService.getJoinRequestById(joinRequestId);
        Member member = joinRequest.getMember();
        Group group = joinRequest.getGroup();

        // 3. Member의 group 필드 업데이트
        member.setGroup(group);
        memberService.updateMember(member); // 데이터베이스에 저장

        notificationService.sendNotification(
                member.getId(),
                group.getName() + " 그룹 가입이 승인되었습니다!"
        );

        // 이메일 알림 전송
        String toEmail = groupService.getMemberEmailByJoinRequestId(joinRequestId);
        EmailMessage emailMessage = EmailMessage.builder()
                .to(toEmail)
                .subject("가입 승인 안내")
                .message("그룹 가입이 승인되었습니다.")
                .build();
        emailService.sendApprovalEmail(emailMessage);

        Map<String, String> response = new HashMap<>();
        response.put("message", "가입 요청이 승인되었습니다.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/requests/reject/{joinRequestId}")
    public ResponseEntity<Map<String, String>> rejectJoinRequest(@PathVariable Long joinRequestId) {
        groupService.updateJoinRequestStatus(joinRequestId, JoinRequest.Status.REJECTED);

        JoinRequest joinRequest = groupService.getJoinRequestById(joinRequestId);
        Member member = joinRequest.getMember();
        Group group = joinRequest.getGroup();

        notificationService.sendNotification(
                member.getId(),
                group.getName() + " 그룹 가입 요청이 거절되었습니다."
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "가입 요청이 거절되었습니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        List<GroupResponse> groups = groupService.findAllGroups().stream()
                .map(GroupResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/requests/{groupId}")
    public ResponseEntity<List<JoinRequestResponse>> getJoinRequests(@PathVariable Long groupId) {
        List<JoinRequest> joinRequests = groupService.findJoinRequestsByGroup(groupId);

        List<JoinRequestResponse> responses = joinRequests.stream()
                .map(JoinRequestResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // 중복 방지 설정 변경
    @PutMapping("/{groupId}/check-duplicate")
    public ResponseEntity<Void> togglePreventDuplicate(
            @PathVariable Long groupId,
            @RequestParam boolean enable,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(token);
        Long memberId = loginService.getMemberIdByEmail(email);

        groupReceiptSettingService.toggleDuplicateCheck(groupId, enable, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<MemberResponse>> getGroupMembers(@PathVariable Long groupId) {
        List<Member> members = memberRepository.findByGroupId(groupId);  // 그룹 ID 기준 조회
        List<MemberResponse> response = members.stream()
                .map(MemberResponse::new) // 기존 생성자 활용
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 그룹 관리자가 멤버들의 지출 한도 정보 조회하기
    @GetMapping("/{groupId}/members/budgets")
    public ResponseEntity<List<MemberBudgetResponse>> getGroupMemberBudgets(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = jwtUtil.resolveToken(authHeader);
        String email = jwtUtil.extractUsername(token);
        Long requesterId = loginService.getMemberIdByEmail(email);

        List<MemberBudgetResponse> members = groupReceiptSettingService.getMembersWithBudget(groupId, requesterId);
        return ResponseEntity.ok(members);
    }

    // 지출 한도 설정하기
    @PutMapping("/{groupId}/members/{memberId}/budget")
    public ResponseEntity<Void> updateBudget(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestParam BigDecimal budget,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.resolveToken(authHeader);
        String email = jwtUtil.extractUsername(token);
        Long requesterId = loginService.getMemberIdByEmail(email);

        groupReceiptSettingService.updateMemberBudget(groupId, memberId, budget, requesterId);
        return ResponseEntity.ok().build();
    }

    // 그룹 관리자가 속한 그룹의 정보
    @GetMapping("/my-group")
    public ResponseEntity<AdminGroupResponse> getMyGroupInfo(@RequestHeader("Authorization") String authHeader) {
        String token = jwtUtil.resolveToken(authHeader);
        String email = jwtUtil.extractUsername(token);
        Long memberId = loginService.getMemberIdByEmail(email);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!member.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 관리자가 아닐 경우
        }

        Group group = member.getGroup();
        if (group == null) {
            return ResponseEntity.notFound().build(); // 그룹이 없을 경우
        }

        AdminGroupResponse response = new AdminGroupResponse(group);
        return ResponseEntity.ok(response);
    }
}
