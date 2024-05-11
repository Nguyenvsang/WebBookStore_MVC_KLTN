package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.model.lean_model.DiscountLeanModel;
import com.nhom14.webbookstore.model.response_model.BookResponseModel;
import com.nhom14.webbookstore.service.BookPriceService;
import com.nhom14.webbookstore.service.BookService;
import com.nhom14.webbookstore.service.CategoryService;
import com.nhom14.webbookstore.service.DiscountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
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

@Controller
public class AdminDiscountController {
    private DiscountService discountService;
    private BookService bookService;
    private BookPriceService bookPriceService;
    private CategoryService categoryService;
    private ModelMapper modelMapper;

    @Autowired
    public AdminDiscountController(DiscountService discountService, BookService bookService, BookPriceService bookPriceService, CategoryService categoryService, ModelMapper modelMapper) {
        super();
        this.discountService = discountService;
        this.bookService = bookService;
        this.bookPriceService = bookPriceService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/managediscounts")
    public String manageDiscounts(
            @RequestParam(value = "bookId", required = false) Integer bookId, //lọc theo sách
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword, //từ khóa tìm kiếm
            // Có thể kiếm theo tên sách
            @RequestParam(value = "status", required = false) Integer status, // lọc theo trạng thái
            @RequestParam(value = "sortOption", required = false, defaultValue = "sd21") String sortOption,
            //asc- tăng dần-12, desc- giảm dần-21
            //các tùy chọn: sd12 (ngày bắt đầu tăng dần), sd21(ngày bắt đầu giảm dần),
            // ed12 (ngày kết thúc tăng dần), ed21(ngày kết thúc giảm dần)
            // dpc12, dpc21 (tỷ lệ giảm giá), dp12, dp21 (giá cuối cùng)
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

        // Gọi phương thức getFilteredDiscounts với các tham số tìm kiếm và lọc
        Page<Discount> discounts = discountService.getFilteredDiscounts(bookId, searchKeyword, status, pageable);
        // Tổng số tất cả các discount
        long totalAllDiscounts = discountService.getAllDiscounts().size();

        model.addAttribute("discounts", discounts);
        model.addAttribute("totalAllDiscounts", totalAllDiscounts);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
        params.put("bookId", bookId);
        params.put("searchKeyword", searchKeyword);
        params.put("status", status);
        params.put("sortOption", sortOption);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        // Lấy danh sách tất cả sách đang kinh doanh để hiển thị trong dropdown
        List<Book> allBooks = bookService.getActiveBooks();

        model.addAttribute("allBooks", allBooks);

        return "admin/managediscounts";
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
                case "dp12" -> sort.and(Sort.by("discountedPrice").ascending());
                case "dp21" -> sort.and(Sort.by("discountedPrice").descending());
                default -> sort;
            };
        }
        return sort;
    }

    @GetMapping("/adddiscount")
    public String showAddDiscountForm(Model model,
                                       HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Lấy danh sách các danh mục còn kinh doanh
        List<Category> allCategories = categoryService.getActiveCategories();

        // Lấy danh sách các sách còn kinh doanh để hiển thị trong dropdown
        List<Book> allBooks = bookService.getActiveBooks();
        // Chuyển theo dạng Book Response
        List<BookResponseModel> bookResponseModels = allBooks.stream()
                .map(this::convertToBookResponseModel)
                .toList();

        model.addAttribute("allBooks", bookResponseModels);
        model.addAttribute("allCategories", allCategories);

        return "admin/adddiscount";
    }

    @PostMapping("/adddiscount")
    public String addDiscount(@RequestParam(value = "bookId", required = false) Integer bookId,
                              @RequestParam(value = "categoryId", required = false) Integer categoryId,
                              @RequestParam("discountPercent") int discountPercent,
                              @RequestParam("startDate") String startDate,
                              @RequestParam("startTime") String startTime,
                              @RequestParam("endDate") String endDate,
                              @RequestParam("endTime") String endTime,
                              @RequestParam("status") int status,
                              RedirectAttributes redirectAttributes,
                              Model model,
                              HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Gộp ngày và giờ nhập thành một chuỗi, đồng thời thêm giây là 00
        String startDateTimeString = startDate + " " + startTime + ":00";
        String endDateTimeString = endDate + " " + endTime + ":00";

        // Sử dụng định dạng chuẩn cho LocalDateTime.parse()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Chuyển đổi chuỗi thành kiểu LocalDateTime
        LocalDateTime startLocalDateTime = LocalDateTime.parse(startDateTimeString, formatter);
        LocalDateTime endLocalDateTime = LocalDateTime.parse(endDateTimeString, formatter);

        // Chuyển đổi LocalDateTime thành Timestamp
        Timestamp startTimestamp = Timestamp.valueOf(startLocalDateTime);
        Timestamp endTimestamp = Timestamp.valueOf(endLocalDateTime);

        // Lấy sách
        if (bookId != null) {
            Book book = bookService.getBookById(bookId);
            String redirectUrl = addDiscountForBook(book, discountPercent, startTimestamp, endTimestamp, status, redirectAttributes);
            if (redirectUrl != null) {
                return redirectUrl;
            }
        }

        if (categoryId != null) {
            List<Book> books = bookService.getActiveBooksByCategory(categoryId);
            for (Book book : books) {
                String redirectUrl = addDiscountForBook(book, discountPercent, startTimestamp, endTimestamp, status, redirectAttributes);
                if (redirectUrl != null) {
                    return redirectUrl;
                }
            }
        }


        // sau khi lưu thành công
        redirectAttributes.addAttribute("message", "Đã thêm thành công!");
        return "redirect:/adddiscount";
    }

    private String addDiscountForBook(Book book,
                                      int discountPercent,
                                      Timestamp startTimestamp,
                                      Timestamp endTimestamp,
                                      int status,
                                      RedirectAttributes redirectAttributes) {
        // Kiểm tra xem có đợt giảm giá nào trùng khớp không
        List<Discount> overlappingDiscounts = discountService.findOverlappingDiscounts(book.getId(), startTimestamp, endTimestamp);
        if (!overlappingDiscounts.isEmpty()) {
            // Nếu có, thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra: Đợt giảm giá trùng với đợt giảm giá khác cho sách có mã: " + book.getId() + "!");
            return "redirect:/erroradmin";
        }

        // Lấy bookPrice mới nhất của cuốn sách và ngày hiệu lực nhỏ hơn ngày hiện tại
        BookPrice bookPrice = bookPriceService.getLatestEffectiveBookPriceByBook(book);

        // Kiểm tra xem giá sách và giá trong BookPrice có khớp nhau không
        if (bookPrice != null && book.getSellPrice() == bookPrice.getSellPrice()) {
            // Nếu khớp, tính giá đã giảm
            double discountedPrice = book.getSellPrice() * (100 - discountPercent) / 100;

            // Tạo đối tượng Discount và gán các giá trị
            Discount discount = new Discount();
            discount.setBook(book);
            discount.setDiscountPercent(discountPercent); // Lưu số nguyên 5, 10, 20
            discount.setDiscountedPrice(discountedPrice);
            discount.setStartDate(startTimestamp);
            discount.setEndDate(endTimestamp);
            discount.setStatus(status);

            // Lưu đối tượng Discount vào cơ sở dữ liệu
            discountService.saveDiscount(discount);
        } else {
            // Nếu không khớp, thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra: Giá sách và giá trong BookPrice không khớp cho sách có mã: " + book.getId() + "!");
            return "redirect:/erroradmin";
        }

        // Nếu không có lỗi, trả về null
        return null;
    }

    @GetMapping("/updatediscount")
    public String showUpdateDiscountForm(
            @RequestParam("discountId") Long discountId,
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

        // Lấy Discount từ id
        Discount discount = discountService.getDiscountById(discountId);

        if (discount == null) {
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Đặt thuộc tính vào model để sử dụng trong view
        model.addAttribute("discount", discount);

        // Lưu URL trang trước đó vào session
        String referer = request.getHeader("Referer");
        String currentUrl = request.getRequestURL().toString();

        // Kiểm tra xem referer có khác với URL hiện tại hay không (tránh trường hợp 1 trang lặp lại)
        // và có chứa cụm "/managediscounts" hoặc "/managedetailbook" (tránh trường hợp vượt quá kiểm soát)
        if (referer != null && !referer.equals(currentUrl) && (referer.contains("/managediscounts") || referer.contains("/managedetailbook"))) {
            session.setAttribute("previousUrl", referer);
        }

        return "admin/updatediscount";
    }

    @PostMapping("/updatediscount")
    public String updateDiscount(@RequestParam("discountId") Long discountId,
                                 @RequestParam("bookId") int bookId,
                                 @RequestParam("discountPercent") int discountPercent,
                                 @RequestParam("startDate") String startDate,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endDate") String endDate,
                                 @RequestParam("endTime") String endTime,
                                 @RequestParam("status") int status,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Lấy sách
        Book book = bookService.getBookById(bookId);

        // Lấy đối tượng Discount từ id
        Discount discount = discountService.getDiscountById(discountId);

        // Kiểm xem đợt giảm giá hoặc sách có bị null không
        if(discount == null || book == null){
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Gộp ngày và giờ nhập thành một chuỗi, đồng thời thêm giây là 00
        String startDateTimeString = startDate + " " + startTime + ":00";
        String endDateTimeString = endDate + " " + endTime + ":00";

        // Sử dụng định dạng chuẩn cho LocalDateTime.parse()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Chuyển đổi chuỗi thành kiểu LocalDateTime
        LocalDateTime startLocalDateTime = LocalDateTime.parse(startDateTimeString, formatter);
        LocalDateTime endLocalDateTime = LocalDateTime.parse(endDateTimeString, formatter);

        // Chuyển đổi LocalDateTime thành Timestamp
        Timestamp startTimestamp = Timestamp.valueOf(startLocalDateTime);
        Timestamp endTimestamp = Timestamp.valueOf(endLocalDateTime);

        // Lấy bookPrice mới nhất của cuốn sách và ngày hiệu lực nhỏ hơn ngày hiện tại
        BookPrice bookPrice = bookPriceService.getLatestEffectiveBookPriceByBook(book);

        // Kiểm tra xem giá sách và giá trong BookPrice có khớp nhau không
        if (bookPrice != null && book.getSellPrice() == bookPrice.getSellPrice()) {
            // Nếu khớp, tính giá đã giảm
            double discountedPrice = book.getSellPrice() * (100 - discountPercent) / 100;

            // Cập nhật các giá trị cho đối tượng Discount
            discount.setDiscountPercent(discountPercent); // Lưu số nguyên 5, 10, 20
            discount.setDiscountedPrice(discountedPrice);
            discount.setStartDate(startTimestamp);
            discount.setEndDate(endTimestamp);
            discount.setStatus(status);

            // Lưu đối tượng Discount vào cơ sở dữ liệu
            discountService.saveDiscount(discount);

            // sau khi lưu thành công
            redirectAttributes.addAttribute("message", "Đã cập nhật thành công!");
            redirectAttributes.addAttribute("discountId", discountId);
            return "redirect:/updatediscount";
        } else {
            // Nếu không khớp, thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra: Giá sách và giá trong BookPrice không khớp!");
            return "redirect:/erroradmin";
        }
    }

    private BookResponseModel convertToBookResponseModel(Book book) {
        BookResponseModel bookResponseModel = modelMapper.map(book, BookResponseModel.class);

        bookResponseModel.setCurrentDiscount(null);
        // Lấy đợt giảm giá còn hiệu lực theo sách
        Discount latestActiveDiscount = discountService.getLatestActiveDiscountByBookId(book.getId());

        if (latestActiveDiscount != null)
        {
            // Gán cho Response
            DiscountLeanModel discountLeanModel = modelMapper.map(latestActiveDiscount, DiscountLeanModel.class);
            bookResponseModel.setCurrentDiscount(discountLeanModel);
        }

        return bookResponseModel;
    }

}
