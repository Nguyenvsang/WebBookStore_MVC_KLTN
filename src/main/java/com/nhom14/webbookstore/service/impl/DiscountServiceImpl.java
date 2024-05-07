package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Discount;
import com.nhom14.webbookstore.repository.DiscountRepository;
import com.nhom14.webbookstore.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountServiceImpl implements DiscountService {
    private DiscountRepository discountRepository;

    @Autowired
    public DiscountServiceImpl(DiscountRepository discountRepository) {
        super();
        this.discountRepository = discountRepository;
    }

    @Override
    public void saveDiscount(Discount discount) {
        discountRepository.save(discount);
    }

    @Override
    public Discount getDiscountById(long id) {
        return discountRepository.findById(id).orElse(null);
    }
}
