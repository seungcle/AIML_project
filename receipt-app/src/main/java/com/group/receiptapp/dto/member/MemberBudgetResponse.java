package com.group.receiptapp.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberBudgetResponse {
    private Long memberId;
    private String name;
    private String email;
    private BigDecimal budget;
}