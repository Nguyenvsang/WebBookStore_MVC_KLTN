package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.service.ProfitService;
import com.nhom14.webbookstore.service.RevenueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public String showStatistics(@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 Model model, HttpSession session) {

        Account admin = (Account) session.getAttribute("admin");

        if (admin == null) {
            return "redirect:/loginadmin";
        }

        boolean isCustomDateRange = startDate != null && endDate != null;

        if (!isCustomDateRange) {
            endDate = LocalDate.now();
            startDate = endDate.minusMonths(6);
        }

        List<Double> revenuesData = new ArrayList<>();
        List<Double> profitsData = new ArrayList<>();
        List<String> monthsData = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int year = current.getYear();
            int month = current.getMonthValue();
            double sumRevenue = revenueService.sumRevenueByDateYearAndMonth(year, month);
            double sumProfit = profitService.sumProfitByDateYearAndMonth(year, month);
            revenuesData.add(sumRevenue);
            profitsData.add(sumProfit);
            monthsData.add(current.getMonth().getDisplayName(TextStyle.SHORT, new Locale("vi", "VN")) + " " + year);
            current = current.plusMonths(1);
        }

        model.addAttribute("revenuesOfLastSixMonthsDataByMonth", revenuesData);
        model.addAttribute("profitsOfLastSixMonthsDataByMonth", profitsData);
        model.addAttribute("monthsOfLastSixMonths", monthsData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("isCustomDateRange", isCustomDateRange);

        return "admin/statistics";
    }
}
