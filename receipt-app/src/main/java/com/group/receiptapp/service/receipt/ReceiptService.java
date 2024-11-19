package com.group.receiptapp.service.receipt;

import com.group.receiptapp.domain.category.Category;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.dto.receipt.ReceiptResponse;
import com.group.receiptapp.dto.receipt.ReceiptSaveRequest;
import com.group.receiptapp.domain.receipt.Receipt;
import com.group.receiptapp.dto.ocr.OcrResponse;
import com.group.receiptapp.repository.category.CategoryRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.repository.receipt.ReceiptRepository;
import com.group.receiptapp.service.ocr.OcrResultParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final OcrResultParser ocrResultParser;

    public ReceiptService(ReceiptRepository receiptRepository, MemberRepository memberRepository, CategoryRepository categoryRepository, OcrResultParser ocrResultParser) {
        this.receiptRepository = receiptRepository;
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
        this.ocrResultParser = ocrResultParser;
    }

    @Transactional
    public ReceiptResponse saveReceiptFromOcr(OcrResponse ocrResponse, Long memberId) {
        try {
            // OCR 결과를 ReceiptSaveRequest로 파싱
            ReceiptSaveRequest request = ocrResultParser.parseOcrResponse(ocrResponse);
            log.info("Parsed OCR result into ReceiptSaveRequest: {}", request);

            // 현재 사용자 로드
            if (memberId == null) {
                throw new IllegalArgumentException("Member ID must not be null");
            }
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

            // 카테고리 ID가 null일 때 무조건 1로 설정
            request.setCategoryId(1L); // 강제로 categoryId를 1로 설정

            // 카테고리 로드
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

            // 로그 추가: Member와 Category 객체 확인
            log.info("Saving receipt for member: {}", member.getName());
            log.info("Receipt category: {}", category.getName());

            // Receipt 엔티티 생성
            Receipt receipt = new Receipt();
            receipt.setStoreName(request.getStoreName());
            receipt.setStoreAddress(request.getStoreAddress());
            receipt.setDate(request.getDate());
            receipt.setAmount(request.getTotalAmount());
            receipt.setMemo(request.getMemo());
            receipt.setMember(member);
            receipt.setCategory(category);

            // 데이터베이스에 저장
            receiptRepository.save(receipt);
            log.info("Receipt saved successfully with ID: {}", receipt.getId());

            // 저장된 Receipt를 ReceiptResponse로 변환하여 반환
            return new ReceiptResponse(
                    receipt.getAmount(),
                    receipt.getCategory().getId(),
                    receipt.getDate(),
                    receipt.getDeletedAt(),
                    receipt.getIsDeleted(),
                    receipt.getMember().getId(),
                    receipt.getMemo(),
                    receipt.getImageId(),
                    receipt.getStoreAddress(),
                    receipt.getStoreName()
            );
        } catch (Exception e) {
            log.error("Error saving receipt: {}", e.getMessage(), e);
            throw e; // 예외를 던져서 트랜잭션 롤백을 유도
        }
    }
}