package com.group.receiptapp.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MemberReceiptResponse {
    private Long receiptId;
    private String storeName;
    private String storeAddress;
    private String memo;
    private LocalDate date;
    private BigDecimal amount;
    private String category;
}