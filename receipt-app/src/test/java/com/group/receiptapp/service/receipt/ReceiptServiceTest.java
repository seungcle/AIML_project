package com.group.receiptapp.service.receipt;

import com.group.receiptapp.domain.category.Category;
import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.member.Member;
import com.group.receiptapp.domain.receipt.Receipt;

import com.group.receiptapp.dto.receipt.ReceiptSaveRequest;
import com.group.receiptapp.repository.category.CategoryRepository;
import com.group.receiptapp.repository.member.MemberRepository;
import com.group.receiptapp.repository.receipt.ReceiptRepository;
import com.group.receiptapp.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ReceiptService receiptService;
    private Receipt receipt;
    private ReceiptSaveRequest request;

    @BeforeEach
    void setUp() {
        receipt = new Receipt();

        request = new ReceiptSaveRequest();
        request.setMemberId(1L);
        request.setCategoryId(1L);
        request.setStoreName("Test Store");
        request.setStoreAddress("Test Address");
        request.setDate(LocalDate.now()); // LocalDate 사용
        request.setTotalAmount(BigDecimal.valueOf(1000)); // BigDecimal 직접 설정

        // Receipt에 데이터 설정
        receipt.setDate(request.getDate());
        receipt.setAmount(request.getTotalAmount()); // 변환 없이 바로 설정
    }

    @Test
    void saveReceipt_shouldCreateNotificationsForGroupMembers() {
        // Mock Group 생성
        Group mockGroup = new Group();
        mockGroup.setId(1L);
        mockGroup.setName("Test Group");

        // Mock Member 생성
        Member mockMember = new Member();
        mockMember.setId(1L);
        mockMember.setName("Test User");
        mockMember.setGroup(mockGroup); // Group 설정

        // Mock Receipt 생성
        Receipt mockReceipt = new Receipt();
        mockReceipt.setId(1L);
        mockReceipt.setStoreName("Test Store");

        // Mock Group Members 생성
        Member groupMember1 = new Member();
        groupMember1.setId(2L);
        groupMember1.setName("Group Member 1");
        groupMember1.setGroup(mockGroup); // Group 설정

        Member groupMember2 = new Member();
        groupMember2.setId(3L);
        groupMember2.setName("Group Member 2");
        groupMember2.setGroup(mockGroup); // Group 설정

        // Mock Category 생성
        Category mockCategory = new Category(); // Category 객체 생성
        mockCategory.setId(1L);
        mockCategory.setName("Test Category");

        // Mock 동작 설정
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(receiptRepository.save(any(Receipt.class))).thenReturn(mockReceipt);
        when(memberRepository.findByGroupId(mockGroup.getId())).thenReturn(List.of(mockMember, groupMember1, groupMember2));

        // 메서드 호출
        receiptService.saveReceipt(request);

        // 알림 생성 호출 검증
        verify(notificationService, times(2))
                .createGroupNotification(eq(mockGroup.getId()), anyLong(), anyString());

        verify(notificationService).createGroupNotification(mockGroup.getId(), 2L,
                "'Test User'님이 'Test Store' 영수증을 등록했습니다.");
        verify(notificationService).createGroupNotification(mockGroup.getId(), 3L,
                "'Test User'님이 'Test Store' 영수증을 등록했습니다.");
    }
}
