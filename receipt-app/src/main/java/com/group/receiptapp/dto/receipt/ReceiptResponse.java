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
    private String categoryName;
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
    private Boolean duplicate;

    public ReceiptResponse(Receipt receipt, List<Map<String, Object>> notificationResults) {
        this.receiptId = receipt.getId();
        this.amount = receipt.getAmount();
        this.categoryId = receipt.getCategory().getId();
        this.categoryName = receipt.getCategory() != null ? receipt.getCategory().getName() : null;
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
        this.duplicate = false;
    }

    public ReceiptResponse(ReceiptSaveRequest request, String categoryName) {
        this.storeName = request.getStoreName();
        this.storeAddress = request.getStoreAddress();
        this.date = request.getDate();
        this.amount = request.getAmount();
        this.memo = request.getMemo();
        this.filePath = request.getImagePath();
        this.categoryId = request.getCategoryId();
        this.categoryName = categoryName;

        this.items = request.getItems().stream()
                .map(ReceiptItemResponse::new)
                .toList();
        this.duplicate = false;
    }

    public ReceiptResponse(String errorMessage, boolean duplicate) {
        this.errorMessage = errorMessage;
        this.duplicate = duplicate;
    }

    public static ReceiptResponse fromEntity(Receipt receipt, List<Map<String, Object>> notificationResults) {
        return new ReceiptResponse(receipt, notificationResults);
    }

    public static ReceiptResponse fromEntity(Receipt receipt) {
        ReceiptResponse response = new ReceiptResponse(receipt, new ArrayList<>());
        response.setDuplicate(false);
        return response;
    }
}
