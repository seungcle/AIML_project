package com.group.receiptapp.dto.receipt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ReceiptItemRequest {
    private String itemName;    // 품목명
    private Integer quantity;   // 수량
    private BigDecimal unitPrice;  // 단가
    private BigDecimal totalPrice; // 총 가격

    public ReceiptItemRequest(String itemName, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}