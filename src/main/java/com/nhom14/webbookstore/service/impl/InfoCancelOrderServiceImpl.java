package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.InfoCancelOrder;
import com.nhom14.webbookstore.repository.InfoCancelOrderRepository;
import com.nhom14.webbookstore.service.InfoCancelOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfoCancelOrderServiceImpl implements InfoCancelOrderService {
    private InfoCancelOrderRepository infoCancelOrderRepository;
    @Autowired
    public InfoCancelOrderServiceImpl(InfoCancelOrderRepository infoCancelOrderRepository) {
        super();
        this.infoCancelOrderRepository = infoCancelOrderRepository;
    }
    @Override
    public void addInfoCancelOrder(InfoCancelOrder infoCancelOrder) {
        infoCancelOrderRepository.save(infoCancelOrder);
    }
}
