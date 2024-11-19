package com.group.receiptapp.dto.receipt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReceiptResponse {
    private BigDecimal amount;          // 금액
    private Long categoryId;            // 카테고리 ID
    private LocalDate date;             // 날짜
    private LocalDateTime deletedAt;   // 삭제 시각
    private Boolean isDeleted;         // 삭제 여부
    private Long userId;               // 사용자 ID
    private String memo;               // 메모
    private Long imageId;              // 이미지 ID
    private String storeAddress;       // 상점 주소
    private String storeName;          // 상점 이름

    // 추가: 에러 메시지를 반환하기 위한 필드
    private String errorMessage;

    public ReceiptResponse(BigDecimal amount, Long categoryId, LocalDate date, LocalDateTime deletedAt,
                           Boolean isDeleted, Long userId, String memo, Long imageId, String storeAddress, String storeName) {
        this.amount = amount;
        this.categoryId = categoryId;
        this.date = date;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;
        this.userId = userId;
        this.memo = memo;
        this.imageId = imageId;
        this.storeAddress = storeAddress;
        this.storeName = storeName;
    }

    // 에러 메시지 생성자
    public ReceiptResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Getters and Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }

    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

}
