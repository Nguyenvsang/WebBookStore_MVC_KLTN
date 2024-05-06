package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;
import com.nhom14.webbookstore.entity.BookPrice;
import com.nhom14.webbookstore.service.BookPriceService;
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
public class AdminBookPriceController {
    private BookPriceService bookPriceService;
    private BookService bookService;

    @Autowired
    public AdminBookPriceController(BookPriceService bookPriceService, BookService bookService) {
        super();
        this.bookPriceService = bookPriceService;
        this.bookService = bookService;
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

        return "admin/updatebookprice";
    }
}
