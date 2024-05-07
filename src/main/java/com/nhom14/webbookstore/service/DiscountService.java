package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Discount;

public interface DiscountService {
    void saveDiscount(Discount discount);

    // Phương thức lấy thông tin giảm giá theo id
    Discount getDiscountById(long id);
}
