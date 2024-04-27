package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.BookImportService;
import com.nhom14.webbookstore.service.BookService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminBookImportController {
    private BookImportService bookImportService;
    private BookService bookService;

    @Autowired
    public AdminBookImportController(BookImportService bookImportService, BookService bookService) {
        super();
        this.bookImportService = bookImportService;
        this.bookService = bookService;
    }

    @GetMapping("/managebookimports")
    public String manageBookImports(
            @RequestParam(value = "bookId", required = false) Integer bookId, //lọc theo sách
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword, //từ khóa tìm kiếm
            // Có thể kiếm theo tên sách
            @RequestParam(value = "status", required = false) Integer status, // lọc theo trạng thái
            @RequestParam(value = "sortOption", required = false, defaultValue = "d21") String sortOption,
            //asc- tăng dần-12, desc- giảm dần-21
            //các tùy chọn: d12 (ngày tăng dần), d21(ngày giảm dần), q12 (quantity tăng dần)
            // q21 (quantity giảm dần), p12, p21 (importPrice), rq12, rq21 (remainingQuantity)
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

        // Gọi phương thức getFilteredProfits với các tham số tìm kiếm và lọc
        Page<BookImport> bookImports = bookImportService.getFilteredBookImports(bookId, searchKeyword, status, pageable);
        // Tổng số tất cả các bản ghi lợi nhuận
        long totalAllBookImports = bookImportService.getAllBookImports().size();

        model.addAttribute("bookImports", bookImports);
        model.addAttribute("totalAllBookImports", totalAllBookImports);

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

        // Lấy danh sách tất cả sách để hiển thị trong dropdown
        List<Book> allBooks = bookService.getAllBooks();

        model.addAttribute("allBooks", allBooks);

        return "admin/managebookimports";
    }

    public Sort handleSortOption(String sortOption) {
        Sort sort = Sort.unsorted();
        if (sortOption != null) {
            sort = switch (sortOption) {
                case "d12" -> sort.and(Sort.by("importDate").ascending());
                case "d21" -> sort.and(Sort.by("importDate").descending());
                case "q12" -> sort.and(Sort.by("quantity").ascending());
                case "q21" -> sort.and(Sort.by("quantity").descending());
                case "p12" -> sort.and(Sort.by("importPrice").ascending());
                case "p21" -> sort.and(Sort.by("importPrice").descending());
                case "rq12" -> sort.and(Sort.by("remainingQuantity").ascending());
                case "rq21" -> sort.and(Sort.by("remainingQuantity").descending());
                default -> sort;
            };
        }
        return sort;
    }


    @GetMapping("/addbookimport")
    public String showAddBookImportForm(Model model,
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

        return "admin/addbookimport";
    }

    @PostMapping("/addbookimport")
    public String addBookImport(@RequestParam("bookId") int bookId,
                                @RequestParam("quantity") int quantity,
                                @RequestParam("remainingQuantity") int remainingQuantity,
                                @RequestParam("importPrice") double importPrice,
                                @RequestParam("importDate") String importDate,
                                @RequestParam("importTime") String importTime,
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

        // Kiểm xem sách có bị null không
        if(book == null){
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Gộp ngày và giờ nhập thành một chuỗi, đồng thời thêm giây là 00
        String dateTimeString = importDate + " " + importTime + ":00";

        // Sử dụng định dạng chuẩn cho LocalDateTime.parse()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Chuyển đổi chuỗi thành kiểu LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);

        // Chuyển đổi LocalDateTime thành Timestamp
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        // Tạo đối tượng BookImport và gán các giá trị
        BookImport bookImport = new BookImport();
        bookImport.setBook(book);
        bookImport.setQuantity(quantity);
        bookImport.setRemainingQuantity(remainingQuantity);
        bookImport.setImportPrice(importPrice);
        bookImport.setImportDate(timestamp);
        bookImport.setStatus(status);

        // Lưu đối tượng BookImport vào cơ sở dữ liệu
        bookImportService.saveBookImport(bookImport);

        // Nếu trạng thái là đang bán thì sẽ gán nó cho costPrice, quantity của book
        if(status == 1) {
            book.setCostPrice(importPrice);
            book.setQuantity(quantity);
            bookService.updateBook(book);
        }

        // Redirect hoặc forward sau khi lưu thành công
        //return "redirect:/managebookimports"; // Chuyển hướng đến trang quản lý đợt nhập sách
        redirectAttributes.addAttribute("message", "Đã thêm thành công!");
        return "redirect:/addbookimport";
    }

    @GetMapping("/updatebookimport")
    public String showUpdateBookImportForm(
                                    @RequestParam("bookImportId") Long bookImportId,
                                    RedirectAttributes redirectAttributes,
                                    Model model,
                                    HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        // Lấy BookImport từ id
        BookImport bookImport = bookImportService.getBookImportById(bookImportId);

        if (bookImport == null) {
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Đặt thuộc tính vào model để sử dụng trong view
        model.addAttribute("bookImport", bookImport);

        return "admin/updatebookimport";
    }

    @PostMapping("/updatebookimport")
    public String updateBookImport(@RequestParam("importId") Long importId,
                                   @RequestParam("bookId") int bookId,
                                   @RequestParam("quantity") int quantity,
                                   @RequestParam("remainingQuantity") int remainingQuantity,
                                   @RequestParam("importPrice") double importPrice,
                                   @RequestParam("importDate") String importDate,
                                   @RequestParam("importTime") String importTime,
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

        // Lấy đối tượng BookImport từ id
        BookImport bookImport = bookImportService.getBookImportById(importId);

        // Kiểm xem đợt nhập sách hoặc sách có bị null không
        if(bookImport == null || book == null){
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/erroradmin";
        }

        // Gộp ngày và giờ nhập thành một chuỗi, đồng thời thêm giây là 00
        String dateTimeString = importDate + " " + importTime + ":00";

        // Sử dụng định dạng chuẩn cho LocalDateTime.parse()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Chuyển đổi chuỗi thành kiểu LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);

        // Chuyển đổi LocalDateTime thành Timestamp
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        // Nếu chuyển từ Chưa bán sang đang bán
        // sẽ gán nó cho costPrice, quantity của book
        if(bookImport.getStatus() == 2 && status == 1) {
            book.setCostPrice(importPrice);
            book.setQuantity(quantity);
            bookService.updateBook(book);
        }

        // Cập nhật các giá trị cho đối tượng BookImport
        bookImport.setQuantity(quantity);
        bookImport.setRemainingQuantity(remainingQuantity);
        bookImport.setImportPrice(importPrice);
        bookImport.setImportDate(timestamp);
        bookImport.setStatus(status);

        // Lưu đối tượng BookImport vào cơ sở dữ liệu
        bookImportService.saveBookImport(bookImport);

        // Redirect
        redirectAttributes.addAttribute("message", "Đã cập nhật thành công!");
        redirectAttributes.addAttribute("bookImportId", importId);
        return "redirect:/updatebookimport";
    }


}
