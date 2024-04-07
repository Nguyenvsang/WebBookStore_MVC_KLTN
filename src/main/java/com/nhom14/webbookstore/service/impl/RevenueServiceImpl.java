package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.repository.RevenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RevenueServiceImpl {
    private RevenueRepository revenueRepository;

    @Autowired
    public RevenueServiceImpl(RevenueRepository revenueRepository) {
        super();
        this.revenueRepository = revenueRepository;
    }
}
