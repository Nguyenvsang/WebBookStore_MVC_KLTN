package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.OrderItem;
import com.nhom14.webbookstore.entity.Profit;
import com.nhom14.webbookstore.repository.ProfitRepository;
import com.nhom14.webbookstore.service.ProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfitServiceImpl implements ProfitService {
    private ProfitRepository profitRepository;

    @Autowired
    public ProfitServiceImpl(ProfitRepository profitRepository) {
        super();
        this.profitRepository = profitRepository;
    }

    @Override
    public void addProfit(Profit profit) {
        profitRepository.save(profit);
    }

    @Override
    public Profit getProfitByOrderItem(OrderItem orderItem) {
        return profitRepository.findByOrderItem(orderItem);
    }

    @Override
    public List<Profit> getProfitsOfLastSixMonths() {
        // Lấy ngày hiện tại
        LocalDateTime currentDate = LocalDateTime.now();

        // Tính ngày 6 tháng trước
        LocalDateTime sixMonthsAgo = currentDate.minusMonths(6);

        // Chuyển đổi LocalDate sang Timestamp
        Timestamp start = Timestamp.valueOf(sixMonthsAgo);
        Timestamp end = Timestamp.valueOf(currentDate);

        // Lấy lợi nhuận từ 6 tháng gần đây
        return profitRepository.findByDateBetween(start, end); // không thấy sẽ trả về danh sách rỗng
    }

    @Override
    public double sumProfitByDateYearAndMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return profitRepository.sumProfitBetweenDates(start, end); //Không có sẽ trả về 0
    }

    @Override
    public double sumProfitByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return profitRepository.sumProfitBetweenDates(start, end);
    }

    @Override
    public Page<Profit> getFilteredProfits(Long profitId, Integer orderitemId, Double costPrice, Double sellPrice, Double profit, LocalDate date, Double profitMin, Double profitMax, Pageable pageable) {
        return profitRepository.findWithFilters(profitId, orderitemId, costPrice, sellPrice, profit, date, profitMin, profitMax, pageable);
    }

    @Override
    public List<Profit> getAllProfits() {
        return profitRepository.findAll();
    }
}
