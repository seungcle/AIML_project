package com.group.receiptapp.service.receipt;

import com.group.receiptapp.domain.category.Category;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.domain.receipt.ReceiptImage;
import com.group.receiptapp.domain.receipt.ReceiptItem;
import com.group.receiptapp.dto.receipt.*;
import com.group.receiptapp.domain.receipt.Receipt;
import com.group.receiptapp.dto.ocr.OcrResponse;
import com.group.receiptapp.repository.category.CategoryRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.repository.receipt.ReceiptRepository;
import com.group.receiptapp.service.notification.NotificationService;
import com.group.receiptapp.service.ocr.OcrResultParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

@Slf4j
@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final OcrResultParser ocrResultParser;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;

    @Value("${receipt.image-path}")
    private String uploadDir;

    public ReceiptService(ReceiptRepository receiptRepository, MemberRepository memberRepository,
                          CategoryRepository categoryRepository, OcrResultParser ocrResultParser,
                          NotificationService notificationService, RestTemplate restTemplate) {
        this.receiptRepository = receiptRepository;
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
        this.ocrResultParser = ocrResultParser;
        this.notificationService = notificationService;
        this.restTemplate = restTemplate;
    }

    /*
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
                    receipt.getId(),
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
     */
    public List<ReceiptResponse> getReceiptsByMemberId(Long memberId) {
        // Repository에서 해당 유저의 영수증 조회
        List<Receipt> receipts = receiptRepository.findByMemberId(memberId);

        // Receipt 엔티티를 ReceiptResponse로 변환
        return receipts.stream()
                .map(ReceiptResponse::fromEntity)  // `fromEntity()` 메서드 사용
                .toList();
    }

    @Transactional
    public ReceiptResponse saveReceiptFromOcr(OcrResponse ocrResponse, Long memberId) {
        // OCR 결과 → ReceiptSaveRequest로 변환
        ReceiptSaveRequest request = ocrResultParser.parseOcrResponse(ocrResponse);

        // 사용자 ID 설정
        request.setMemberId(memberId);

        // 저장 처리 (기존 saveReceipt 로직 재사용)
        return saveReceipt(request);
    }

    @Transactional
    public ReceiptResponse saveReceipt(ReceiptSaveRequest request) {
        try {
            log.info("Parsed ReceiptSaveRequest: {}", request);
            log.info("Member ID: {}", request.getMemberId());
            log.info("Category ID: {}", request.getCategoryId());
            log.info("Items: {}", request.getItems());
            log.info("Image Path: {}", request.getImagePath());

            // 사용자 검증
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));


            // 그룹 검증 (멤버가 그룹에 속해 있는지 확인)
            if (member.getGroup() == null) {
                throw new IllegalStateException("회원이 그룹에 속해 있지 않습니다.");
            }
            Long groupId = member.getGroup().getId();

            Long categoryId = getCategoryFromAI(request.getStoreName(), request.getItems());
            log.info("API 응답 받은 카테고리 ID: {}", categoryId);
            request.setCategoryId(categoryId);

            // 카테고리 설정
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

            // 영수증 엔티티 생성
            Receipt receipt = new Receipt();
            receipt.setStoreName(request.getStoreName());
            receipt.setStoreAddress(request.getStoreAddress());
            receipt.setDate(request.getDate());
            log.info("✅ storeName: {}", request.getStoreName());
            log.info("✅ totalAmount: {}", request.getAmount());
            log.info("✅ date: {}", request.getDate());
            log.info("✅ amount set to receipt: {}", receipt.getAmount());
            receipt.setAmount(request.getAmount());
            log.info("✅ storeName: {}", request.getStoreName());
            log.info("✅ totalAmount: {}", request.getAmount());
            log.info("✅ date: {}", request.getDate());
            log.info("✅ amount set to receipt: {}", receipt.getAmount());
            receipt.setMemo(request.getMemo());
            receipt.setMember(member);
            receipt.setCategory(category);

            if (request.getImagePath() != null && !request.getImagePath().isBlank()) {
                ReceiptImage receiptImage = new ReceiptImage();
                receiptImage.setFilePath(request.getImagePath());

                log.info("ReceiptImage 생성됨 filePath: {}", receiptImage.getFilePath());

                receipt.setReceiptImage(receiptImage);
            } else {
                log.warn("imagePath가 null 혹은 blank: ReceiptImage 생성 생략");
            }

            if (receipt.getItems() == null) {
                receipt.setItems(new ArrayList<>());  // 리스트가 null일 경우 초기화
            }

            // 품목 리스트 추가
            request.getItems().forEach(itemRequest -> {
                ReceiptItem item = new ReceiptItem();
                item.setItemName(itemRequest.getItemName());
                item.setQuantity(itemRequest.getQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());
                item.setTotalPrice(itemRequest.getTotalPrice());
                item.setReceipt(receipt);  // 영수증 연관 관계 설정
                receipt.getItems().add(item);
            });

            // 데이터베이스에 저장
            receiptRepository.save(receipt);
            log.info("Receipt saved successfully with ID: {}", receipt.getId());

            // 같은 그룹 멤버에게 알림 생성 및 SSE 전송
            String message = String.format("'%s'님이 '%s' 영수증을 등록했습니다.", member.getName(), receipt.getStoreName());
            List<Map<String, Object>> notificationResults = notificationService.createGroupNotification(groupId, member.getId(), message);

            // 저장된 Receipt를 Response로 반환
            return ReceiptResponse.fromEntity(receipt, notificationResults);
        } catch (Exception e) {
            log.error("Error saving receipt: {}", e.getMessage(), e);
            return new ReceiptResponse("영수증 저장 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional
    public ReceiptResponse deleteReceipt(Long receiptId, Long memberId) {
        // 1. 영수증 조회
        Receipt receipt = receiptRepository.findByIdAndMemberId(receiptId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found or unauthorized access."));

        // 2. 사용자 검증
        if (!receipt.getMember().getId().equals(memberId)) {
            throw new SecurityException("You are not authorized to delete this receipt.");
        }

        // 3. 삭제 처리
        receipt.setDeletedAt(LocalDateTime.now());  // 삭제 시간 설정
        receipt.setIsDeleted(true);  // 삭제 상태 설정

        // 4. 변경된 엔티티를 저장 (영속성 컨텍스트에 의해 자동 저장)
        receiptRepository.save(receipt);

        // 5. 삭제된 영수증 정보 반환
        return ReceiptResponse.fromEntity(receipt);
    }

    public String saveImageToDisk(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path uploadPath = Paths.get("D:/custom_temp", uniqueFilename);

        Files.createDirectories(uploadPath.getParent());  // 디렉토리 없으면 생성
        Files.copy(file.getInputStream(), uploadPath);  // 파일 복사하여 저장

        if (!Files.exists(uploadPath)) {
            throw new IOException("파일 저장 실패: " + uploadPath);
        }

        log.info("File saved successfully: {}", uploadPath.toString());
        return uploadPath.toString();
    }


    public String getCategoryNameById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse("기타");  // 기본값 설정 가능
    }

    // 그룹-카테고리별 지출 내역 조회
    public Map<String, BigDecimal> getCategoryStats(Long groupId, int year, int month) {
        List<Object[]> results = receiptRepository.getCategoryStats(groupId, year, month);
        Map<String, BigDecimal> categoryStats = new HashMap<>();

        for (Object[] row : results) {
            String category = (String) row[0];  // 카테고리명
            BigDecimal totalSpent = (BigDecimal) row[1]; // 사용 금액
            categoryStats.put(category, totalSpent);
        }
        return categoryStats;
    }

    // 그룹-멤버별 지출 내역 조회
    public List<MemberSpendingResponse> getMemberSpending(Long groupId, int year, int month) {
        List<Object[]> results = receiptRepository.getMemberSpending(groupId, year, month);
        List<MemberSpendingResponse> spendingList = new ArrayList<>();

        for (Object[] row : results) {
            Long memberId = ((Number) row[0]).longValue();
            String memberName = (String) row[1];
            BigDecimal totalSpent = (BigDecimal) row[2];
            spendingList.add(new MemberSpendingResponse(memberId, memberName, totalSpent));
        }
        return spendingList;
    }

    // 그룹별 가장 많이 사용한 점포 조회 3개
    public List<TopStoreResponse> getTopStores(Long groupId, int year, int month) {
        List<Object[]> results = receiptRepository.getTopStoresByGroup(groupId, year, month);

        return results.stream().map(result -> new TopStoreResponse(
                (String) result[0], // storeName
                ((Number) result[1]).intValue(), // usageCount
                (BigDecimal) result[2] // totalSpent
        )).collect(Collectors.toList());
    }

    // 멤버별 총 지출과 카테고리별 지출 조회
    public MemberCategorySpendingResponse getMemberCategorySpending(Long memberId, int year, int month) {
        List<Object[]> results = receiptRepository.getMemberCategorySpending(memberId, year, month);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다. ID: " + memberId));

        Map<String, BigDecimal> categoryStats = results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],  // 카테고리명
                        result -> (BigDecimal) result[1] // 해당 카테고리의 총 지출 금액
                ));

        BigDecimal totalSpent = categoryStats.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MemberCategorySpendingResponse(memberId, member.getName(), totalSpent, categoryStats);
    }

    public List<MemberReceiptResponse> getReceiptsByMemberAndDate(Long memberId, int year, int month) {
        List<Object[]> results = receiptRepository.getReceiptsByMemberAndDate(memberId, year, month);

        return results.stream().map(result -> new MemberReceiptResponse(
                (Long) result[0],    // receiptId
                (String) result[1],  // storeName
                (String) result[2],  // storeAddress
                (String) result[3],  // memo
                (LocalDate) result[4], // date
                (BigDecimal) result[5], // amount
                (String) result[6]   // category
        )).collect(Collectors.toList());
    }

    public List<GroupReceiptSimpleResponse> getReceiptsByGroupAndDate(Long groupId, int year, int month) {
        List<Object[]> results = receiptRepository.getReceiptsByGroupAndDate(groupId, year, month);

        return results.stream().map(result -> new GroupReceiptSimpleResponse(
                (Long) result[0],    // receiptId
                (String) result[1],  // storeName
                (String) result[2],  // storeAddress
                (String) result[3],  // memo
                (LocalDate) result[4], // date
                (BigDecimal) result[5], // amount
                (String) result[6],  // memberName (사용자 이름)
                (String) result[7]   // categoryName (카테고리 이름)
        )).collect(Collectors.toList());
    }

    // 카테고리 분류 API
    private Long getCategoryFromAI(String storeName, List<ReceiptItemRequest> items) {
        try {
            // 요청 데이터 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("store_name", storeName);

            // 품목 리스트는 요청에서 제외 (필요 없는 경우)
            List<String> productNames = items.stream()
                    .map(ReceiptItemRequest::getItemName)
                    .collect(Collectors.toList());
            requestBody.put("product_names", productNames);

            // HTTP 요청 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://item-sorting-server.onrender.com/classify_receipt",
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("category")) {
                    String categoryName = (String) responseBody.get("category"); // 카테고리 이름 추출
                    log.info("AI 분류된 카테고리: {}", categoryName);
                    return mapCategoryNameToId(categoryName);
                }
            }

            log.warn("카테고리 분류 실패, 기본값 설정: 기타(1)");
            return 1L; // 기본값 사용
        } catch (Exception e) {
            log.error("카테고리 분류 호출 오류, 기본값 설정: {}", e.getMessage());
            return 1L; // 기본값 사용
        }
    }

    public Long getCategoryIdFromAI(String storeName, List<ReceiptItemRequest> items) {
        return getCategoryFromAI(storeName, items);
    }

    // name -> id
    private Long mapCategoryNameToId(String categoryName) {
        Map<String, Long> categoryMap = new HashMap<>();
        categoryMap.put("기타", 1L);
        categoryMap.put("식비", 2L);
        categoryMap.put("교통비", 3L);
        categoryMap.put("숙박비", 4L);
        categoryMap.put("전자기기", 5L);
        categoryMap.put("소모품비", 6L);

        return categoryMap.getOrDefault(categoryName, 1L); // 기본값: 기타(1)
    }

}