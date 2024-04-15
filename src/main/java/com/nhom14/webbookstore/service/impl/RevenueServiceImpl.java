package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.Revenue;
import com.nhom14.webbookstore.repository.RevenueRepository;
import com.nhom14.webbookstore.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RevenueServiceImpl implements RevenueService {
    private RevenueRepository revenueRepository;

    @Autowired
    public RevenueServiceImpl(RevenueRepository revenueRepository) {
        super();
        this.revenueRepository = revenueRepository;
    }

    @Override
    public void addRevenue(Revenue revenue) {
        revenueRepository.save(revenue);
    }

    @Override
    public Revenue getRevenueByOrder(Order order) {
        return revenueRepository.findByOrder(order); //Không có sẽ trả về null
    }
}
