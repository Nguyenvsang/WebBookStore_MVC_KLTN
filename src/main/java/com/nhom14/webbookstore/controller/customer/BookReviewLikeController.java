package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.BookReview;
import com.nhom14.webbookstore.entity.BookReviewLike;
import com.nhom14.webbookstore.service.BookReviewLikeService;
import com.nhom14.webbookstore.service.BookReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;

@Controller
public class BookReviewLikeController {
    private BookReviewLikeService bookReviewLikeService;
    private BookReviewService bookReviewService;

    @Autowired

    public BookReviewLikeController(BookReviewLikeService bookReviewLikeService,
                                    BookReviewService bookReviewService) {
        super();
        this.bookReviewLikeService = bookReviewLikeService;
        this.bookReviewService = bookReviewService;
    }

    @PostMapping("likebookreview")
    public ResponseEntity<String> likeBookReview(@RequestParam("reviewId") int reviewId, HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, trả về URL chuyển hướng như một phần của phản hồi
            return ResponseEntity.ok("/webbookstore/customer/loginaccount");
        }

        // Tìm đánh giá dựa trên ID
        BookReview review = bookReviewService.findById(reviewId);
        if (review == null) {
            // Nếu không tìm thấy đánh giá, trả về lỗi
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Review not found");
        }

        // Tìm "thích" dựa trên Account và BookReview
        BookReviewLike like = bookReviewLikeService.findByAccountAndBookReview(account, review);
        if (like != null) {
            // Nếu tìm thấy "thích", xóa nó
            bookReviewLikeService.delete(like);
            return ResponseEntity.ok("unliked");
        } else {
            // Nếu không tìm thấy "thích", tạo một "thích" mới
            like = new BookReviewLike();
            like.setAccount(account);
            like.setBookReview(review);
            like.setLikedAt(new Timestamp(System.currentTimeMillis()));
            bookReviewLikeService.save(like);
            return ResponseEntity.ok("liked");
        }
    }


}
