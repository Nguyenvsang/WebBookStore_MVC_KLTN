package com.nhom14.webbookstore.service.impl;

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
}
