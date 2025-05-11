package com.group.receiptapp.service.receipt;

import com.group.receiptapp.dto.receipt.ReceiptSaveRequest;
import com.group.receiptapp.repository.group.GroupRepository;
import com.group.receiptapp.repository.receipt.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiptCheckService {
    private final ReceiptRepository receiptRepository;
    private final GroupRepository groupRepository;

    public void validateDuplicate(ReceiptSaveRequest request, Long groupId) {
        // 그룹 중복 방지 설정이 false면 검사하지 않음
        boolean isPreventEnabled = groupRepository.findById(groupId)
                .map(group -> Boolean.TRUE.equals(group.isPreventDuplicateReceipt()))
                .orElse(false);

        if (!isPreventEnabled) {
            return;  // 설정 꺼져 있음 → 중복 체크 건너뜀
        }

        boolean isDuplicate = receiptRepository.existsDuplicate(
                request.getDate(),
                request.getAmount(),
                groupId
        );

        if (isDuplicate) {
            throw new IllegalStateException("같은 그룹 내 동일 날짜와 금액의 영수증이 이미 존재합니다.");
        }
    }
}