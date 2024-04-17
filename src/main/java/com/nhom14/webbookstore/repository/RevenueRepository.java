package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.Revenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    Revenue findByOrder(Order order);

    @Query("SELECT COALESCE(SUM(r.revenue), 0) FROM Revenue r WHERE r.date >= :start AND r.date < :end")
    double sumRevenueBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT r FROM Revenue r WHERE " +
            "(:revenueId is null or r.id = :revenueId) and " +
            "(:orderId is null or r.order.id = :orderId) and " +
            "(:revenue is null or r.revenue = :revenue) and " +
            "(:date is null or DATE(r.date) = :date) and " +
            "(:revenueMin is null or r.revenue >= :revenueMin) and " +
            "(:revenueMax is null or r.revenue <= :revenueMax)")
    Page<Revenue> findWithFilters(@Param("revenueId") Long revenueId,
                                  @Param("orderId") Integer orderId,
                                  @Param("revenue") Double revenue,
                                  @Param("date") LocalDate date,
                                  @Param("revenueMin") Double revenueMin,
                                  @Param("revenueMax") Double revenueMax,
                                  Pageable pageable);

}
