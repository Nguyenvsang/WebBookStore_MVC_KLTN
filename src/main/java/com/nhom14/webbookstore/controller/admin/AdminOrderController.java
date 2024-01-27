package com.nhom14.webbookstore.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Category;
import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.service.BookService;
import com.nhom14.webbookstore.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminOrderController {
	private OrderService orderService;
	private BookService bookService;
	
	@Autowired
	public AdminOrderController(OrderService orderService, BookService bookService) {
		super();
		this.orderService = orderService;
		this.bookService = bookService;
	}
	
	@GetMapping("/manageorders")
	public String manageOrders(@RequestParam(value = "status", required = false) Integer status,
	@RequestParam(value = "search", required = false) String searchKeyword,
	@RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
	Model model,
    HttpSession session) {
		
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    List<Order> orders;
	    int totalOrders;
	    
	    // Số danh mục hiển thị trên mỗi trang	
        int recordsPerPage = 10;
        int start;
        int end;
        int totalPages;
        
        if (status == null || (status == -1)) {
        	orders = orderService.getAllOrders();
        } else {
        	orders = orderService.getOrdersByStatus(status);
        }
        
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
        	orders = orderService.searchOrdersByKeyword(orders, searchKeyword);
        }
        
        // Lấy tổng số lượng đơn hàng
        totalOrders = orders.size();
        
        // Tính toán vị trí bắt đầu và kết thúc của đơn hàng trên trang hiện tại
        start = (currentPage - 1) * recordsPerPage;
        end = Math.min(start + recordsPerPage, totalOrders);
        
        // Lấy danh sách đơn hàng trên trang hiện tại
        List<Order> ordersOnPage = orders.subList(start, end);
        
        // Tính toán số trang
        totalPages = (int) Math.ceil((double) totalOrders / recordsPerPage);
        
        // Tổng số tất cả các đơn hàng
        int totalAllOrders = orderService.getAllOrders().size();
        
        model.addAttribute("orders", ordersOnPage);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalAllOrders", totalAllOrders);
        
        return "admin/manageorders";
	}
	
	@PostMapping("/updateorderstatus")
	public String updateOrderStatus(@RequestParam("orderId") int orderId, 
			@RequestParam("status") int status, 
			HttpSession session) {
	    Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

	    // Lấy đơn hàng từ Service
	    Order order = orderService.getOrderById(orderId);

	    // Cập nhật trạng thái đơn hàng
	    order.setStatus(status);

	    // Cập nhật đơn hàng thông qua Service
	    orderService.updateOrder(order);

	    // Chuyển hướng về trang manageorderitems
	    return "redirect:/manageorderitems?orderId=" + orderId;
	}
}
