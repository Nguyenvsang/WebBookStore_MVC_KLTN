package com.nhom14.webbookstore.controller.admin;

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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminNotificationController {
    private NotificationService notificationService;
    private AccountService accountService;
    private ModelMapper modelMapper;

    @Autowired

    public AdminNotificationController(NotificationService notificationService, AccountService accountService, ModelMapper modelMapper) {
        super();
        this.notificationService = notificationService;
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    // Trả về danh sách các thông báo cho admin
    @GetMapping("/adminnotifications")
    public String AdminNotifications(@RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
                                    HttpSession session,
                                    Model model) {
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem admin đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/loginadmin";
        }

        Sort sort = Sort.unsorted();
        sort = sort.and(Sort.by("sentTime").descending());

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        Page<Notification> notifications = notificationService.findAllByAdminReceivers(pageable);
        Page<NotificationResponseModel> notificationResponseModels = notifications.map(this::convertToNotificationResponseModel);

        model.addAttribute("notifications", notificationResponseModels);

        return "admin/adminnotifies";
    }

    @PostMapping("/adminmarknotifyasread/{notificationId}")
    public ResponseEntity<?> adminMarkNotificationAsRead(@PathVariable long notificationId, HttpSession session) {
        // Lấy thông tin admin dùng từ HttpSession
        Account admin = (Account) session.getAttribute("admin");

        // Kiểm tra xem admin đã đăng nhập hay chưa
        if (admin == null) {
            // Nếu chưa đăng nhập, trả về lỗi
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Admin not logged in\"}");
        }

        // Lấy thông báo từ cơ sở dữ liệu
        Notification notification = notificationService.getNotificationById(notificationId);

        // Kiểm tra xem thông báo có tồn tại hay không
        if (notification == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Đánh dấu thông báo là đã đọc
        notification.setStatus(1); // Đã đọc
        notificationService.save(notification);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/adminmarkallnotificationsasread")
    public ResponseEntity<?> adminMarkAllNotificationsAsRead(HttpSession session) {
        Account admin = (Account) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not logged in");
        }

        notificationService.markAllAsReadForAdmins();
        return ResponseEntity.ok().build();
    }

    private NotificationResponseModel convertToNotificationResponseModel(Notification notification) {
        NotificationResponseModel notificationResponseModel = modelMapper.map(notification, NotificationResponseModel.class);
        return notificationResponseModel;
    }
}
