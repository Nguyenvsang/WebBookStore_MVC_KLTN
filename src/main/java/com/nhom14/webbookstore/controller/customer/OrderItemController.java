package com.nhom14.webbookstore.controller.customer;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.OrderItem;
import com.nhom14.webbookstore.service.OrderItemService;
import com.nhom14.webbookstore.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderItemController {

	private OrderItemService orderItemService;
	private OrderService orderService;

	public OrderItemController(OrderItemService orderItemService, OrderService orderService) {
		super();
		this.orderItemService = orderItemService;
		this.orderService = orderService;
	}

	@GetMapping("/vieworderitems")
	public String viewOrderItems(@RequestParam int orderId,
								 HttpSession session,
								 Model model) {
		Account account = (Account) session.getAttribute("account");

		// Kiểm tra xem người dùng đã đăng nhập hay chưa
		if (account == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/customer/loginaccount";
		}

		// Lấy đối tượng Order từ OrderService bằng orderId
		Order order = orderService.getOrderById(orderId);

		// Lấy danh sách OrderItem từ OrderItemService
		List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);

		// Tính số ngày giữa ngày giao hàng và ngày hiện tại
		long daysBetween = -1; // Tức là chưa giao hàng
		if (order.getDeliveryDate() != null){
			LocalDate dateOrderLocalDate = order.getDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate today = LocalDate.now();
			daysBetween = ChronoUnit.DAYS.between(dateOrderLocalDate, today);
		}

		// Đặt đối tượng Order, orderItems và daysBetween vào thuộc tính model để sử dụng trong Thymeleaf
		model.addAttribute("order", order);
		model.addAttribute("orderItems", orderItems);
		model.addAttribute("daysBetween", daysBetween);

		// Trả về tên của view để render ra giao diện
		return "customer/vieworderitems";
	}

}
