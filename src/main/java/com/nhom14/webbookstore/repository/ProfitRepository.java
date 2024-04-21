package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.OrderItem;
import com.nhom14.webbookstore.entity.Profit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProfitRepository extends JpaRepository<Profit, Long> {
    Profit findByOrderItem(OrderItem orderItem);

    List<Profit> findByDateBetween(Timestamp start, Timestamp end);

    @Query("SELECT COALESCE(SUM(p.profit), 0) FROM Profit p WHERE p.date >= :start AND p.date < :end")
    double sumProfitBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM Profit p WHERE " +
            "(:profitId is null or p.id = :profitId) and " +
            "(:orderitemId is null or p.orderItem.id = :orderitemId) and " +
            "(:costPrice is null or p.costPrice = :costPrice) and " +
            "(:sellPrice is null or p.sellPrice = :sellPrice) and " +
            "(:profit is null or p.profit = :profit) and " +
            "(:date is null or DATE(p.date) = :date) and " +
            "(:profitMin is null or p.profit >= :profitMin) and " +
            "(:profitMax is null or p.profit <= :profitMax)")
    Page<Profit> findWithFilters(@Param("profitId") Long profitId,
                                 @Param("orderitemId") Integer orderitemId,
                                 @Param("costPrice") Double costPrice,
                                 @Param("sellPrice") Double sellPrice,
                                 @Param("profit") Double profit,
                                 @Param("date") LocalDate date,
                                 @Param("profitMin") Double profitMin,
                                 @Param("profitMax") Double profitMax,
                                 Pageable pageable);

}
