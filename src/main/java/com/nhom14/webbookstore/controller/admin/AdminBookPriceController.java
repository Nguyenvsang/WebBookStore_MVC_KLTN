package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;
import com.nhom14.webbookstore.entity.BookPrice;
import com.nhom14.webbookstore.service.BookPriceService;
import com.nhom14.webbookstore.service.BookService;
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

@Controller
public class AdminBookPriceController {
    private BookPriceService bookPriceService;
    private BookService bookService;

    @Autowired
    public AdminBookPriceController(BookPriceService bookPriceService, BookService bookService) {
        super();
        this.bookPriceService = bookPriceService;
        this.bookService = bookService;
    }

    @GetMapping("/managebookprices")
    public String manageBookPrices(
            @RequestParam(value = "bookId", required = false) Integer bookId, //lọc theo sách
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword, //từ khóa tìm kiếm
            // Có thể kiếm theo tên sách
            @RequestParam(value = "sortOption", required = false, defaultValue = "d21") String sortOption,
            //asc- tăng dần-12, desc- giảm dần-21
            //các tùy chọn: d12 (ngày tăng dần), d21(ngày giảm dần),
            // s12, s21 (sellPrice)
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

        // Gọi phương thức getFilteredBookPrices với các tham số tìm kiếm và lọc
        Page<BookPrice> bookPrices = bookPriceService.getFilteredBookPrices(bookId, searchKeyword, pageable);
        // Tổng số tất cả các lần gán giá bán
        long totalAllBookPrices = bookPriceService.getAllBookPrices().size();

        model.addAttribute("bookPrices", bookPrices);
        model.addAttribute("totalAllBookPrices", totalAllBookPrices);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
        params.put("bookId", bookId);
        params.put("searchKeyword", searchKeyword);
        params.put("sortOption", sortOption);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        // Lấy danh sách tất cả sách để hiển thị trong dropdown
        List<Book> allBooks = bookService.getAllBooks();

        model.addAttribute("allBooks", allBooks);

        return "admin/managebookprices";
    }

    public Sort handleSortOption(String sortOption) {
        Sort sort = Sort.unsorted();
        if (sortOption != null) {
            sort = switch (sortOption) {
                case "d12" -> sort.and(Sort.by("effectiveDate").ascending());
                case "d21" -> sort.and(Sort.by("effectiveDate").descending());
                case "p12" -> sort.and(Sort.by("sellPrice").ascending());
                case "p21" -> sort.and(Sort.by("sellPrice").descending());
                default -> sort;
            };
        }
        return sort;
    }

    @GetMapping("/addbookprice")
    public String showAddBookPriceForm(Model model,
                                        HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Lấy danh sách tất cả sách để hiển thị trong dropdown
        List<Book> allBooks = bookService.getAllBooks();

        model.addAttribute("allBooks", allBooks);

        return "admin/addbookprice";
    }

    @PostMapping("/addbookprice")
    public String addBookPrice(@RequestParam("bookId") int bookId,
                                @RequestParam("sellPrice") double sellPrice,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }
        // Lấy sách
        Book book = bookService.getBookById(bookId);

        // Kiểm xem sách có bị null không
        if(book == null){
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Tạo đối tượng BookPrice và gán các giá trị
        BookPrice bookPrice = new BookPrice();
        bookPrice.setBook(book);
        bookPrice.setSellPrice(sellPrice);
        bookPrice.setEffectiveDate(new Timestamp(System.currentTimeMillis()));

        // Lưu đối tượng BookPrice vào cơ sở dữ liệu
        bookPriceService.saveBookPrice(bookPrice);

        // Cập nhật giá bán cho sách
        if(sellPrice != book.getSellPrice()) {
            book.setSellPrice(sellPrice);
            bookService.updateBook(book);
        }

        // sau khi lưu thành công
        redirectAttributes.addAttribute("message", "Đã thêm thành công!");
        return "redirect:/addbookprice";
    }

    @GetMapping("/updatebookprice")
    public String showUpdateBookPriceForm(
            @RequestParam("bookPriceId") Long bookPriceId,
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

        // Lấy BookPrice từ id
        BookPrice bookPrice = bookPriceService.getBookPriceById(bookPriceId);

        if (bookPrice == null) {
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Đặt thuộc tính vào model để sử dụng trong view
        model.addAttribute("bookPrice", bookPrice);

        // Lưu URL trang trước đó vào session
        String referer = request.getHeader("Referer");
        String currentUrl = request.getRequestURL().toString();

        // Kiểm tra xem referer có khác với URL hiện tại hay không (tránh trường hợp 1 trang lặp lại)
        // và có chứa cụm "/managebookprices" hoặc "/managedetailbook" (tránh trường hợp vượt quá kiểm soát)
        if (referer != null && !referer.equals(currentUrl) && (referer.contains("/managebookprices") || referer.contains("/managedetailbook"))) {
            session.setAttribute("previousUrl", referer);
        }

        return "admin/updatebookprice";
    }

    @PostMapping("/updatebookprice")
    public String updateBookPrice(@RequestParam("bookPriceId") Long bookPriceId,
                                   @RequestParam("bookId") int bookId,
                                   @RequestParam("sellPrice") double sellPrice,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Lấy sách
        Book book = bookService.getBookById(bookId);

        // Lấy đối tượng BookPrice từ id
        BookPrice bookPrice = bookPriceService.getBookPriceById(bookPriceId);

        // Kiểm xem giá bán sách hoặc sách có bị null không
        if(bookPrice == null || book == null){
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Cập nhật giá bán cho sách
        if(sellPrice != book.getSellPrice()) {
            book.setSellPrice(sellPrice);
            bookService.updateBook(book);
        }

        // Cập nhật các giá trị cho đối tượng BookPrice
        bookPrice.setSellPrice(sellPrice);

        // Lưu đối tượng BookPrice vào cơ sở dữ liệu
        bookPriceService.saveBookPrice(bookPrice);

        // Redirect
        redirectAttributes.addAttribute("message", "Đã cập nhật thành công!");
        redirectAttributes.addAttribute("bookPriceId", bookPriceId);
        return "redirect:/updatebookprice";
    }
}
