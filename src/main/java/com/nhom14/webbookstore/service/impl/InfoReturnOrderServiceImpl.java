package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.repository.InfoReturnOrderRepository;
import com.nhom14.webbookstore.service.InfoReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfoReturnOrderServiceImpl implements InfoReturnOrderService {

    private InfoReturnOrderRepository infoReturnOrderRepository;

    @Autowired
    public InfoReturnOrderServiceImpl(InfoReturnOrderRepository infoReturnOrderRepository) {
        super();
        this.infoReturnOrderRepository = infoReturnOrderRepository;
    }

    @Override
    public void addInfoReturnOrder(InfoReturnOrder infoReturnOrder){
        infoReturnOrderRepository.save(infoReturnOrder);
    }

    @Override
    public InfoReturnOrder getInfoReturnOrderByOrder(Order order) {
        return infoReturnOrderRepository.findByOrder(order); //không có sẽ trả về null
    }
}
