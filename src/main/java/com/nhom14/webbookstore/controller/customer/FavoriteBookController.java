package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.Category;
import com.nhom14.webbookstore.entity.FavoriteBook;
import com.nhom14.webbookstore.service.AccountService;
import com.nhom14.webbookstore.service.BookService;
import com.nhom14.webbookstore.service.CategoryService;
import com.nhom14.webbookstore.service.FavoriteBookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FavoriteBookController {
    private FavoriteBookService favoriteBookService;
    private AccountService accountService;
    private BookService bookService;
    private CategoryService categoryService;

    @Autowired
    public FavoriteBookController(FavoriteBookService favoriteBookService, AccountService accountService, BookService bookService, CategoryService categoryService) {
        this.favoriteBookService = favoriteBookService;
        this.accountService = accountService;
        this.bookService = bookService;
        this.categoryService = categoryService;
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

    @GetMapping("/viewfavoritebooks")
    public String viewFavoriteBooks(
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "search", required = false) String searchKeyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(value = "size", required = false, defaultValue = "12") Integer pageSize,
            @RequestParam(value = "pricemin", required = false) Double priceMin, // Lọc sách theo khoảng giá
            @RequestParam(value = "pricemax", required = false) Double priceMax, // Lọc sách theo khoảng giá
            @RequestParam(value = "priceoption", required = false) Integer priceOption,
            // Sếp sách tăng dần nếu giá trị priceOption là 12, giảm dần nếu giá trị là 21
            @RequestParam(value = "nameoption", required = false) Integer nameOption,
            // Xếp sách theo chữ cái đầu tiên của tên sách tăng dần từ A đến Y nếu nameOption là 12 và ngược lại
            @RequestParam(value = "publisher", required = false) String publisher, // Lọc sách theo tên nhà xuất bản
            Model model,
            HttpSession session) {

        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        Sort sort = Sort.unsorted();
        if (priceOption != null) {
            if (priceOption == 12) {
                sort = sort.and(Sort.by("book.sellPrice").ascending());
            } else if (priceOption == 21) {
                sort = sort.and(Sort.by("book.sellPrice").descending());
            }
        }
        if (nameOption != null) {
            if (nameOption == 12) {
                sort = sort.and(Sort.by("book.name").ascending());
            } else if (nameOption == 21) {
                sort = sort.and(Sort.by("book.name").descending());
            }
        }
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        // Gọi phương thức getFilteredFavoriteBooks với các tham số tìm kiếm và lọc
        Page<Book> favoriteBooks = bookService.getFilteredFavoriteBooks(account.getId(), categoryId, searchKeyword, priceMin, priceMax, publisher, pageable);

        model.addAttribute("favoriteBooks", favoriteBooks);
        List<Category> categories = categoryService.getActiveCategories();
        model.addAttribute("categories", categories);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("search", searchKeyword);
        params.put("pricemin", priceMin);
        params.put("pricemax", priceMax);
        params.put("priceoption", priceOption);
        params.put("nameoption", nameOption);
        params.put("publisher", publisher);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        return "customer/viewfavoritebooks";
    }





}
