package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.PaymentStatus;

public interface PaymentStatusService {
    // Phương thức để thêm một trạng thái thanh toán
    void addPaymentStatus(PaymentStatus paymentStatus);

    // Lấy trạng thái thanh toán theo đơn hàng
    PaymentStatus getPaymentStatusByOrder(Order order);

    // Phương thức để cập nhật một trạng thái thanh toán
    void updatePaymentStatus(PaymentStatus paymentStatus);
}
