package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.Revenue;
import com.nhom14.webbookstore.repository.RevenueRepository;
import com.nhom14.webbookstore.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RevenueServiceImpl implements RevenueService {
    private RevenueRepository revenueRepository;

    @Autowired
    public RevenueServiceImpl(RevenueRepository revenueRepository) {
        super();
        this.revenueRepository = revenueRepository;
    }

    @Override
    public void addRevenue(Revenue revenue) {
        revenueRepository.save(revenue);
    }

    @Override
    public Revenue getRevenueByOrder(Order order) {
        return revenueRepository.findByOrder(order); //Không có sẽ trả về null
    }

    @Override
    public double sumRevenueByDateYearAndMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return revenueRepository.sumRevenueBetweenDates(start, end); //Không có sẽ trả về 0
    }

    @Override
    public double sumRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return revenueRepository.sumRevenueBetweenDates(start, end);
    }

    @Override
    public Page<Revenue> getFilteredRevenues(Long revenueId, Integer orderId, Double revenue, LocalDate date, Double revenueMin, Double revenueMax, Pageable pageable) {
        return revenueRepository.findWithFilters(revenueId, orderId, revenue, date, revenueMin, revenueMax, pageable);
    }

    @Override
    public List<Revenue> getAllRevenues() {
        return revenueRepository.findAll();
    }

}
