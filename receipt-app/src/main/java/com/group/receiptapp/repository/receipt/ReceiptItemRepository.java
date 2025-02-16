package com.group.receiptapp.repository.receipt;

import com.group.receiptapp.domain.receipt.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
    List<ReceiptItem> findByReceiptId(Long receiptId); // 특정 영수증의 품목 리스트 조회
}
