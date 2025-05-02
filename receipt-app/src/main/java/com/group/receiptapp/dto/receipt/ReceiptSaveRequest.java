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

    private String storeName;         // 점포 이름
    private String storeAddress;      // 점포 주소
    private LocalDate date;           // 영수증 날짜
    private BigDecimal amount;   // 총 금액
    private String memo;              // 메모 (선택 사항)
    private Long memberId;            // 유저 식별용 ID
    private Long categoryId;          // 카테고리 ID
    private List<ReceiptItemRequest> items = new ArrayList<>();  // 품목 리스트
    private String imagePath;         // 이미지 경로

    public ReceiptSaveRequest() {
        this.items = new ArrayList<>();
    }
}
