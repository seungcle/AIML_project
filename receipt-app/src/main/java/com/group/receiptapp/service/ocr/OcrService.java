package com.group.receiptapp.service.ocr;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.dto.ocr.OcrRequest;
import com.group.receiptapp.dto.receipt.ReceiptResponse;
import com.group.receiptapp.dto.receipt.ReceiptSaveRequest;
import com.group.receiptapp.dto.ocr.OcrResponse;
import com.group.receiptapp.security.JwtUtil;
import com.group.receiptapp.service.login.CustomUserDetailsService;
import com.group.receiptapp.service.receipt.ReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
@Slf4j
@Service
public class OcrService {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final OcrResultParser ocrResultParser;
    private final ReceiptService receiptService;

    @Value("${receipt.image-path}")
    private String uploadDir;

    private static final String CLOVA_OCR_URL = "https://djyhuy4mnd.apigw.ntruss.com/custom/v1/35910/c9c51c7e47ecae4852bf3de8a921c33abd6369260f7538fd01fe2f4cd5f90676/document/receipt";
    private static final String OCR_SECRET_KEY = "SHZ5cmNhZ2lnTlhPWEZ4WmdpdkJ4SVFIa0JvekRxemY=";

    public OcrService(RestTemplate restTemplate, JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService,
                      OcrResultParser ocrResultParser, ReceiptService receiptService) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.ocrResultParser = ocrResultParser;
        this.receiptService = receiptService;
    }

    public OcrResponse callClovaOcrApi(OcrRequest ocrRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", OCR_SECRET_KEY);

        HttpEntity<OcrRequest> entity = new HttpEntity<>(ocrRequest, headers);
        ResponseEntity<OcrResponse> responseEntity = restTemplate.postForEntity(CLOVA_OCR_URL, entity, OcrResponse.class);
        return responseEntity.getBody();
    }

    public ReceiptResponse processOcrAndSaveReceipt(MultipartFile file, String authorizationHeader) throws IOException {
        // 1. 인증된 사용자 확인
        String token = jwtUtil.resolveToken(authorizationHeader);
        String username = jwtUtil.extractUsername(token);
        Member member = customUserDetailsService.loadUserByUsernameAsMember(username);

        String imagePath = receiptService.saveImageToDisk(file);

        // 2. 파일 처리 및 OCR 요청 생성
        byte[] fileData = file.getBytes();
        String fileName = file.getOriginalFilename();
        String fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        OcrRequest.Image image = new OcrRequest.Image();
        image.setFormat(fileFormat);
        image.setName(fileName);
        image.setData(Base64.getEncoder().encodeToString(fileData));

        OcrRequest ocrRequest = new OcrRequest();
        ocrRequest.setVersion("V2");
        ocrRequest.setRequestId(UUID.randomUUID().toString());
        ocrRequest.setTimestamp(System.currentTimeMillis());
        ocrRequest.setImages(Collections.singletonList(image));

        // 3. 클로바 OCR API 호출
        OcrResponse ocrResponse = callClovaOcrApi(ocrRequest);
        if (ocrResponse == null || ocrResponse.getImages().isEmpty()) {
            log.error("OCR API returned null or empty images.");
            throw new RuntimeException("Invalid OCR response or empty images.");
        }
        log.info("OCR Response: {}", ocrResponse);  // OCR 응답 전체 로그 출력

        // 4. OCR 응답 파싱 및 DB 저장
        ReceiptSaveRequest receiptSaveRequest = ocrResultParser.parseOcrResponse(ocrResponse);
        receiptSaveRequest.setMemberId(member.getId());
        receiptSaveRequest.setImagePath(imagePath);
        return receiptService.saveReceipt(receiptSaveRequest);  // 저장 후 ReceiptResponse 반환
    }


    @Transactional
    public ReceiptResponse previewReceiptWithAuth(MultipartFile file, String authorizationHeader) throws IOException {
        String token = jwtUtil.resolveToken(authorizationHeader);
        if (token == null) {
            throw new SecurityException("JWT 토큰이 존재하지 않습니다.");
        }

        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // ✅ validateToken의 두 번째 인자 추가
        if (!jwtUtil.validateToken(token, userDetails)) {
            throw new SecurityException("유효하지 않은 JWT 토큰입니다.");
        }

        Member member = customUserDetailsService.loadUserByUsernameAsMember(username);

        // 파일 저장
        String imagePath = receiptService.saveImageToDisk(file);

        // OCR 처리
        byte[] fileData = file.getBytes();
        String fileName = file.getOriginalFilename();
        String fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        OcrRequest.Image image = new OcrRequest.Image();
        image.setFormat(fileFormat);
        image.setName(fileName);
        image.setData(Base64.getEncoder().encodeToString(fileData));

        OcrRequest ocrRequest = new OcrRequest();
        ocrRequest.setVersion("V2");
        ocrRequest.setRequestId(UUID.randomUUID().toString());
        ocrRequest.setTimestamp(System.currentTimeMillis());
        ocrRequest.setImages(Collections.singletonList(image));

        OcrResponse ocrResponse = callClovaOcrApi(ocrRequest);
        ReceiptSaveRequest parsed = ocrResultParser.parseOcrResponse(ocrResponse);

        Long categoryId = receiptService.getCategoryIdFromAI(parsed.getStoreName(), parsed.getItems());
        parsed.setCategoryId(categoryId);

        String categoryName = receiptService.getCategoryNameById(categoryId);

        ReceiptResponse response = new ReceiptResponse(parsed, categoryName);
        response.setUserId(member.getId());
        response.setFilePath(imagePath);

        return response;
    }

    public ReceiptResponse ocrOnly(MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        String fileName = file.getOriginalFilename();
        String fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // OCR 요청 준비
        OcrRequest.Image image = new OcrRequest.Image();
        image.setFormat(fileFormat);
        image.setName(fileName);
        image.setData(Base64.getEncoder().encodeToString(fileData));

        OcrRequest ocrRequest = new OcrRequest();
        ocrRequest.setVersion("V2");
        ocrRequest.setRequestId(UUID.randomUUID().toString());
        ocrRequest.setTimestamp(System.currentTimeMillis());
        ocrRequest.setImages(Collections.singletonList(image));

        // OCR 호출
        OcrResponse ocrResponse = callClovaOcrApi(ocrRequest);
        if (ocrResponse == null || ocrResponse.getImages().isEmpty()) {
            log.error("OCR API returned null or empty images.");
            throw new RuntimeException("Invalid OCR response or empty images.");
        }

        log.info("OCR Response: {}", ocrResponse);  // OCR 응답 전체 로그 출력

        // OCR 결과 파싱
        ReceiptSaveRequest parsed = ocrResultParser.parseOcrResponse(ocrResponse);

        // 💡 카테고리 자동 분류 포함
        Long categoryId = receiptService.getCategoryIdFromAI(parsed.getStoreName(), parsed.getItems());
        parsed.setCategoryId(categoryId);

        String categoryName = receiptService.getCategoryNameById(categoryId);


        // 응답에 categoryId 포함된 ReceiptResponse 생성
        return new ReceiptResponse(parsed, categoryName);
    }
}