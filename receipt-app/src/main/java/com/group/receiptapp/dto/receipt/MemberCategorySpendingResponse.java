package com.group.receiptapp.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@AllArgsConstructor
public class MemberCategorySpendingResponse {
    private Long memberId;
    private String memberName;
    private BigDecimal totalSpent;
    private Map<String, BigDecimal> categoryStats;
}
