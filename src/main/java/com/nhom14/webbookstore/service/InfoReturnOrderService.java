package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.entity.Order;

public interface InfoReturnOrderService {
    // Phương thức để thêm một thông tin trả hàng
    void addInfoReturnOrder(InfoReturnOrder infoReturnOrder);
    // Lấy thông tin trả hàng theo đơn hàng
    InfoReturnOrder getInfoReturnOrderByOrder(Order order);
}
