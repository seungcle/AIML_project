package com.group.receiptapp.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MemberSpendingResponse {
    private Long memberId;
    private String memberName;
    private BigDecimal totalSpent;
}
