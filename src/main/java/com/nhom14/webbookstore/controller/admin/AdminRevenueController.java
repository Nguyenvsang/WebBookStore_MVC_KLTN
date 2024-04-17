package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.Revenue;
import com.nhom14.webbookstore.service.RevenueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminRevenueController {
    private RevenueService revenueService;

    @Autowired
    public AdminRevenueController(RevenueService revenueService) {
        super();
        this.revenueService = revenueService;
    }

    @GetMapping("/managerevenues")
    public String manageRevenues(
            @RequestParam(value = "revenueId", required = false) Long revenueId,
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "revenue", required = false) Double revenue,
            @RequestParam(value = "dateStr", required = false) String dateStr,
            @RequestParam(value = "revenueMin", required = false) Double revenueMin, // Lọc doanh thu theo khoảng giá
            @RequestParam(value = "revenueMax", required = false) Double revenueMax, // Lọc doanh thu theo khoảng giá
            @RequestParam(value = "revenueOption", required = false) Integer revenueOption,
            // Sếp doanh thu tăng dần nếu giá trị priceOption là 12, giảm dần nếu giá trị là 21
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem admin đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        Sort sort = Sort.unsorted();
        if (revenueOption != null) {
            if (revenueOption == 12) {
                sort = sort.and(Sort.by("revenue").ascending());
            } else if (revenueOption == 21) {
                sort = sort.and(Sort.by("revenue").descending());
            }
        }

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        // Chuyển đổi chuỗi ngày thành đối tượng LocalDate
        LocalDate date = null;
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                // Xử lý trường hợp khi dateStr không phải là một ngày hợp lệ
                // Ví dụ: hiển thị thông báo lỗi cho người dùng
                redirectAttributes.addAttribute("message", "Ngày không hợp lệ!");
                // Chuyển hướng về trang managerevenues
                return "redirect:/managerevenues";
            }
        }

        // Gọi phương thức getFilteredRevenues với các tham số tìm kiếm và lọc
        Page<Revenue> revenues = revenueService.getFilteredRevenues(revenueId, orderId, revenue, date, revenueMin, revenueMax, pageable);
        // Tổng số tất cả các đánh giá
        long totalAllRevenues = revenueService.getAllRevenues().size();

        model.addAttribute("revenues", revenues);
        model.addAttribute("totalAllRevenues", totalAllRevenues);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
        params.put("revenueId", revenueId);
        params.put("orderId", orderId);
        params.put("revenue", revenue);
        params.put("dateStr", dateStr);
        params.put("revenueMin", revenueMin);
        params.put("revenueMax", revenueMax);
        params.put("revenueOption", revenueOption);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        return "admin/managerevenues";
    }
}
