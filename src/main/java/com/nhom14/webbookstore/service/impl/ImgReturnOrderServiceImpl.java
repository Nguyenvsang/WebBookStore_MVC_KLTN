package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.ImgReturnOrder;
import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.repository.ImgReturnOrderRepository;
import com.nhom14.webbookstore.service.ImgReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImgReturnOrderServiceImpl implements ImgReturnOrderService {
    private ImgReturnOrderRepository imgReturnOrderRepository;
    @Autowired
    public ImgReturnOrderServiceImpl(ImgReturnOrderRepository imgReturnOrderRepository) {
        super();
        this.imgReturnOrderRepository = imgReturnOrderRepository;
    }

    @Override
    public void addImgReturnOrder(ImgReturnOrder imgReturnOrder) {
        imgReturnOrderRepository.save(imgReturnOrder);
    }

    @Override
    public void updateImgReturnOrder(ImgReturnOrder imgReturnOrder) {
        imgReturnOrderRepository.save(imgReturnOrder);
    }

    @Override
    public ImgReturnOrder getImgReturnOrderByInfoReturnOrder(InfoReturnOrder infoReturnOrder) {
        return imgReturnOrderRepository.findByInfoReturnOrder(infoReturnOrder); // trả về null
    }
}
