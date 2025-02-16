package com.group.receiptapp.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GroupReceiptSimpleResponse {
    private Long receiptId;      // 영수증 ID
    private String storeName;    // 점포명
    private String storeAddress; // 점포 주소
    private String memo;         // 메모
    private LocalDate date;      // 날짜
    private BigDecimal amount;   // 지출 금액
    private String memberName;   // 사용자 이름
    private String categoryName; // 카테고리 이름
}
