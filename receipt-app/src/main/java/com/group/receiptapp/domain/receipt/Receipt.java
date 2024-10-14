package com.group.receiptapp.domain.receipt;

import com.group.receiptapp.domain.category.Category;
import com.group.receiptapp.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name= "receipt")
@Getter @Setter
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="receipt_id")
    private Long id;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo; // 영수증 메모

    @Column(name = "date", nullable = false)
    private LocalDate date; // 영수증 날짜

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // 지출 금액

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // 영수증 삭제 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 영수증을 소유한 유저 (외래 키)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 영수증의 카테고리 (외래 키)
}
