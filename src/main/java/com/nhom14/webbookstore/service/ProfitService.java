package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.OrderItem;
import com.nhom14.webbookstore.entity.Profit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ProfitService {
    // Phương thức thêm một bản ghi lợi nhuận mới
    void addProfit(Profit profit);

    Profit getProfitByOrderItem(OrderItem orderItem);

    // Lấy lợi nhuận từ 6 tháng gần đây
    List<Profit> getProfitsOfLastSixMonths();

    // Lấy lợi nhuận mỗi tháng theo tháng và năm
    double sumProfitByDateYearAndMonth(int year, int month);

    Page<Profit> getFilteredProfits(Long profitId, Integer orderitemId, Double costPrice, Double sellPrice, Double profit, LocalDate date, Double profitMin, Double profitMax, Pageable pageable);

    List<Profit> getAllProfits();
}
