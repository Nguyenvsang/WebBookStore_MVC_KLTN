package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Profit;
import com.nhom14.webbookstore.service.ProfitService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminProfitController {
    private ProfitService profitService;

    @Autowired
    public AdminProfitController(ProfitService profitService) {
        super();
        this.profitService = profitService;
    }

    @GetMapping("/manageprofits")
    public String manageProfits(
            @RequestParam(value = "profitId", required = false) Long profitId,
            @RequestParam(value = "orderitemId", required = false) Integer orderitemId,
            @RequestParam(value = "costPrice", required = false) Double costPrice,
            @RequestParam(value = "sellPrice", required = false) Double sellPrice,
            @RequestParam(value = "profit", required = false) Double profit,
            @RequestParam(value = "dateStr", required = false) String dateStr,
            @RequestParam(value = "profitMin", required = false) Double profitMin, // Lọc lợi nhuận theo khoảng giá
            @RequestParam(value = "profitMax", required = false) Double profitMax, // Lọc lợi nhuận theo khoảng giá
            @RequestParam(value = "profitOption", required = false) Integer profitOption,
            // Sếp lợi nhuận tăng dần nếu giá trị priceOption là 12, giảm dần nếu giá trị là 21
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
        if (profitOption != null) {
            if (profitOption == 12) {
                sort = sort.and(Sort.by("profit").ascending());
            } else if (profitOption == 21) {
                sort = sort.and(Sort.by("profit").descending());
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
                return "redirect:/manageprofits";
            }
        }

        // Gọi phương thức getFilteredProfits với các tham số tìm kiếm và lọc
        Page<Profit> profits = profitService.getFilteredProfits(profitId, orderitemId, costPrice, sellPrice, profit, date, profitMin, profitMax, pageable);
        // Tổng số tất cả các bản ghi lợi nhuận
        long totalAllProfits = profitService.getAllProfits().size();

        model.addAttribute("profits", profits);
        model.addAttribute("totalAllProfits", totalAllProfits);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
        params.put("profitId", profitId);
        params.put("orderitemId", orderitemId);
        params.put("costPrice", costPrice);
        params.put("sellPrice", sellPrice);
        params.put("profit", profit);
        params.put("dateStr", dateStr);
        params.put("profitMin", profitMin);
        params.put("profitMax", profitMax);
        params.put("profitOption", profitOption);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        return "admin/manageprofits";
    }
}
