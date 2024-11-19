package com.group.receiptapp.dto.ocr;

import lombok.Data;

import java.util.List;

@Data
public class OcrRequest {
    private String version; // API 버전
    private String requestId; // 고유 요청 ID
    private long timestamp; // 요청 시각
    private List<Image> images; // 이미지 데이터 리스트

    @Data
    public static class Image {
        private String format; // 이미지 형식 (jpg, jpeg, png 등)
        private String name; // 이미지 이름
        private String data; // Base64 인코딩된 이미지 데이터
    }
}
