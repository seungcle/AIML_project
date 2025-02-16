package com.group.receiptapp.dto.receipt;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceiptItemResponse {
    private String itemName;    // 품목명
    private Integer quantity;   // 수량
    private BigDecimal unitPrice;  // 단가
    private BigDecimal totalPrice; // 총 가격

    public ReceiptItemResponse(String itemName, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
