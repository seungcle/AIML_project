package com.group.receiptapp.controller.member;

import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.dto.member.MemberResponse;
import com.group.receiptapp.security.CustomUserDetails;
import com.group.receiptapp.service.group.GroupService;
import com.group.receiptapp.service.member.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService, GroupService groupService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signupMember(@RequestBody @Valid Member member) {
        try {
            member.setGroup(null);
            Member savedMember = memberService.join(member);
            MemberResponse response = new MemberResponse(savedMember);
            return ResponseEntity.ok(response); // JSON 응답으로 MemberResponse 반환
        } catch (IllegalStateException e) {
            MemberResponse errorResponse = new MemberResponse("이미 존재하는 이메일입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization") String token) {
        // JWT 값만 추출
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;

        memberService.deleteAccount(jwt);

        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    @GetMapping("/current")
    public ResponseEntity<MemberResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getMember() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new MemberResponse(userDetails.getMember()));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> listMembers() {
        List<MemberResponse> response = memberService.findMember()
                .stream()
                .map(MemberResponse::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> viewMember(@PathVariable Long id) {
        return ResponseEntity.ok(new MemberResponse(memberService.findOne(id)));
    }

    // 예외 처리 핸들러
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }

}
