package com.group.receiptapp.service.ocr;
import com.group.receiptapp.dto.receipt.ReceiptItemRequest;
import com.group.receiptapp.dto.receipt.ReceiptSaveRequest;
import com.group.receiptapp.dto.ocr.OcrResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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
                        String year = dateFormatted.getYear();
                        String month = dateFormatted.getMonth();
                        String day = dateFormatted.getDay();
                        String dateString = year + "-" + month + "-" + day; // "yyyy-MM-dd" 형식
                        try {
                            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            request.setDate(date);  // 날짜 설정
                        } catch (DateTimeParseException e) {
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
                                BigDecimal totalAmount = new BigDecimal(totalAmountText);
                                request.setAmount(totalAmount);
                            } catch (NumberFormatException e) {
                                log.error("Total amount parsing failed: {}", totalAmountText, e);
                            }
                        });

                // **품목 리스트 파싱**
                List<ReceiptItemRequest> items = new ArrayList<>();
                Optional.ofNullable(receipt.getResult().getSubResults())
                        .ifPresent(subResults -> subResults.forEach(subResult -> {
                            Optional.ofNullable(subResult.getItems())
                                    .ifPresent(subItems -> subItems.forEach(item -> {
                                        String itemName = Optional.ofNullable(item.getName())
                                                .map(name -> name.getText())
                                                .orElse("Unknown Item");
                                        int quantity = Integer.parseInt(Optional.ofNullable(item.getCount())
                                                .map(count -> count.getFormatted().getValue())
                                                .orElse("1"));
                                        BigDecimal unitPrice = new BigDecimal(Optional.ofNullable(item.getPrice().getUnitPrice())
                                                .map(price -> price.getFormatted().getValue())
                                                .orElse("0"));
                                        BigDecimal totalPrice = new BigDecimal(Optional.ofNullable(item.getPrice().getPrice())
                                                .map(price -> price.getFormatted().getValue())
                                                .orElse("0"));

                                        ReceiptItemRequest itemRequest = new ReceiptItemRequest(itemName, quantity, unitPrice, totalPrice);
                                        items.add(itemRequest);
                                    }));
                        }));

                request.setItems(items);  // 품목 리스트 추가
            }
        }

        return request;
    }
}