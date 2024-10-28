package com.group.receiptapp.web.member;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.service.group.GroupService;
import com.group.receiptapp.service.member.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller
//@RequestMapping("/members")
//public class MemberController {
//
//    @Autowired
//    private GroupService groupService;
//
//    @Autowired
//    private final MemberService memberService;
//
//    // 생성자 주입으로 MemberService 주입
//    public MemberController(MemberService memberService) {
//        this.memberService = memberService;
//    }
//
//    // 회원 등록 폼으로 이동
//    @GetMapping("/add")
//    public String addMemberForm(Model model) {
//        model.addAttribute("member", new Member());
//
//        // 그룹 리스트를 폼에서 선택할 수 있게 넘겨줌
//        List<Group> groups = groupService.findAllGroups();
//        model.addAttribute("groups", groups);
//
//        return "members/addMemberForm";
//    }
//
//    // 회원 등록 처리
//    @PostMapping("/add")
//    public String addMember(@Valid @ModelAttribute Member member, BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            // 유효성 검사 실패 시 다시 폼으로 이동
//            List<Group> groups = groupService.findAllGroups();
//            model.addAttribute("groups", groups);
//            return "members/addMemberForm";
//        }
//
//        // 선택된 그룹 설정
//        Group group = groupService.findOne(member.getGroup().getId());
//        member.setGroup(group);
//
//        // 서비스 계층에서 회원가입 처리 (비밀번호 암호화 포함)
//        memberService.join(member);
//
//        return "redirect:/members";
//    }
//
//    // 전체 회원 목록 조회
//    @GetMapping
//    public String listMembers(Model model) {
//        List<Member> members = memberService.findMember();
//        model.addAttribute("members", members);
//        return "members/memberList";
//    }
//
//    // 특정 회원 상세 조회
//    @GetMapping("/{id}")
//    public String viewMember(@PathVariable Long id, Model model) {
//        Member member = memberService.findOne(id);
//        model.addAttribute("member", member);
//        return "members/memberDetail";
//    }
//
//    // 특정 그룹에 속한 회원 조회
//    @GetMapping("/group/{groupId}")
//    public String listMembersByGroup(@PathVariable Long groupId, Model model) {
//        List<Member> membersGroup = groupService.findMembersByGroup(groupId);
//        model.addAttribute("membersGroup", membersGroup);
//
//        Group group = groupService.findOne(groupId);
//        model.addAttribute("group", group);
//
//        return "members/memberListByGroup";  // 그룹별 회원 리스트를 보여주는 템플릿
//    }
//
//    // 그룹 설정 폼으로 이동
//    @GetMapping("/{id}/setGroup")
//    public String setGroupForm(@PathVariable Long id, Model model) {
//        Member member = memberService.findOne(id);
//        if (member == null) {
//            return "redirect:/members";  // 회원이 없을 경우 리스트 페이지로 리다이렉트
//        }
//
//        model.addAttribute("member", member);
//
//        // 모든 그룹 리스트를 넘겨서 선택할 수 있게
//        List<Group> groups = groupService.findAllGroups();
//        model.addAttribute("groups", groups);
//
//        return "members/setGroupForm";  // 그룹 설정 폼 템플릿
//    }
//
//
//    // 그룹 설정 처리
//    @PostMapping("/{id}/setGroup")
//    public String setGroup(@PathVariable Long id, @RequestParam Long groupId) {
//        Member member = memberService.findOne(id);
//        if (member == null) {
//            return "redirect:/members";  // 회원이 없을 경우 리다이렉트
//        }
//
//        Group group = groupService.findOne(groupId);
//        member.setGroup(group);
//
//        memberService.save(member);  // MemberService를 통해 저장
//
//        return "redirect:/members/" + id;  // 설정 후 해당 회원 상세 페이지로 리다이렉트
//    }
//}

import com.group.receiptapp.dto.MemberResponse;
import com.group.receiptapp.dto.GroupResponse;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final GroupService groupService;

    public MemberController(MemberService memberService, GroupService groupService) {
        this.memberService = memberService;
        this.groupService = groupService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMember(@RequestBody @Valid Member member) {
        Group group = groupService.findOne(member.getGroup().getId());
        member.setGroup(group);
        Member savedMember = memberService.join(member);

//        // 비밀번호를 제외한 정보를 포함하여 MemberResponse 객체 생성
//        MemberResponse response = new MemberResponse(savedMember);
//        return ResponseEntity.ok(response); // JSON 응답

        // 회원가입 완료 메시지를 반환
        return ResponseEntity.ok("회원가입 완료");
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> listMembers() {
        List<Member> members = memberService.findMember();
        List<MemberResponse> response = members.stream().map(MemberResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> viewMember(@PathVariable Long id) {
        Member member = memberService.findOne(id);
        return ResponseEntity.ok(new MemberResponse(member));
    }

}
