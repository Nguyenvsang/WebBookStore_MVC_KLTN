package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.FavoriteBook;
import com.nhom14.webbookstore.service.AccountService;
import com.nhom14.webbookstore.service.BookService;
import com.nhom14.webbookstore.service.FavoriteBookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class FavoriteBookController {
    private FavoriteBookService favoriteBookService;
    private AccountService accountService;
    private BookService bookService;

    @Autowired
    public FavoriteBookController(FavoriteBookService favoriteBookService, AccountService accountService, BookService bookService) {
        this.favoriteBookService = favoriteBookService;
        this.accountService = accountService;
        this.bookService = bookService;
    }

    @PostMapping("/adddeletefavoritebook")
    public ResponseEntity<?> addDeleteFavoriteBook(
            @RequestParam("bookId") int bookId,
            HttpSession session
    ) {
        Map<String, String> response = new HashMap<>();
        try {
            Account account = (Account) session.getAttribute("account");

            // Kiểm tra xem người dùng đã đăng nhập hay chưa
            if (account == null) {
                response.put("status", "redirect");
                response.put("location", "/webbookstore/customer/loginaccount");
                return ResponseEntity.ok(response);
            }

            Book book = bookService.getBookById(bookId);
            FavoriteBook favoriteBook = null;

            if (book != null) {
                // Kiểm xem người dùng đã yêu thích sách này chưa
                favoriteBook = favoriteBookService.findByAccountAndBook(account, book);
                if (favoriteBook != null) {
                    // Nếu đã có nghĩa là người dùng đang muốn loại sách khỏi danh sách yêu thích
                    favoriteBookService.delete(favoriteBook);
                    response.put("status", "success");
                    response.put("message", "deleted");
                    return ResponseEntity.ok(response);
                } else {
                    favoriteBook = new FavoriteBook();
                    favoriteBook.setAccount(account);
                    favoriteBook.setBook(book);
                    favoriteBookService.addFavoriteBook(favoriteBook);
                    response.put("status", "success");
                    response.put("message", "added");
                    return ResponseEntity.ok(response);
                }
            } else {
                response.put("status", "error");
                response.put("message", "Không tìm thấy sách.");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Đã xảy ra lỗi. Vui lòng thử lại sau.");
            return ResponseEntity.ok(response);
        }
    }


}
