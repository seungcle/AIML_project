package com.group.receiptapp.service.receipt;

import com.group.receiptapp.dto.receipt.GroupReceiptSimpleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ExcelExportService {
    public byte[] generateGroupReceiptExcel(List<GroupReceiptSimpleResponse> receiptResponses) throws IOException {
        log.info("엑셀 파일 생성 시작");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("그룹 영수증 내역");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("영수증 ID");
        headerRow.createCell(1).setCellValue("점포명");
        headerRow.createCell(2).setCellValue("점포 주소");
        headerRow.createCell(3).setCellValue("메모");
        headerRow.createCell(4).setCellValue("날짜");
        headerRow.createCell(5).setCellValue("지출금액");
        headerRow.createCell(6).setCellValue("사용자");
        headerRow.createCell(7).setCellValue("카테고리(이름)");
        log.info("엑셀 헤더 생성 완료. 데이터 입력 시작...");
        // 데이터 삽입
        int rowNum = 1;
        for (GroupReceiptSimpleResponse response : receiptResponses) {
            log.info("데이터 입력 중: {}", response);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(response.getReceiptId());
            row.createCell(1).setCellValue(response.getStoreName());
            row.createCell(2).setCellValue(response.getStoreAddress());
            row.createCell(3).setCellValue(response.getMemo());
            row.createCell(4).setCellValue(response.getDate().toString());
            row.createCell(5).setCellValue(response.getAmount().doubleValue());
            row.createCell(6).setCellValue(response.getMemberName());
            row.createCell(7).setCellValue(response.getCategoryName());
        }
        log.info("엑셀 데이터 입력 완료/ 파일 생성 중");

        // 파일을 ByteArray로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        log.info("엑셀 파일 생성 완료");

        return outputStream.toByteArray();
    }
}
