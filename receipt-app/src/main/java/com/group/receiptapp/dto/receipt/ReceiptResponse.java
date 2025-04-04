package com.group.receiptapp.dto.receipt;

import com.group.receiptapp.domain.receipt.Receipt;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Setter
@Getter
public class ReceiptResponse {
    // Getters and Setters

    private Long receiptId;            // 영수증 ID 추가
    private BigDecimal amount;          // 금액
    private Long categoryId;            // 카테고리 ID
    private LocalDate date;             // 날짜
    private LocalDateTime deletedAt;   // 삭제 시각
    private Boolean isDeleted;         // 삭제 여부
    private Long userId;               // 사용자 ID
    private String memo;               // 메모
    private String filePath;
    private String storeAddress;       // 상점 주소
    private String storeName;          // 상점 이름
    private List<ReceiptItemResponse> items; // 상품 리스트
    private List<Map<String, Object>> notificationResults; // 알림 전송 결과
    private String errorMessage; // 에러 메시지를 반환하기 위한 필드

    public ReceiptResponse(Receipt receipt, List<Map<String, Object>> notificationResults) {
        this.receiptId = receipt.getId();
        this.amount = receipt.getAmount();
        this.categoryId = receipt.getCategory().getId();
        this.date = receipt.getDate();
        this.deletedAt = receipt.getDeletedAt();
        this.isDeleted = receipt.getIsDeleted();
        this.userId = receipt.getMember().getId();
        this.memo = receipt.getMemo();
        this.filePath = receipt.getReceiptImage() != null ? receipt.getReceiptImage().getFilePath() : "이미지 없음";
        this.storeAddress = receipt.getStoreAddress();
        this.storeName = receipt.getStoreName();
        this.items = receipt.getItems().stream()
                .map(item -> new ReceiptItemResponse(
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .toList();
        this.notificationResults = notificationResults;
    }

    // 에러 메시지 생성자
    public ReceiptResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ReceiptResponse fromEntity(Receipt receipt, List<Map<String, Object>> notificationResults) {
        return new ReceiptResponse(receipt, notificationResults);
    }

    public static ReceiptResponse fromEntity(Receipt receipt) {
        return new ReceiptResponse(receipt, new ArrayList<>()); // 빈 알림 결과 리스트 포함
    }
}
