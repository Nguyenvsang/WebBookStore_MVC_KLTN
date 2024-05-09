package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Discount;
import com.nhom14.webbookstore.repository.DiscountRepository;
import com.nhom14.webbookstore.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public Page<Discount> getFilteredDiscounts(Integer bookId, String searchKeyword, Integer status, Pageable pageable) {
        return discountRepository.findWithFilters(bookId, searchKeyword, status, pageable);
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll(); // Nếu không có sách sẽ trả về empty list
    }

    @Override
    public Discount getLatestActiveDiscountByBookId(int bookId) {
        List<Discount> discounts = discountRepository.findActiveDiscountsByBookId(bookId, LocalDateTime.now(), PageRequest.of(0, 1));
        if (!discounts.isEmpty()) {
            return discounts.get(0);
        } else {
            return null;
        }
    }
}
