package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.OrderItem;
import com.nhom14.webbookstore.entity.Profit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProfitRepository extends JpaRepository<Profit, Long> {
    Profit findByOrderItem(OrderItem orderItem);

    List<Profit> findByDateBetween(Timestamp start, Timestamp end);

    @Query("SELECT COALESCE(SUM(p.profit), 0) FROM Profit p WHERE p.date >= :start AND p.date < :end")
    double sumProfitBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
