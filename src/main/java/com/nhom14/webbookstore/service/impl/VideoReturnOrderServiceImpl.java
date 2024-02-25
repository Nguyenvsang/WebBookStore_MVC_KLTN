package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.entity.VideoReturnOrder;
import com.nhom14.webbookstore.repository.VideoReturnOrderRepository;
import com.nhom14.webbookstore.service.VideoReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoReturnOrderServiceImpl implements VideoReturnOrderService {
    private VideoReturnOrderRepository videoReturnOrderRepository;

    @Autowired

    public VideoReturnOrderServiceImpl(VideoReturnOrderRepository videoReturnOrderRepository) {
        super();
        this.videoReturnOrderRepository = videoReturnOrderRepository;
    }

    @Override
    public VideoReturnOrder getVideoReturnOrderByInfoReturnOrder(InfoReturnOrder infoReturnOrder) {
        return videoReturnOrderRepository.findByInfoReturnOrder(infoReturnOrder);
    }

    @Override
    public void addVideoReturnOrder(VideoReturnOrder videoReturnOrder) {
        videoReturnOrderRepository.save(videoReturnOrder);
    }

    @Override
    public void updateVideoReturnOrder(VideoReturnOrder videoReturnOrder) {
        videoReturnOrderRepository.save(videoReturnOrder);
    }
}
