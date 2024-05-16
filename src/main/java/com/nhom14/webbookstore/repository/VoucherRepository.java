package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Voucher findByCode(String code);

    @Query("SELECT v FROM Voucher v WHERE " +
            "(:searchKeywordAsInteger is null or v.id = :searchKeywordAsInteger) and " +
            "(:categoryId is null or v.category.id = :categoryId) and " +
            "(:voucherScope is null or v.voucherScope = :voucherScope) and " +
            "(:status is null or v.status = :status) and " +
            "(:searchKeyword is null or lower(v.code) like lower(concat('%', :searchKeyword,'%')))")
    Page<Voucher> findWithFilters(@Param("categoryId") Integer categoryId,
                                  @Param("searchKeyword") String searchKeyword,
                                  @Param("searchKeywordAsInteger") Integer searchKeywordAsInteger,
                                  @Param("voucherScope") Integer voucherScope,
                                  @Param("status") Integer status,
                                  Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE " +
            "v.category.id = :categoryId and " +
            "v.voucherScope = 1 and " +
            "v.startDate <= :now and " +
            "v.endDate >= :now " +
            "ORDER BY v.startDate DESC")
    List<Voucher> findActiveVouchersByCategoryId(@Param("categoryId") Integer categoryId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE " +
            "v.category.id = :categoryId and " +
            "(v.startDate <= :startDate and v.endDate >= :startDate) or " +
            "(v.startDate <= :endDate and v.endDate >= :endDate)")
    List<Voucher> findOverlappingVouchers(@Param("categoryId") Integer categoryId,
                                          @Param("startDate") Timestamp startDate,
                                          @Param("endDate") Timestamp endDate);

    @Query("SELECT v FROM Voucher v WHERE " +
            "v.status = 1 and " +
            "v.remainingQuantity > 0 and " +
            "v.startDate <= :now and " +
            "v.endDate >= :now")
    List<Voucher> findActiveVouchers(@Param("now") LocalDateTime now);
}
