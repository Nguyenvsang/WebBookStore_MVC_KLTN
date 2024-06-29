package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.Revenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface RevenueService {
    // Phương thức để thêm một bản ghi doanh thu mới
    void addRevenue(Revenue revenue);

    // Phương thức lấy một bản ghi doanh thu theo đơn hàng
    Revenue getRevenueByOrder(Order order);

    // Lấy doanh thu mỗi tháng theo tháng và năm
    double sumRevenueByDateYearAndMonth(int year, int month);

    // Lấy doanh thu mỗi tháng trong khoảng thời gian đã chọn
    double sumRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    // Lấy danh sách doanh thu với các tham số tìm kiếm và lọc, phân trang
    Page<Revenue> getFilteredRevenues(Long revenueId, Integer orderId, Double revenue, LocalDate date, Double revenueMin, Double revenueMax, Pageable pageable);

    List<Revenue> getAllRevenues();
}
