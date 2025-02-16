package com.group.receiptapp.domain.receipt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "receipt_item")
@Getter
@Setter
public class ReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id; // 품목 ID

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName; // 품목명

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice; // 단가

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice; // 총 금액

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt; // 영수증과의 다대일 관계
}
