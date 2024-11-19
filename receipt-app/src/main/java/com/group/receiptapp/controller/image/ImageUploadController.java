package com.group.receiptapp.controller.image;

import com.group.receiptapp.dto.ocr.OcrRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/images")
public class ImageUploadController {

    @PostMapping("/upload")
    public ResponseEntity<OcrRequest> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
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

        // OcrRequest 객체를 응답으로 반환
        return ResponseEntity.ok(ocrRequest);
    }
}