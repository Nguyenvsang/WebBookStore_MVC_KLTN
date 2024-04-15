package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.Revenue;

public interface RevenueService {
    // Phương thức để thêm một bản ghi doanh thu mới
    void addRevenue(Revenue revenue);

    // Phương thức lấy một bản ghi doanh thu theo đơn hàng
    Revenue getRevenueByOrder(Order order);
}
