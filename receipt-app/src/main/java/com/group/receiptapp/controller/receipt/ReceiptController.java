package com.group.receiptapp.controller.receipt;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.dto.ocr.OcrResponse;
import com.group.receiptapp.dto.receipt.ReceiptResponse;
import com.group.receiptapp.dto.receipt.ReceiptSaveRequest;
import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.login.CustomUserDetailsService;
import com.group.receiptapp.service.ocr.OcrService;
import com.group.receiptapp.service.receipt.ReceiptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final OcrService ocrService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final ReceiptService receiptService;

    public ReceiptController(OcrService ocrService, JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService, ReceiptService receiptService) {
        this.ocrService = ocrService;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.receiptService = receiptService;
    }

    /*
    @PostMapping("/process")
    public ResponseEntity<ReceiptResponse> processAll(@RequestParam("file") MultipartFile file,
                                                      @RequestHeader("Authorization") String authorizationHeader) {
        try {

            // OCR 처리 및 DB 저장
            ReceiptResponse receiptResponse = ocrService.processOcrAndSaveReceipt(file, authorizationHeader);
            return ResponseEntity.ok(receiptResponse);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(new ReceiptResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ReceiptResponse("Error processing receipt: " + e.getMessage()));
        }
    }
    */

    @PostMapping("/preview")
    public ResponseEntity<ReceiptResponse> previewReceipt(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            ReceiptResponse response = ocrService.previewReceiptWithAuth(file, authorizationHeader);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ReceiptResponse("Unauthorized: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ReceiptResponse("OCR error: " + e.getMessage()));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<ReceiptResponse> saveReceipt(@RequestBody ReceiptSaveRequest request,
                                                       @RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7);  // Bearer 제거
            String username = jwtUtil.extractUsername(token);
            Member member = customUserDetailsService.loadUserByUsernameAsMember(username);

            // 사용자 ID 설정
            request.setMemberId(member.getId());

            // 저장 처리
            ReceiptResponse receiptResponse = receiptService.saveReceipt(request);
            return ResponseEntity.ok(receiptResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ReceiptResponse("Error saving receipt: " + e.getMessage()));
        }
    }

    /*
    @PostMapping("/save")
    public ResponseEntity<ReceiptResponse> saveReceipt(@RequestBody OcrResponse ocrResponse,
                                              @RequestHeader("Authorization") String authorization) {
        try {
            // JWT 토큰에서 사용자 이름 추출
            String token = authorization.substring(7);  // "Bearer " 제거
            String username = jwtUtil.extractUsername(token);

            // 사용자 정보를 로드하여 memberId를 가져옴
            Member member = customUserDetailsService.loadUserByUsernameAsMember(username);
            Long memberId = member.getId(); // Member 엔티티에서 getId() 호출

            // OCR 응답을 처리하여 영수증을 저장
            ReceiptResponse receiptResponse = receiptService.saveReceiptFromOcr(ocrResponse, memberId);

            return ResponseEntity.ok(receiptResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ReceiptResponse("Error saving receipt: " + e.getMessage()));
        }
    }
     */

    @GetMapping("/my")
    public ResponseEntity<List<ReceiptResponse>> getUserReceipts(@RequestHeader("Authorization") String authorization) {
        try {
            // JWT 토큰에서 사용자 이름 추출
            String token = authorization.substring(7); // "Bearer " 제거
            String username = jwtUtil.extractUsername(token);

            // 사용자 정보를 로드하여 memberId를 가져옴
            Member member = customUserDetailsService.loadUserByUsernameAsMember(username);
            Long memberId = member.getId(); // Member 엔티티에서 getId() 호출

            // 로그인한 유저의 모든 영수증 조회
            List<ReceiptResponse> receipts = receiptService.getReceiptsByMemberId(memberId);

            return ResponseEntity.ok(receipts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    private Long getCurrentMemberId() {
        // 예시: Spring Security에서 인증된 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getName()); // `authentication.getName()`은 JWT의 username
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<ReceiptResponse> deleteReceipt(@PathVariable("id") Long receiptId,
                                                         @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // 사용자 인증
            String token = jwtUtil.resolveToken(authorizationHeader);
            String username = jwtUtil.extractUsername(token);
            Member member = customUserDetailsService.loadUserByUsernameAsMember(username);

            // 영수증 삭제
            ReceiptResponse deletedReceipt = receiptService.deleteReceipt(receiptId, member.getId());
            return ResponseEntity.ok(deletedReceipt);  // 삭제된 영수증 정보 반환
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(new ReceiptResponse("Unauthorized: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ReceiptResponse("Receipt not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ReceiptResponse("Error deleting receipt: " + e.getMessage()));
        }
    }

    
}