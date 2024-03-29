package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoReturnOrderRepository extends JpaRepository<InfoReturnOrder, Integer> {
    InfoReturnOrder findByOrder(Order order);
}
