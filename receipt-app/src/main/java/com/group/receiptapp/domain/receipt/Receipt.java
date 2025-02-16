package com.group.receiptapp.domain.receipt;

import com.group.receiptapp.domain.category.Category;
import com.group.receiptapp.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipt")
@Getter
@Setter
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long id;

    @Column(name = "store_name", length = 255)
    private String storeName; // 점포 이름 (OCR 추출 데이터)

    @Column(name = "store_address", columnDefinition = "TEXT")
    private String storeAddress; // 점포 주소 (OCR 추출 데이터)

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo; // 영수증 메모

    @Column(name = "date", nullable = false)
    private LocalDate date; // 영수증 날짜

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // 지출 금액

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // 영수증 삭제 여부

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 삭제 시각 (선택 사항)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 영수증을 소유한 유저 (외래 키)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 영수증의 카테고리 (외래 키)

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ReceiptImage receiptImage; // 영수증 이미지와의 관계

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReceiptItem> items = new ArrayList<>(); // 품목 리스트

    public Long getImageId() {
        return receiptImage != null ? receiptImage.getId() : null; // receiptImage가 null이 아니면 그 id를 반환, null이면 null 반환
    }

    public Long getCategoryId() {
        return category != null ? category.getId() : null; // category가 null이 아니면 그 id를 반환, null이면 null 반환
    }

    public String getImagePath() {
        return receiptImage != null ? receiptImage.getFilePath() : null; // 이미지 경로 반환
    }

    public void setImagePath(String imagePath) {
        if (receiptImage == null) {
            receiptImage = new ReceiptImage(); // 이미지 엔티티가 없으면 생성
        }
        receiptImage.setFilePath(imagePath); // 이미지 경로 설정
    }
}
