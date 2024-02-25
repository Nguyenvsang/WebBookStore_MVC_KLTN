package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.InfoCancelOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoCancelOrderRepository extends JpaRepository<InfoCancelOrder, Integer> {

}
