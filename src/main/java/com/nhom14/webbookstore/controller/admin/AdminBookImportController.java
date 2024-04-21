package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.BookImportService;
import com.nhom14.webbookstore.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

        model.addAttribute("bookImport", new BookImport());
        model.addAttribute("allBooks", allBooks);

        return "admin/addbookimport";
    }
}
