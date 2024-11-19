package com.group.receiptapp.service.ocr;
import com.group.receiptapp.dto.receipt.ReceiptSaveRequest;
import com.group.receiptapp.dto.ocr.OcrResponse;
import com.group.receiptapp.service.receipt.ReceiptService;
import org.springframework.stereotype.Service;
@Service
public class OcrService {

    private final ReceiptService receiptService;
    private final OcrResultParser ocrResultParser;

    public OcrService(ReceiptService receiptService, OcrResultParser ocrResultParser) {
        this.receiptService = receiptService;
        this.ocrResultParser = ocrResultParser;
    }

    // OCR 결과를 처리하여 영수증을 저장
    public void processOcrResponse(OcrResponse ocrResponse, Long memberId) {
        // OCR 응답을 ReceiptSaveRequest로 파싱하여 영수증 저장
        receiptService.saveReceiptFromOcr(ocrResponse, memberId);  // OcrResponse와 memberId 전달
    }
}