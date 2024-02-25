package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.entity.VideoReturnOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoReturnOrderRepository extends JpaRepository<VideoReturnOrder, Integer> {
    VideoReturnOrder findByInfoReturnOrder(InfoReturnOrder infoReturnOrder);
}
