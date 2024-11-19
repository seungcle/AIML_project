package com.group.receiptapp.dto.ocr;
import lombok.Data;
import java.util.List;


@Data
public class OcrResponse {
    private String version; // 버전 정보 (V2)
    private String requestId; // API 호출 UUID
    private long timestamp; // 호출 시각
    private List<Image> images; // 이미지 처리 결과 리스트

    @Data
    public static class Image {
        private String uid; // 영수증 이미지 UID
        private String name; // 영수증 이미지 이름
        private String inferResult; // 인식 결과 (SUCCESS, FAILURE, ERROR)
        private String message; // 결과 메시지
        private ValidationResult validationResult; // 유효성 검사 결과
        private Receipt receipt; // 영수증 세부 정보
    }

    @Data
    public static class ValidationResult {
        private String result; // 유효성 검사 결과 코드
        private String message; // 유효성 검사 결과 메시지
    }

    @Data
    public static class Receipt {
        private Meta meta; // 메타 정보
        private Result result; // 인식된 결과
    }

    @Data
    public static class Meta {
        private String estimatedLanguage; // OCR 추정 언어
    }

    @Data
    public static class Result {
        private StoreInfo storeInfo; // 점포 정보
        private PaymentInfo paymentInfo; // 결제 정보
        private List<SubResult> subResults; // 상품 그룹 정보
        private TotalPrice totalPrice; // 총 금액 정보
    }

    @Data
    public static class StoreInfo {
        private Info name; // 점포 이름
        private Info subName; // 지점 이름
        private Info bizNum; // 사업자 등록번호
        private List<Info> addresses; // 점포 주소
        private List<Info> tel; // 점포 전화번호
    }

    @Data
    public static class PaymentInfo {
        private Info date; // 결제 일자
        private Info time; // 결제 시간
        private CardInfo cardInfo; // 카드 정보
        private Info confirmNum; // 승인 번호
    }

    @Data
    public static class CardInfo {
        private Info company; // 카드사 정보
        private Info number; // 카드 번호
    }

    @Data
    public static class SubResult {
        private List<Item> items; // 상품 상세 정보
    }

    @Data
    public static class Item {
        private Info name; // 상품 이름
        private Info count; // 수량
        private Price price; // 가격 정보
    }

    @Data
    public static class Price {
        private Info price; // 가격
        private Info unitPrice; // 단가
    }

    @Data
    public static class TotalPrice {
        private Info price; // 총 금액 정보
    }

    // 공통적으로 사용하는 Info 클래스
    @Data
    public static class Info {
        private String text; // 인식된 텍스트
        private Formatted formatted; // 포맷된 텍스트
        private String keyText; // 키 값
        private Float confidenceScore; // 신뢰도
    }

    @Data
    public static class Formatted {
        private String year;    // 연도
        private String month;   // 월
        private String day;     // 일
        private String value;   // 포맷된 값 (이 값이 다른 정보일 수 있음)
    }
}