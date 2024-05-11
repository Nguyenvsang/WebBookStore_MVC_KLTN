package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.BookImport;
import com.nhom14.webbookstore.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d WHERE " +
            "(:bookId is null or d.book.id = :bookId) and " +
            "(:status is null or d.status = :status) and " +
            "(:searchKeyword is null or (cast(d.book.id as string) like lower(concat('%', :searchKeyword,'%')) or lower(d.book.name) like lower(concat('%', :searchKeyword,'%'))))")
    Page<Discount> findWithFilters(@Param("bookId") Integer bookId,
                                   @Param("searchKeyword") String searchKeyword,
                                   @Param("status") Integer status,
                                   Pageable pageable);

    @Query("SELECT d FROM Discount d WHERE " +
            "d.book.id = :bookId and " +
            "d.status = 1 and " +
            "d.startDate <= :now and " +
            "d.endDate >= :now " +
            "ORDER BY d.startDate DESC")
    List<Discount> findActiveDiscountsByBookId(@Param("bookId") Integer bookId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT d FROM Discount d WHERE " +
            "d.book.id = :bookId and " +
            "((d.startDate <= :startDate and d.endDate >= :startDate) or " +
            "(d.startDate <= :endDate and d.endDate >= :endDate))")
    List<Discount> findOverlappingDiscounts(@Param("bookId") Integer bookId,
                                            @Param("startDate") Timestamp startDate,
                                            @Param("endDate") Timestamp endDate);
}
