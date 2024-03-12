package com.nhom14.webbookstore.controller.admin;

import java.sql.Timestamp;
import java.util.List;

import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.service.InfoReturnOrderService;
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
public class AdminOrderItemController {

	private OrderItemService orderItemService;
	private OrderService orderService;
	private InfoReturnOrderService infoReturnOrderService;
	
	public AdminOrderItemController(OrderItemService orderItemService, OrderService orderService,
									InfoReturnOrderService infoReturnOrderService) {
		super();
		this.orderItemService = orderItemService;
		this.orderService = orderService;
		this.infoReturnOrderService = infoReturnOrderService;
	}

	@GetMapping("/manageorderitems")
	public String manageOrderItems(@RequestParam int orderId,
			HttpSession session, 
			Model model) {
		
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Lấy đối tượng Order từ OrderService bằng orderId
	    Order order = orderService.getOrderById(orderId);
	    
	    // Lấy danh sách OrderItem từ OrderItemService
	    List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);

		// Lấy thông tin trà hàng
		Timestamp returnRequestDate = null;
		InfoReturnOrder infoReturnOrder = infoReturnOrderService.getInfoReturnOrderByOrder(order);
		if(infoReturnOrder != null){
			returnRequestDate = infoReturnOrder.getRequestDate();
		}
	    
	    // Đặt đối tượng Order và orderItems vào thuộc tính model để sử dụng trong Thymeleaf
	    model.addAttribute("order", order);
	    model.addAttribute("orderItems", orderItems);
		model.addAttribute("returnRequestDate", returnRequestDate);
	    
	    // Trả về tên của view để render ra giao diện
	    return "admin/manageorderitems";
	    
	}
	
}
