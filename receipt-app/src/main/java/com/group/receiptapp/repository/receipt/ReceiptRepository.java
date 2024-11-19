package com.group.receiptapp.repository.receipt;


import com.group.receiptapp.domain.receipt.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}