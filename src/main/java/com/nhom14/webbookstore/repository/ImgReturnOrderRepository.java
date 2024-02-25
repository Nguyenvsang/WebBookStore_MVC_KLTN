package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.ImgReturnOrder;
import com.nhom14.webbookstore.entity.InfoReturnOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgReturnOrderRepository extends JpaRepository<ImgReturnOrder, Integer> {
    ImgReturnOrder findByInfoReturnOrder(InfoReturnOrder infoReturnOrder);
}
