package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Profit;
import com.nhom14.webbookstore.entity.Revenue;
import com.nhom14.webbookstore.service.ProfitService;
import com.nhom14.webbookstore.service.RevenueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedList;

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

    public Map<String, Double> reverseMap(Map<String, Double> originalMap) {
        Map<String, Double> reversedMap = new LinkedHashMap<>();
        new LinkedList<>(new ArrayList<>(originalMap.keySet()))
                .descendingIterator()
                .forEachRemaining(key -> reversedMap.put(key, originalMap.get(key)));
        return reversedMap;
    }


    private Map<String, Double> getRevenueData() {
        Map<String, Double> revenueData = new LinkedHashMap<>();
        // Lấy danh sách doanh thu 6 tháng gần đây từ CSDL
        // Ví dụ: List<Revenue> revenues = revenueService.getRecentRevenues();
        // Với mỗi doanh thu, thêm vào revenueData
        // Ví dụ: for (Revenue revenue : revenues) { revenueData.put(revenue.getDate().toString(), revenue.getRevenue()); }
        return revenueData;
    }

    private Map<String, Double> getProfitData() {
        Map<String, Double> profitData = new LinkedHashMap<>();
        // Lấy danh sách lợi nhuận 6 tháng gần đây từ CSDL
        // Ví dụ: List<Profit> profits = profitService.getRecentProfits();
        // Với mỗi lợi nhuận, thêm vào profitData
        // Ví dụ: for (Profit profit : profits) { profitData.put(profit.getDate().toString(), profit.getProfit()); }
        return profitData;
    }

}
