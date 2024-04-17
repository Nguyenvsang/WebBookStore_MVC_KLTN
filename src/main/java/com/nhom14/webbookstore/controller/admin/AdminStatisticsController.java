package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.service.ProfitService;
import com.nhom14.webbookstore.service.RevenueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminStatisticsController {
    private RevenueService revenueService;
    private ProfitService profitService;

    @Autowired
    public AdminStatisticsController(RevenueService revenueService, ProfitService profitService) {
        super();
        this.revenueService = revenueService;
        this.profitService = profitService;
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model,
                                 HttpSession session) {

        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem admin đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        List<Double> revenuesOfLastSixMonthsDataByMonth = new ArrayList<>();
        List<Double> profitsOfLastSixMonthsDataByMonth = new ArrayList<>();
        List<String> monthsOfLastSixMonths = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime now = LocalDateTime.now().minusMonths(i);
            int year = now.getYear();
            int month = now.getMonthValue();
            double sumRevenue = revenueService.sumRevenueByDateYearAndMonth(year, month);
            double sumProfit = profitService.sumProfitByDateYearAndMonth(year, month);
            revenuesOfLastSixMonthsDataByMonth.add(sumRevenue);
            profitsOfLastSixMonthsDataByMonth.add(sumProfit);
            monthsOfLastSixMonths.add(String.valueOf(month));
        }

        // Thêm dữ liệu vào mô hình
        model.addAttribute("revenuesOfLastSixMonthsDataByMonth", revenuesOfLastSixMonthsDataByMonth);
        model.addAttribute("profitsOfLastSixMonthsDataByMonth", profitsOfLastSixMonthsDataByMonth);
        model.addAttribute("monthsOfLastSixMonths", monthsOfLastSixMonths);

        return "admin/statistics";
    }
}
