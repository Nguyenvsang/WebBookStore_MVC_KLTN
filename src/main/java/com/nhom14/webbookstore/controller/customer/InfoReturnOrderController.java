package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.service.InfoReturnOrderService;
import com.nhom14.webbookstore.service.OrderService;
import com.nhom14.webbookstore.service.PaymentStatusService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class InfoReturnOrderController {

    private InfoReturnOrderService infoReturnOrderService;
    private OrderService orderService;
    private PaymentStatusService paymentStatusService;

    @Autowired
    public InfoReturnOrderController(InfoReturnOrderService infoReturnOrderService, OrderService orderService, PaymentStatusService paymentStatusService) {
        this.infoReturnOrderService = infoReturnOrderService;
        this.orderService = orderService;
        this.paymentStatusService = paymentStatusService;
    }

    @GetMapping("/returnorder")
    public String showReasonReturnOrder(@RequestParam int orderId,
                                        HttpSession session,
                                        Model model) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        model.addAttribute("orderId", orderId);
        model.addAttribute("account", account);
        return "customer/reasonreturnorder";
    }

    @PostMapping("/returnorder")
    public String returnOrder(@RequestParam int orderId,
                              @RequestParam("image1") MultipartFile image1,
                              @RequestParam("image2") MultipartFile image2,
                              @RequestParam("image3") MultipartFile image3,
                              @RequestParam("image4") MultipartFile image4,
                              @RequestParam("video1") MultipartFile video1,
                              @RequestParam("video2") MultipartFile video2,
                              @RequestParam("name") String name,
                              @RequestParam("address") String address,
                              @RequestParam("phoneNumber") String phoneNumber,
                              @RequestParam("email") String email,
                                        HttpSession session,
                                        Model model) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        InfoReturnOrder newInfoReturnOrder = new InfoReturnOrder();
        // Cập nhật thông tin cho


        return "customer/reasonreturnorder";
    }
}
