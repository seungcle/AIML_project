package com.group.receiptapp.dto.receipt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReceiptSaveRequest {
    private String storeName;     // 점포 이름
    private String storeAddress;  // 점포 주소
    private LocalDate date;       // 날짜
    private BigDecimal totalAmount; // 총 금액
    private String memo;          // 메모 (옵션)
    private Long memberId; // 이 데이터를 통해 해당 유저를 식별합니다.
    private Long categoryId;      // 카테고리 ID
    private List<ReceiptItemRequest> items = new ArrayList<>();
    private String imagePath;

    public ReceiptSaveRequest() {
        this.items = new ArrayList<>();
    }

    @JsonCreator
    public ReceiptSaveRequest(
            @JsonProperty("storeName") String storeName,
            @JsonProperty("storeAddress") String storeAddress,
            @JsonProperty("date") LocalDate date,
            @JsonProperty("totalAmount") BigDecimal totalAmount,
            @JsonProperty("memo") String memo,
            @JsonProperty("memberId") Long memberId,
            @JsonProperty("categoryId") Long categoryId,
            @JsonProperty("items") List<ReceiptItemRequest> items,
            @JsonProperty("imagePath") String imagePath
    ) {
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.date = date;
        this.totalAmount = totalAmount;
        this.memo = memo;
        this.memberId = memberId;
        this.categoryId = categoryId;
        this.items = items == null ? new ArrayList<>() : items;  // null일 경우
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<ReceiptItemRequest> getItems() {
        return items;
    }

    public void setItems(List<ReceiptItemRequest> items) {
        this.items = items;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}