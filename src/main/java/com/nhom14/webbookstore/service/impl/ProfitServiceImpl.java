package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.OrderItem;
import com.nhom14.webbookstore.entity.Profit;
import com.nhom14.webbookstore.repository.ProfitRepository;
import com.nhom14.webbookstore.service.ProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfitServiceImpl implements ProfitService {
    private ProfitRepository profitRepository;

    @Autowired
    public ProfitServiceImpl(ProfitRepository profitRepository) {
        super();
        this.profitRepository = profitRepository;
    }

    @Override
    public void addProfit(Profit profit) {
        profitRepository.save(profit);
    }

    @Override
    public Profit getProfitByOrderItem(OrderItem orderItem) {
        return profitRepository.findByOrderItem(orderItem);
    }
}
