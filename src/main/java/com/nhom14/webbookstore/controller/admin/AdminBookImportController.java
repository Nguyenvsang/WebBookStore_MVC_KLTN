package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.BookImportService;
import com.nhom14.webbookstore.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
