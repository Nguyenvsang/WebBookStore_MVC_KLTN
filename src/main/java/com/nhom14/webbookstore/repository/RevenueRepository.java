package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    Revenue findByOrder(Order order);

    @Query("SELECT COALESCE(SUM(r.revenue), 0) FROM Revenue r WHERE r.date >= :start AND r.date < :end")
    double sumRevenueBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
