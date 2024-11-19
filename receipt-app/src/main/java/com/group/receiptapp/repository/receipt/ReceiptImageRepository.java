package com.group.receiptapp.repository.receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import com.group.receiptapp.domain.receipt.ReceiptImage;

public interface ReceiptImageRepository extends JpaRepository<ReceiptImage, Long> {
}