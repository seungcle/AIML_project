package com.group.receiptapp.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TopStoreResponse {
    private String storeName;  // 점포 이름
    private int usageCount;    // 사용 횟수
    private BigDecimal totalSpent;  // 총 사용 금액
}
