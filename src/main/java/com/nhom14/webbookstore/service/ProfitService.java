package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.OrderItem;
import com.nhom14.webbookstore.entity.Profit;

public interface ProfitService {
    // Phương thức thêm một bản ghi lợi nhuận mới
    void addProfit(Profit profit);

    Profit getProfitByOrderItem(OrderItem orderItem);
}
