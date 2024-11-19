package com.group.receiptapp.domain.receipt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "receipt_image")
@Getter
@Setter
public class ReceiptImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath; // 로컬 저장 경로 또는 클라우드 스토리지 URL
}
