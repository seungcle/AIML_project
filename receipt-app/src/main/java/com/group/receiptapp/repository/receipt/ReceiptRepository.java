package com.group.receiptapp.repository.receipt;


import com.group.receiptapp.domain.receipt.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    // 유저 ID로 영수증 조회
    List<Receipt> findByMemberId(Long memberId);
}