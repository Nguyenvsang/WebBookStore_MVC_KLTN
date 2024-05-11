package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface DiscountService {
    void saveDiscount(Discount discount);

    // Phương thức lấy thông tin giảm giá theo id
    Discount getDiscountById(long id);

    Page<Discount> getFilteredDiscounts(Integer bookId, String searchKeyword, Integer status, Pageable pageable);

    List<Discount> getAllDiscounts();

    Discount getLatestActiveDiscountByBookId(int bookId);

    List<Discount> findOverlappingDiscounts(Integer bookId, Timestamp startDate, Timestamp endDate);
}
