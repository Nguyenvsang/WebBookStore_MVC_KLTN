package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {

    PaymentStatus findByOrder(Order order);
}
