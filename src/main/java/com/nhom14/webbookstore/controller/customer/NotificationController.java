package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Notification;
import com.nhom14.webbookstore.model.response_model.NotificationResponseModel;
import com.nhom14.webbookstore.service.AccountService;
import com.nhom14.webbookstore.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@Controller
public class NotificationController {
    private NotificationService notificationService;
    private AccountService accountService;
    private ModelMapper modelMapper;

    @Autowired
    public NotificationController(NotificationService notificationService, AccountService accountService, ModelMapper modelMapper) {
        super();
        this.notificationService = notificationService;
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    // Nhận thông báo mới
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(HttpSession session) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        // Giả lập thời gian chờ cho đến khi có thông báo mới
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Lấy thông báo mới từ cơ sở dữ liệu
        Notification notification = notificationService.getNewNotification(account);


        if (notification == null) {
            return ResponseEntity.noContent().build();
        }

        NotificationResponseModel notificationResponseModel = modelMapper.map(notification, NotificationResponseModel.class);

        return ResponseEntity.ok(notificationResponseModel);
    }

    @PostMapping("/marknotifyasread/{notificationId}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable long notificationId, HttpSession session) {
        // Lấy thông tin người dùng từ HttpSession
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, trả về lỗi
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"User not logged in\"}");
        }

        // Lấy thông báo từ cơ sở dữ liệu
        Notification notification = notificationService.getNotificationById(notificationId);

        // Kiểm tra xem thông báo có tồn tại hay không
        if (notification == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Kiểm tra xem người dùng hiện tại có phải là người nhận thông báo hay không
        if (notification.getReceiver().getId() != account.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"User not authorized to mark this notification as read\"}");
        }

        // Đánh dấu thông báo là đã đọc
        notification.setStatus(1); // Đã đọc
        notificationService.save(notification);

        return ResponseEntity.ok().build();
    }

    // Trả về danh sách các thông báo cho người dùng hiện tại
//    @GetMapping("/viewnotifications")
//    public ResponseEntity<?> viewNotifications(@RequestParam(defaultValue = "0") int page,
//                                             @RequestParam(defaultValue = "10") int size,
//                                             HttpSession session) {
//        // Lấy thông tin người dùng từ HttpSession
//        Account account = (Account) session.getAttribute("account");
//
//        // Kiểm tra xem người dùng (cũng là người gửi/kích hoạt thông báo)  đã đăng nhập hay chưa
//        if (account == null) {
//            // Nếu chưa đăng nhập, trả về lỗi
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
//        }
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Notification> notifications = notificationService.findAllByAccountReceived(account, pageable);
//        Page<NotificationResponseModel> responses = notifications.map(this::convertToNotificationResponseModel);
//
//        return ResponseEntity.ok(responses);
//    }

    // Trả về danh sách các thông báo cho người dùng hiện tại
    @GetMapping("/viewnotifications")
    public String viewNotifications(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpSession session,
                                               Model model) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            return "redirect:/customer/loginaccount";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.findAllByAccountReceived(account, pageable);
        Page<NotificationResponseModel> notificationResponseModels = notifications.map(this::convertToNotificationResponseModel);

        model.addAttribute("notifications", notificationResponseModels);

        return "customer/viewnotifies";
    }

    private NotificationResponseModel convertToNotificationResponseModel(Notification notification) {
        NotificationResponseModel notificationResponseModel = modelMapper.map(notification, NotificationResponseModel.class);
        return notificationResponseModel;
    }

}
