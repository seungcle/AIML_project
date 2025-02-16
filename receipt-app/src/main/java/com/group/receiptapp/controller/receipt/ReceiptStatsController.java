package com.group.receiptapp.controller.receipt;

import com.group.receiptapp.dto.receipt.MemberCategorySpendingResponse;
import com.group.receiptapp.dto.receipt.MemberReceiptResponse;
import com.group.receiptapp.dto.receipt.MemberSpendingResponse;
import com.group.receiptapp.dto.receipt.TopStoreResponse;
import com.group.receiptapp.dto.receipt.GroupReceiptSimpleResponse;
import com.group.receiptapp.service.receipt.ExcelExportService;
import com.group.receiptapp.service.receipt.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/receipts/stats")
@RequiredArgsConstructor
@Slf4j
public class ReceiptStatsController {
    private final ReceiptService receiptService;
    private final ExcelExportService excelExportService;

    // 그룹별 카테고리별 지출 내역
    @GetMapping("/group/{groupId}/category/{year}/{month}")
    public ResponseEntity<Map<String, BigDecimal>> getCategoryStats(
            @PathVariable Long groupId, @PathVariable int year, @PathVariable int month) {
        Map<String, BigDecimal> stats = receiptService.getCategoryStats(groupId, year, month);
        return ResponseEntity.ok(stats);
    }

    // 그룹별 멤버별 지출 내역
    @GetMapping("/group/{groupId}/members/{year}/{month}")
    public ResponseEntity<List<MemberSpendingResponse>> getMemberSpending(
            @PathVariable Long groupId, @PathVariable int year, @PathVariable int month) {
        List<MemberSpendingResponse> response = receiptService.getMemberSpending(groupId, year, month);
        return ResponseEntity.ok(response);
    }

    // 그룹별 가장 많이 사용한 점포
    @GetMapping("/group/{groupId}/stores/{year}/{month}")
    public ResponseEntity<List<TopStoreResponse>> getTopStores(
            @PathVariable Long groupId, @PathVariable int year, @PathVariable int month) {
        List<TopStoreResponse> topStores = receiptService.getTopStores(groupId, year, month);
        return ResponseEntity.ok(topStores);
    }

    // 멤버별 연도/월별 카테고리별 지출 조회
    @GetMapping("/member/{memberId}/categories/{year}/{month}")
    public ResponseEntity<MemberCategorySpendingResponse> getMemberCategorySpending(
            @PathVariable Long memberId,
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(receiptService.getMemberCategorySpending(memberId, year, month));
    }

    // 멤버별 모든 영수증 조회
    @GetMapping("/member/{memberId}/receipts/{year}/{month}")
    public ResponseEntity<List<MemberReceiptResponse>> getReceiptsByMemberAndDate(
            @PathVariable Long memberId, @PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(receiptService.getReceiptsByMemberAndDate(memberId, year, month));
    }

    // 그룹 내 특정 연도/월 영수증 엑셀 다운로드
    @GetMapping("/group/{groupId}/export/{year}/{month}")
    public ResponseEntity<byte[]> exportGroupReceiptsToExcel(
            @PathVariable Long groupId,
            @PathVariable int year,
            @PathVariable int month) throws IOException {

        log.info("엑셀 다운로드 API 호출됨: groupId={}, year={}, month={}", groupId, year, month); // ✅ 디버깅 로그 추가

        List<GroupReceiptSimpleResponse> receiptResponses = receiptService.getReceiptsByGroupAndDate(groupId, year, month);
        log.info("조회된 영수증 개수: {}", receiptResponses.size());

        if (receiptResponses.isEmpty()) {
            log.warn("선택한 그룹의 영수증 데이터가 없음");
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=group_receipts_" + year + "_" + month + ".xlsx")
                    .body(new byte[0]);  // 빈 바이트 배열 반환
        }

        byte[] excelFile = excelExportService.generateGroupReceiptExcel(receiptResponses);
        log.info("엑셀 파일 변환 완료, 응답 반환 준비");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=group_receipts_" + year + "_" + month + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }
}
