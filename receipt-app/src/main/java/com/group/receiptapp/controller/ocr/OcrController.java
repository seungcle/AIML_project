package com.group.receiptapp.controller.ocr;

import com.group.receiptapp.dto.ocr.OcrRequest;
import com.group.receiptapp.dto.ocr.OcrResponse;
import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.login.CustomUserDetailsService;
import com.group.receiptapp.service.ocr.OcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ocr")
public class OcrController {

    private final OcrService ocrService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public OcrController(OcrService ocrService, JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.ocrService = ocrService;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    /*
    @PostMapping("/process")
    public ResponseEntity<String> processOcr(@RequestBody OcrResponse ocrResponse,
                                             @RequestHeader("Authorization") String authorization) {
        try {
            // JWT 토큰에서 사용자 이름 추출
            String token = authorization.substring(7);  // "Bearer " 제거
            String username = jwtUtil.extractUsername(token);

            // 사용자 정보를 로드하여 memberId를 가져옴
            Long memberId = customUserDetailsService.loadUserByUsernameAsMember(username).getId();

            // OCR 응답을 받아서 처리하고 영수증 저장
            ocrService.processOcrResponse(ocrResponse, memberId);

            // 성공적으로 처리된 경우 응답 반환
            return ResponseEntity.ok("OCR Processing Completed");
        } catch (Exception e) {
            // 오류 발생 시 적절한 메시지 반환
            return ResponseEntity.status(500).body("Error processing the OCR request");
        }
    }
     */
}