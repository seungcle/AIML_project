package com.group.receiptapp.controller.group;

import com.group.receiptapp.domain.email.EmailMessage;
import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.join.JoinRequest;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.dto.GroupResponse;
import com.group.receiptapp.dto.join.JoinRequestResponse;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.service.email.EmailService;
import com.group.receiptapp.service.group.GroupService;
import com.group.receiptapp.service.member.MemberService;
import com.group.receiptapp.service.notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public GroupController(MemberService memberService, GroupService groupService, EmailService emailService,
                           NotificationService notificationService, MemberRepository memberRepository) {
        this.memberService = memberService; // memberService 주입
        this.groupService = groupService; // groupService 주입
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.memberRepository = memberRepository;
    }

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupRequest request, Principal principal) {
        String email = principal.getName();  // 인증된 사용자의 이메일을 가져옴
        Member admin = memberService.getMemberByEmail(email);

        admin.setAdmin(true);
        Group group = groupService.createGroup(request, admin);
        return ResponseEntity.ok(new GroupResponse(group));
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
}
