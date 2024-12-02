package com.group.receiptapp.controller.image;

import com.group.receiptapp.dto.ocr.OcrRequest;
import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.login.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/images")
public class ImageUploadController {

    private final JwtUtil jwtUtil; // JwtUtil 의존성 주입
    private final CustomUserDetailsService customUserDetailsService;

    public ImageUploadController(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }
    @PostMapping("/upload")
    public ResponseEntity<OcrRequest> uploadImage(@RequestParam("file") MultipartFile file,
                                                  @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        // Authorization 헤더에서 토큰 추출
        String token = jwtUtil.resolveToken(authorizationHeader);

        // 토큰에서 사용자 이름 추출
        String username = jwtUtil.extractUsername(token);

        // UserDetails 로드
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // 토큰 검증
        if (!jwtUtil.validateToken(token, userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 인증 실패 응답
        }

        // 파일 데이터 추출
        byte[] fileData = file.getBytes();
        String fileName = file.getOriginalFilename();
        String fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // OcrRequest.Image 객체 생성
        OcrRequest.Image image = new OcrRequest.Image();
        image.setFormat(fileFormat); // 파일 형식
        image.setName(fileName); // 파일 이름
        image.setData(Base64.getEncoder().encodeToString(fileData)); // Base64로 인코딩

        // OcrRequest 객체 생성
        OcrRequest ocrRequest = new OcrRequest();
        ocrRequest.setVersion("V2"); // API 버전
        ocrRequest.setRequestId(UUID.randomUUID().toString()); // 고유 요청 ID
        ocrRequest.setTimestamp(System.currentTimeMillis()); // 타임스탬프
        ocrRequest.setImages(Collections.singletonList(image)); // 이미지 리스트 설정
        ocrRequest.setSubmittedBy(username); // 요청자 정보 추가

        // OcrRequest 객체를 응답으로 반환
        return ResponseEntity.ok(ocrRequest);
    }
}