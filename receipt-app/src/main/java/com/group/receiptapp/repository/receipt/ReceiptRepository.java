package com.group.receiptapp.repository.receipt;

import com.group.receiptapp.domain.receipt.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    // 전체 영수증 조회
    List<Receipt> findByMemberId(Long memberId);

    // 특정 영수증 조회
    Optional<Receipt> findByIdAndMemberId(Long receiptId, Long memberId);

    // 그룹별 카테고리별 지출 내역 조회
    @Query("SELECT c.name, SUM(r.amount) " +
            "FROM Receipt r " +
            "JOIN r.category c " +
            "JOIN r.member m " +
            "WHERE m.group.id = :groupId " +
            "AND YEAR(r.date) = :year " +
            "AND MONTH(r.date) = :month " +
            "GROUP BY c.name")
    List<Object[]> getCategoryStats(@Param("groupId") Long groupId,
                                    @Param("year") int year,
                                    @Param("month") int month);

    // 그룹별 멤버별 지출 내역 조회
    @Query("SELECT m.id, m.name, SUM(r.amount) " +
            "FROM Receipt r " +
            "JOIN r.member m " +
            "WHERE m.group.id = :groupId " +
            "AND YEAR(r.date) = :year " +
            "AND MONTH(r.date) = :month " +
            "GROUP BY m.id, m.name")
    List<Object[]> getMemberSpending(@Param("groupId") Long groupId,
                                     @Param("year") int year,
                                     @Param("month") int month);

    // 그룹별 가장 많이 사용한 점포 조회 (Top 3)
    @Query("SELECT r.storeName, COUNT(r.id), SUM(r.amount) FROM Receipt r " +
            "WHERE r.member.group.id = :groupId AND YEAR(r.date) = :year AND MONTH(r.date) = :month " +
            "GROUP BY r.storeName " +
            "ORDER BY COUNT(r.id) DESC, SUM(r.amount) DESC")
    List<Object[]> getTopStoresByGroup(@Param("groupId") Long groupId,
                                       @Param("year") int year,
                                       @Param("month") int month);

    // 멤버별 총 지출과 카테고리별 지출 조회
    @Query("SELECT r.category.name, SUM(r.amount) FROM Receipt r " +
            "WHERE r.member.id = :memberId " +
            "AND YEAR(r.date) = :year " +
            "AND MONTH(r.date) = :month " +
            "GROUP BY r.category.name")
    List<Object[]> getMemberCategorySpending(
            @Param("memberId") Long memberId,
            @Param("year") int year,
            @Param("month") int month);

    // 멤버별 모든 영수증 조회
    @Query("SELECT r.id, r.storeName, r.storeAddress, r.memo, r.date, r.amount, r.category.name " +
            "FROM Receipt r WHERE r.member.id = :memberId " +
            "AND YEAR(r.date) = :year " +
            "AND MONTH(r.date) = :month " +
            "ORDER BY r.date DESC")
    List<Object[]> getReceiptsByMemberAndDate(
            @Param("memberId") Long memberId,
            @Param("year") int year,
            @Param("month") int month);

    // 특정 그룹의 연도/월별 영수증 조회
    @Query("SELECT r.id, r.storeName, r.storeAddress, r.memo, r.date, r.amount, " +
            "r.member.name, r.category.name FROM Receipt r " +
            "WHERE r.member.group.id = :groupId " +
            "AND YEAR(r.date) = :year " +
            "AND MONTH(r.date) = :month " +
            "ORDER BY r.date DESC")
    List<Object[]> getReceiptsByGroupAndDate(
            @Param("groupId") Long groupId,
            @Param("year") int year,
            @Param("month") int month);

}