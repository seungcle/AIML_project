package com.group.receiptapp.service.ocr;
import com.group.receiptapp.dto.receipt.ReceiptSaveRequest;
import com.group.receiptapp.dto.ocr.OcrResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Slf4j
@Service
public class OcrResultParser {

    public ReceiptSaveRequest parseOcrResponse(OcrResponse ocrResponse) {
        ReceiptSaveRequest request = new ReceiptSaveRequest();

        // 첫 번째 이미지에 대한 처리
        if (ocrResponse.getImages() != null && !ocrResponse.getImages().isEmpty()) {
            var receipt = ocrResponse.getImages().get(0).getReceipt();

            if (receipt != null && receipt.getResult() != null) {
                // 점포 이름
                Optional.ofNullable(receipt.getResult().getStoreInfo())
                        .map(storeInfo -> storeInfo.getName())
                        .map(name -> name.getText())
                        .ifPresent(request::setStoreName);

                // 점포 주소 (최초의 주소 정보)
                Optional.ofNullable(receipt.getResult().getStoreInfo())
                        .map(storeInfo -> storeInfo.getAddresses())
                        .filter(addresses -> !addresses.isEmpty())
                        .map(addresses -> addresses.get(0).getText())
                        .ifPresent(request::setStoreAddress);

                // 날짜 파싱
                if (receipt.getResult().getPaymentInfo() != null && receipt.getResult().getPaymentInfo().getDate() != null) {
                    var dateFormatted = receipt.getResult().getPaymentInfo().getDate().getFormatted();
                    if (dateFormatted != null) {
                        // formatted 객체에서 year, month, day 직접 접근
                        String year = dateFormatted.getYear();
                        String month = dateFormatted.getMonth();
                        String day = dateFormatted.getDay();

                        // 날짜를 문자열로 결합하여 LocalDate 생성
                        String dateString = year + "-" + month + "-" + day; // "yyyy-MM-dd" 형식
                        try {
                            // 날짜 파싱 시 예외 처리
                            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            request.setDate(date);  // 날짜 설정
                        } catch (DateTimeParseException e) {
                            // 날짜 파싱 예외 처리
                            log.error("Date parsing failed for date string: {}", dateString, e);
                        }
                    }
                }

                // 총 금액 파싱
                Optional.ofNullable(receipt.getResult().getTotalPrice())
                        .map(totalPrice -> totalPrice.getPrice())
                        .map(price -> price.getFormatted().getValue())
                        .ifPresent(totalAmountText -> {
                            try {
                                // 금액 파싱 시 예외 처리
                                BigDecimal totalAmount = new BigDecimal(totalAmountText);
                                request.setTotalAmount(totalAmount);
                            } catch (NumberFormatException e) {
                                // 금액 파싱 예외 처리
                                log.error("Total amount parsing failed: {}", totalAmountText, e);
                            }
                        });
            }
        }

        return request;
    }
}