package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookReview;
import com.nhom14.webbookstore.service.BookReviewService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminBookReviewController {
    private BookReviewService bookReviewService;

    @Autowired
    public AdminBookReviewController(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @GetMapping("/managebookreviews")
    public String manageBookReviews(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "isPurchased", required = false) Boolean isPurchased,
            @RequestParam(value = "isPublished", required = false) Boolean isPublished,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model,
            HttpSession session) {

        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem admin đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        Sort sortOrder = sort.equals("asc") ? Sort.by("datePosted").ascending() : Sort.by("datePosted").descending();
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sortOrder);
        Page<BookReview> bookReviewPage = bookReviewService.getFilteredBookReviews(rating, isPurchased, isPublished, keyword, pageable);
        // Tổng số tất cả các đánh giá
        long totalAllBookReviews = bookReviewService.getAllBookReviews().size();


        model.addAttribute("bookReviews", bookReviewPage);
        model.addAttribute("totalAllBookReviews", totalAllBookReviews);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
        params.put("rating", rating);
        params.put("isPurchased", isPurchased);
        params.put("isPublished", isPublished);
        params.put("keyword", keyword);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }


        return "admin/managebookreviews";
    }

    @PostMapping("/approvebookreview")
    public ResponseEntity<String> approveBookReview(@RequestParam("reviewId") int reviewId,
                                                    @RequestParam("isPublished") boolean newIsPublished,
                                                    HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem admin đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/webbookstore/loginadmin");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .headers(headers)
                    .body("User not logged in");
        }

        try {
            BookReview bookReview = bookReviewService.findById(reviewId);
            if (bookReview == null) {
                return new ResponseEntity<>("error", HttpStatus.NOT_FOUND);
            }
            bookReview.setPublished(newIsPublished);
            bookReviewService.update(bookReview);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/admindeletereviewbook/{reviewId}")
//    public String deleteReviewBook(@PathVariable("reviewId") int reviewId,
//                                   HttpSession session,
//                                   RedirectAttributes redirectAttributes) {
//        Account admin = (Account) session.getAttribute("admin");
//
//        // Kiểm tra xem admin đã đăng nhập hay chưa
//        if (admin == null) {
//            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
//            return "redirect:/loginadmin";
//        }
//
//        // Lấy đánh giá cần xóa
//        BookReview review = bookReviewService.findById(reviewId);
//
//        if (review == null) {
//            String message = "Có lỗi vui lòng thử lại";
//            redirectAttributes.addAttribute("message", message);
//            return "redirect:/managebookreviews";
//        }
//
//        // Xóa đánh giá khỏi cơ sở dữ liệu
//        bookReviewService.delete(review);
//
//
//        String message = "Đánh giá đã được xóa thành công!";
//        redirectAttributes.addAttribute("message", message);
//        return "redirect:/managebookreviews";
//    }

    @PostMapping("/admindeletereviewbook/{reviewId}")
    @ResponseBody
    public ResponseEntity<String> deleteReviewBook(@PathVariable("reviewId") int reviewId,
                                                   HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem admin đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/webbookstore/loginadmin");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .headers(headers)
                    .body("User not logged in");
        }

        // Lấy đánh giá cần xóa
        BookReview review = bookReviewService.findById(reviewId);

        if (review == null) {
            return new ResponseEntity<>("Có lỗi vui lòng thử lại", HttpStatus.NOT_FOUND);
        }

        // Xóa đánh giá khỏi cơ sở dữ liệu
        bookReviewService.delete(review);

        return new ResponseEntity<>("Đánh giá đã được xóa thành công!", HttpStatus.OK);
    }



}
