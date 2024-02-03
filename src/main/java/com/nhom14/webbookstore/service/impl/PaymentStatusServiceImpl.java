package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.PaymentStatus;
import com.nhom14.webbookstore.repository.PaymentStatusRepository;
import com.nhom14.webbookstore.service.PaymentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusServiceImpl implements PaymentStatusService {
    private PaymentStatusRepository paymentStatusRepository;

    @Autowired

    public PaymentStatusServiceImpl(PaymentStatusRepository paymentStatusRepository) {
        super();
        this.paymentStatusRepository = paymentStatusRepository;
    }

    @Override
    public void addPaymentStatus(PaymentStatus paymentStatus) {
        paymentStatusRepository.save(paymentStatus);
    }

    @Override
    public PaymentStatus getPaymentStatusByOrder(Order order) {
        return paymentStatusRepository.findByOrder(order);
    }

    @Override
    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        paymentStatusRepository.save(paymentStatus);
    }
}
