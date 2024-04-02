package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.BookReviewService;
import com.nhom14.webbookstore.service.BookService;
import com.nhom14.webbookstore.service.OrderItemService;
import com.nhom14.webbookstore.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class BookReviewController {
    private BookReviewService bookReviewService;
    private BookService bookService;
    private OrderService orderService;
    private OrderItemService orderItemService;

    @Autowired
    public BookReviewController(BookReviewService bookReviewService, BookService bookService,
                                OrderService orderService,
                                OrderItemService orderItemService) {
        super();
        this.bookReviewService = bookReviewService;
        this.bookService = bookService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @PostMapping("/addreviewbook")
    public String addReviewBook(@RequestParam("bookId") int bookId,
                                @RequestParam("rating") int rating,
                                @RequestParam("comment") String comment,
                                //@RequestParam(value = "isPurchased", defaultValue = "false") boolean isPurchased,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        // Lấy thông tin người dùng từ session
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            // Nếu người dùng chưa đăng nhập, chuyển hướng họ đến trang đăng nhập
            return "redirect:/login";
        }

        // Lấy sách theo id
        Book book = bookService.getBookById(bookId);

        // Kiểm tra xem người dùng đã thực sự mua cuốn sách này chưa
        boolean hasPurchased = false;
        List<Order> orders = orderService.getOrdersByAccount(account);
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getBook().getId() == bookId) {
                    hasPurchased = true;
                    break;
                }
            }
            if (hasPurchased) {
                break;
            }
        }
//
//        // Nếu người dùng chưa mua cuốn sách này nhưng lại khẳng định đã mua
//        if (isPurchased && !hasPurchased) {
//            String message = "Bạn chưa mua cuốn sách này. Vui lòng kiểm tra lại.";
//            redirectAttributes.addAttribute("message", message);
//            return "redirect:/detailbook/" + bookId;
//        }
//
//        // Nếu người dùng đã mua cuốn sách này nhưng lại không khẳng định đã mua
//        if (!isPurchased && hasPurchased) {
//            String message = "Bạn đã mua cuốn sách này. Vui lòng kiểm tra lại.";
//            redirectAttributes.addAttribute("message", message);
//            return "redirect:/detailbook/" + bookId;
//        }

        // Tạo một đánh giá mới
        BookReview review = new BookReview();
        review.setAccount(account);
        review.setBook(book);
        review.setRating(rating);
        review.setComment(comment);
        review.setPurchased(hasPurchased);
        review.setPublished(false); // Chưa được duyệt đăng
        review.setDatePosted(new Timestamp(System.currentTimeMillis()));

        // Lưu đánh giá vào cơ sở dữ liệu
        bookReviewService.save(review);

        // Chuyển hướng người dùng đến trang chi tiết sách
        String message = "Cảm ơn bạn đã đánh giá, đánh giá của bạn đã được lưu thành công và sẽ được đăng sau khi chúng tôi xét duyệt!";
        redirectAttributes.addAttribute("message", message);
        return "redirect:/detailbook/" + bookId;
    }

    @PostMapping("/editreviewbook")
    public String editReviewBook(@RequestParam("bookId") int bookId,
                                 @RequestParam("rating") int rating,
                                 @RequestParam("comment") String comment,
                                 //@RequestParam(value = "isPurchased", defaultValue = "false") boolean isPurchased,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // Lấy thông tin người dùng từ session
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            // Nếu người dùng chưa đăng nhập, chuyển hướng họ đến trang đăng nhập
            return "redirect:/login";
        }

        // Lấy sách theo id
        Book book = bookService.getBookById(bookId);

        // Lấy đánh giá hiện tại của người dùng
        BookReview review = bookReviewService.getReviewByAccountAndBook(account, book);

        if (review == null) {
            // Nếu người dùng chưa viết đánh giá, chuyển hướng họ đến trang chi tiết sách
            String message = "Bạn chưa viết đánh giá cho cuốn sách này.";
            redirectAttributes.addAttribute("message", message);
            return "redirect:/detailbook/" + bookId;
        }

        boolean hasPurchased = review.isPurchased();
        // Nếu người dùng chưa mua cuốn sách này nhưng lại khẳng định đã mua
//        if (isPurchased && !hasPurchased) {
//            String message = "Bạn chưa mua cuốn sách này. Vui lòng kiểm tra lại.";
//            redirectAttributes.addAttribute("message", message);
//            return "redirect:/detailbook/" + bookId;
//        }
//
//        // Nếu người dùng đã mua cuốn sách này nhưng lại không khẳng định đã mua
//        if (!isPurchased && hasPurchased) {
//            String message = "Bạn đã mua cuốn sách này. Vui lòng kiểm tra lại.";
//            redirectAttributes.addAttribute("message", message);
//            return "redirect:/detailbook/" + bookId;
//        }

        // Cập nhật đánh giá
        review.setRating(rating);
        review.setComment(comment);
        review.setPurchased(hasPurchased);
        review.setPublished(false); // Chưa được duyệt đăng
        review.setDatePosted(new Timestamp(System.currentTimeMillis()));

        // Lưu đánh giá vào cơ sở dữ liệu
        bookReviewService.save(review);

        // Chuyển hướng người dùng đến trang chi tiết sách
        String message = "Đánh giá của bạn đã được cập nhật thành công! Và sẽ được hiển thị sau khi chúng tôi xét duyệt";
        redirectAttributes.addAttribute("message", message);
        return "redirect:/detailbook/" + bookId;
    }

    @PostMapping("/deletereviewbook/{bookId}")
    public String deleteReviewBook(@PathVariable("bookId") int bookId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        // Lấy thông tin người dùng từ session
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            // Nếu người dùng chưa đăng nhập, chuyển hướng họ đến trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        // Lấy sách theo id
        Book book = bookService.getBookById(bookId);

        // Lấy đánh giá hiện tại của người dùng
        BookReview review = bookReviewService.getReviewByAccountAndBook(account, book);

        if (review == null) {
            // Nếu người dùng chưa viết đánh giá, chuyển hướng họ đến trang chi tiết sách
            String message = "Bạn chưa viết đánh giá cho cuốn sách này.";
            redirectAttributes.addAttribute("message", message);
            return "redirect:/detailbook/" + bookId;
        }

        // Xóa đánh giá khỏi cơ sở dữ liệu
        bookReviewService.delete(review);

        // Chuyển hướng người dùng đến trang chi tiết sách
        String message = "Đánh giá của bạn đã được xóa thành công!";
        redirectAttributes.addAttribute("message", message);
        return "redirect:/detailbook/" + bookId;
    }



}
