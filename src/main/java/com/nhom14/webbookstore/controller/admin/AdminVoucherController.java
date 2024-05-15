package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.CategoryService;
import com.nhom14.webbookstore.service.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class AdminVoucherController {
    private VoucherService voucherService;
    private CategoryService categoryService;

    @Autowired
    public AdminVoucherController(VoucherService voucherService, CategoryService categoryService) {
        super();
        this.voucherService = voucherService;
        this.categoryService = categoryService;
    }

    @GetMapping("/managevouchers")
    public String manageVouchers(
            @RequestParam(value = "categoryId", required = false) Integer categoryId, //lọc theo danh mục
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword, //từ khóa tìm kiếm
            // Có thể kiếm theo mã code
            @RequestParam(value = "voucherScope", required = false) Integer voucherScope, // lọc theo phạm vi áp dụng
            @RequestParam(value = "status", required = false) Integer status, // lọc theo trạng thái
            @RequestParam(value = "sortOption", required = false, defaultValue = "sd21") String sortOption,
            //asc- tăng dần-12, desc- giảm dần-21
            //các tùy chọn: sd12 (ngày bắt đầu tăng dần), sd21(ngày bắt đầu giảm dần),
            // ed12 (ngày kết thúc tăng dần), ed21(ngày kết thúc giảm dần)
            // dpc12, dpc21 (tỷ lệ giảm giá), q12, q21 (số lượng), rq12, rq21 (số lượng còn lại)
            // mov12, mov21 (giá trị đơn hàng tối thiểu)
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

        Sort sort = handleSortOption(sortOption);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        // Gọi phương thức getFilteredVouchers với các tham số tìm kiếm và lọc
        Page<Voucher> vouchers = voucherService.getFilteredVouchers(categoryId, searchKeyword, voucherScope, status, pageable);
        // Tổng số tất cả các voucher
        long totalAllVouchers = voucherService.getAllVouchers().size();

        model.addAttribute("vouchers", vouchers);
        model.addAttribute("totalAllVouchers", totalAllVouchers);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("searchKeyword", searchKeyword);
        params.put("voucherScope", voucherScope);
        params.put("status", status);
        params.put("sortOption", sortOption);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        // Lấy danh sách các danh mục còn kinh doanh
        List<Category> allCategories = categoryService.getActiveCategories();

        model.addAttribute("allCategories", allCategories);

        return "admin/managevouchers";
    }

    public Sort handleSortOption(String sortOption) {
        Sort sort = Sort.unsorted();
        if (sortOption != null) {
            sort = switch (sortOption) {
                case "sd12" -> sort.and(Sort.by("startDate").ascending());
                case "sd21" -> sort.and(Sort.by("startDate").descending());
                case "ed12" -> sort.and(Sort.by("endDate").ascending());
                case "ed21" -> sort.and(Sort.by("endDate").descending());
                case "dpc12" -> sort.and(Sort.by("discountPercent").ascending());
                case "dpc21" -> sort.and(Sort.by("discountPercent").descending());
                case "q12" -> sort.and(Sort.by("quantity").ascending());
                case "q21" -> sort.and(Sort.by("quantity").descending());
                case "rq12" -> sort.and(Sort.by("remainingQuantity").ascending());
                case "rq21" -> sort.and(Sort.by("remainingQuantity").descending());
                case "mov12" -> sort.and(Sort.by("minimumOrderValue").ascending());
                case "mov21" -> sort.and(Sort.by("minimumOrderValue").descending());
                default -> sort;
            };
        }
        return sort;
    }

    @GetMapping("/managedetailvoucher")
    public String manegeDetailVoucher(
            @RequestParam("voucherId") Long voucherId,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request, //Dùng cho nút Quay lại
            Model model,
            HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Lấy Voucher từ id
        Voucher voucher = voucherService.getVoucherById(voucherId);

        if (voucher == null) {
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Đặt thuộc tính vào model để sử dụng trong view
        model.addAttribute("voucher", voucher);

        // Lưu URL trang trước đó vào session
        String referer = request.getHeader("Referer");
        String currentUrl = request.getRequestURL().toString();

        // Kiểm tra xem referer có khác với URL hiện tại hay không (tránh trường hợp 1 trang lặp lại)
        // và có chứa cụm "/managevouchers" (tránh trường hợp vượt quá kiểm soát)
        if (referer != null && !referer.equals(currentUrl) && (referer.contains("/managevouchers"))) {
            session.setAttribute("previousUrl", referer);
        }

        return "admin/managedetailvoucher";
    }

    @GetMapping("/addvoucher")
    public String showAddVoucherForm(Model model, HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Lấy danh sách các danh mục còn kinh doanh
        List<Category> allCategories = categoryService.getActiveCategories();

        model.addAttribute("allCategories", allCategories);

        return "admin/addvoucher";
    }

    @PostMapping("/addvoucher")
    public String addVoucher(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                             @RequestParam("code") String code,
                             @RequestParam("quantity") int quantity,
                             @RequestParam(value = "discountPercent", required = false) Integer discountPercent,
                             @RequestParam(value = "amountDiscount", required = false) Double amountDiscount,
                             @RequestParam("minimumOrderValue") double minimumOrderValue,
                             @RequestParam("status") int status,
                             @RequestParam("startDate") String startDate,
                             @RequestParam("startTime") String startTime,
                             @RequestParam("endDate") String endDate,
                             @RequestParam("endTime") String endTime,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        if (admin == null) return "redirect:/loginadmin";
        if (voucherService.getVoucherByCode(code) != null) {
            redirectAttributes.addAttribute("message", "Mã voucher đã tồn tại. Vui lòng nhập mã khác.");
            return "redirect:/addvoucher";
        }

        if (!isValidInput(discountPercent, amountDiscount, minimumOrderValue, quantity, quantity, redirectAttributes)) {
            return "redirect:/addvoucher";
        }

        Timestamp startTimestamp = convertToTimestamp(startDate, startTime);
        Timestamp endTimestamp = convertToTimestamp(endDate, endTime);

        Voucher voucher = createVoucher(code, quantity, discountPercent, amountDiscount, minimumOrderValue, status, startTimestamp, endTimestamp, categoryId);

        voucherService.saveVoucher(voucher);
        redirectAttributes.addAttribute("message", "Đã thêm thành công!");
        return "redirect:/addvoucher";
    }

    private boolean isValidInput(Integer discountPercent, Double amountDiscount, Double minimumOrderValue, Integer quantity, Integer remainingQuantity, RedirectAttributes redirectAttributes) {
        if (discountPercent != null && (discountPercent < 0 || discountPercent > 100)) {
            redirectAttributes.addAttribute("message", "Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100.");
            return false;
        }
        if (amountDiscount != null && amountDiscount < 0) {
            redirectAttributes.addAttribute("message", "Số tiền giảm giá không được âm.");
            return false;
        }
        if (minimumOrderValue < 0) {
            redirectAttributes.addAttribute("message", "Giá trị đơn hàng tối thiểu không được âm.");
            return false;
        }
        if (quantity <= 0) {
            redirectAttributes.addAttribute("message", "Số lượng phải là một số nguyên dương.");
            return false;
        }
        if (remainingQuantity != null && remainingQuantity <= 0) {
            redirectAttributes.addAttribute("message", "Số lượng còn lại phải là một số nguyên dương.");
            return false;
        }
        return true;
    }

    private Timestamp convertToTimestamp(String date, String time) {
        String dateTimeString = date + " " + time + ":00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        return Timestamp.valueOf(localDateTime);
    }

    private Voucher createVoucher(String code, int quantity, Integer discountPercent, Double amountDiscount, double minimumOrderValue, int status, Timestamp startTimestamp, Timestamp endTimestamp, Integer categoryId) {
        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setQuantity(quantity);
        voucher.setRemainingQuantity(quantity);
        // Gán giá trị ban đầu cho cả 3
        voucher.setDiscountPercent(-1);
        voucher.setAmountDiscount(-1);
        voucher.setCategory(null);
        if (discountPercent != null) voucher.setDiscountPercent(discountPercent);
        if (amountDiscount != null) voucher.setAmountDiscount(amountDiscount);
        voucher.setMinimumOrderValue(minimumOrderValue);
        voucher.setStatus(status);
        voucher.setStartDate(startTimestamp);
        voucher.setEndDate(endTimestamp);
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            voucher.setCategory(category);
            voucher.setVoucherScope(1); //1: Áp dụng cho 1 danh mục
        }
        return voucher;
    }

    @GetMapping("/updatevoucher")
    public String showUpdateVoucherForm(
            @RequestParam("voucherId") Long voucherId,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request, //Dùng cho nút Quay lại
            Model model,
            HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Lấy Voucher từ id
        Voucher voucher = voucherService.getVoucherById(voucherId);

        if (voucher == null) {
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Lấy danh sách các danh mục còn kinh doanh
        List<Category> allCategories = categoryService.getActiveCategories();

        model.addAttribute("allCategories", allCategories);

        // Đặt thuộc tính vào model để sử dụng trong view
        model.addAttribute("voucher", voucher);

        return "admin/updatevoucher";
    }

    @PostMapping("/updatevoucher")
    public String updateVoucher(@RequestParam("voucherId") Long voucherId,
                                @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                @RequestParam("code") String code,
                                @RequestParam("quantity") int quantity,
                                @RequestParam(value = "remainingQuantity", required = false) Integer remainingQuantity,
                                @RequestParam(value = "discountPercent", required = false) Integer discountPercent,
                                @RequestParam(value = "amountDiscount", required = false) Double amountDiscount,
                                @RequestParam("minimumOrderValue") double minimumOrderValue,
                                @RequestParam("status") int status,
                                @RequestParam("startDate") String startDate,
                                @RequestParam("startTime") String startTime,
                                @RequestParam("endDate") String endDate,
                                @RequestParam("endTime") String endTime,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        if (admin == null) return "redirect:/loginadmin";

        Voucher voucher = voucherService.getVoucherById(voucherId);
        if (voucher == null) {
            redirectAttributes.addAttribute("message", "Không tìm thấy voucher.");
            return "redirect:/updatevoucher";
        }

        if (!Objects.equals(voucher.getCode(), code) && voucherService.getVoucherByCode(code) != null) {
            redirectAttributes.addAttribute("message", "Mã voucher đã tồn tại. Vui lòng nhập mã khác.");
            return "redirect:/addvoucher";
        }

        if (!isValidInput(discountPercent, amountDiscount, minimumOrderValue, quantity, remainingQuantity, redirectAttributes)) {
            return "redirect:/updatevoucher";
        }

        Timestamp startTimestamp = convertToTimestamp(startDate, startTime);
        Timestamp endTimestamp = convertToTimestamp(endDate, endTime);
        
        voucherService.saveVoucher(updateVoucher(voucher, code, quantity, remainingQuantity, discountPercent, amountDiscount, minimumOrderValue, status, startTimestamp, endTimestamp, categoryId));
        redirectAttributes.addAttribute("message", "Đã cập nhật thành công!");
        redirectAttributes.addAttribute("voucherId", voucherId);
        return "redirect:/updatevoucher";
    }

    private Voucher updateVoucher(Voucher voucher, String code, Integer quantity, Integer remainingQuantity, Integer discountPercent, Double amountDiscount, Double minimumOrderValue, Integer status, Timestamp startTimestamp, Timestamp endTimestamp, Integer categoryId) {
        voucher.setCode(code);
        voucher.setQuantity(quantity);
        if (remainingQuantity != null) voucher.setRemainingQuantity(remainingQuantity);
        if (discountPercent != null) voucher.setDiscountPercent(discountPercent);
        if (amountDiscount != null) voucher.setAmountDiscount(amountDiscount);
        voucher.setMinimumOrderValue(minimumOrderValue);
        voucher.setStatus(status);
        voucher.setStartDate(startTimestamp);
        voucher.setEndDate(endTimestamp);
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            voucher.setCategory(category);
            voucher.setVoucherScope(1); //1: Áp dụng cho 1 danh mục
        }
        return voucher;
    }




}
