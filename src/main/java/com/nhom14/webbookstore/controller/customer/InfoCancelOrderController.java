package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.InfoCancelOrder;
import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.PaymentStatus;
import com.nhom14.webbookstore.service.InfoCancelOrderService;
import com.nhom14.webbookstore.service.OrderService;
import com.nhom14.webbookstore.service.PaymentStatusService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;

@Controller
public class InfoCancelOrderController {
    private InfoCancelOrderService infoCancelOrderService;
    private OrderService orderService;
    private PaymentStatusService paymentStatusService;

    @Autowired

    public InfoCancelOrderController(InfoCancelOrderService infoCancelOrderService, OrderService orderService, PaymentStatusService paymentStatusService) {
        this.infoCancelOrderService = infoCancelOrderService;
        this.orderService = orderService;
        this.paymentStatusService = paymentStatusService;
    }

    @GetMapping("/cancelorder")
    public String showReasonCancelOrder(@RequestParam int orderId,
                                 HttpSession session,
                                 Model model) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        model.addAttribute("orderId", orderId);
        return "customer/reasoncancelorder";
    }

    @PostMapping("/cancelorder")
    public String cancelOrder(@RequestParam int orderId,
                              @RequestParam(value = "cancelReason", required = false) Integer type,
                              @RequestParam(value = "otherReason", required = false) String otherType,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        // Lấy đối tượng Order từ OrderService bằng orderId
        Order order = orderService.getOrderById(orderId);
        // Đặt trạng thái đơn hàng là đã hủy
        order.setStatus(4); // Đã hủy đơn
        // Lưu vào CSDL
        orderService.updateOrder(order);

        //Tạo đối tượng thông tin hủy đơn
        InfoCancelOrder infoCancelOrder = new InfoCancelOrder();
        infoCancelOrder.setOrder(order);
        infoCancelOrder.setType(type);
        infoCancelOrder.setOtherType(otherType);
        infoCancelOrder.setDate(new Timestamp(System.currentTimeMillis())); // Thêm ngày hủy đơn thành công
        // Lưu vào CSDL
        infoCancelOrderService.addInfoCancelOrder(infoCancelOrder);

        //Lấy đối tượng PaymentStatus
        PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
        //Lấy trạng thái hiện tại của nó
        int statusofpaymentstatus = paymentStatus.getStatus();
        //Nếu trạng thái là chưa thanh toán thì đặt trạng thái thanh toán là không cần thanh toán
        if (statusofpaymentstatus == 0) {
            paymentStatus.setStatus(4);
        }
        //Nếu trạng thái là đã thanh toán thì đặt trạng thái thanh toán là xử lý hoàn tiền
        else if (statusofpaymentstatus == 1) {
            paymentStatus.setStatus(2);
        }
        // Lưu vào CSDL
        paymentStatusService.updatePaymentStatus(paymentStatus);

        redirectAttributes.addAttribute("message", "Đã hủy đơn thành công!");
        redirectAttributes.addAttribute("orderId", order.getId());
        return "redirect:/vieworderitems";
    }
}
