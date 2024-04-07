package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.repository.ProfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfitServiceImpl {
    private ProfitRepository profitRepository;

    @Autowired
    public ProfitServiceImpl(ProfitRepository profitRepository) {
        super();
        this.profitRepository = profitRepository;
    }
}
