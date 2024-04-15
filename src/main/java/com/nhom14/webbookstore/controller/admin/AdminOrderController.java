package com.nhom14.webbookstore.controller.admin;

import java.sql.Timestamp;
import java.util.List;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminOrderController {
	private OrderService orderService;
	private BookService bookService;
	private PaymentStatusService paymentStatusService;
	private OrderItemService orderItemService;
	private BookImportService bookImportService;
	private RevenueService revenueService;
	private ProfitService profitService;
	
	@Autowired
	public AdminOrderController(OrderService orderService, BookService bookService,
                                PaymentStatusService paymentStatusService, OrderItemService orderItemService, BookImportService bookImportService, RevenueService revenueService, ProfitService profitService) {
		super();
		this.orderService = orderService;
		this.bookService = bookService;
		this.paymentStatusService = paymentStatusService;
        this.orderItemService = orderItemService;
        this.bookImportService = bookImportService;
        this.revenueService = revenueService;
        this.profitService = profitService;
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
									HttpSession session,
									RedirectAttributes redirectAttributes) {
	    Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

	    // Lấy đơn hàng từ Service
	    Order order = orderService.getOrderById(orderId);

		// Nếu là trạng thái đã giao thì thêm ngày giao hàng vô
		// Kèm theo thanh toán sẽ tự động chuyển sang Đã thanh toán
		if (status == 3) {
			order.setDeliveryDate(new Timestamp(System.currentTimeMillis()));
			// Lấy trạng thái thanh toán dựa trên order
			PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
			// Cập nhật trạng thái thanh toán mới
			paymentStatus.setStatus(1); // Đã thanh toán
			paymentStatusService.updatePaymentStatus(paymentStatus);
		}

		//  Nếu là trạng thái xử lý trả hàng thì TTTT tự động chuyển qua xử lý hoàn tiền
		if (status == 6) {
			// Lấy trạng thái thanh toán dựa trên order
			PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
			// Cập nhật trạng thái thanh toán mới
			paymentStatus.setStatus(2); // Xử lý hoàn tiền
			paymentStatusService.updatePaymentStatus(paymentStatus);
		}

		// Nếu là Trả hàng thành công thì TTTT tự động chuyển qua Đã hoàn tiền
		if (status == 7) {
			// Lấy trạng thái thanh toán dựa trên order
			PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
			// Cập nhật trạng thái thanh toán mới
			paymentStatus.setStatus(3); // Đã hoàn tiền
			paymentStatusService.updatePaymentStatus(paymentStatus);
		}

		// Khi đơn hàng có trạng thái 10 (đã nhận hàng),
		// hệ thống sẽ tự động cập nhật trạng thái thanh toán của đơn hàng đó thành 1 (đã thanh toán).
		// Từ đây, doanh thu và lợi nhuận sẽ được tính cho đơn hàng đó.
		if (status == 10) {
			// Kiểm tra xem Order đã tồn tại Revenue chưa
			Revenue existingRevenue = revenueService.getRevenueByOrder(order);
			if (existingRevenue == null) {
				// Tính toán doanh thu
				double revenueAmount = order.getTotalPrice();

				// Tạo bản ghi doanh thu mới
				Revenue revenue = new Revenue();
				revenue.setOrder(order);
				revenue.setRevenue(revenueAmount);
				revenue.setDate(new Timestamp(System.currentTimeMillis()));
				revenueService.addRevenue(revenue);
			}

			// Lấy tất cả các OrderItem của Order
			List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);

			for (OrderItem orderItem : orderItems) {
				// Kiểm tra xem OrderItem đã tồn tại Profit chưa
				Profit existingProfit = profitService.getProfitByOrderItem(orderItem);
				if (existingProfit == null) {
					// Lấy Book tương ứng với OrderItem
					Book book = orderItem.getBook();

					// Lấy đợt nhập sách mới nhất của Book
					BookImport latestBookImport = bookImportService.getLatestBookImportByBook(book);

					if (latestBookImport == null){
						redirectAttributes.addAttribute("message", "Không tìm thấy đợt nhập sách!");
						// Chuyển hướng về trang manageorderitems
						return "redirect:/manageorderitems?orderId=" + orderId;
					}

					// Kiểm tra xem số lượng còn lại có đủ để đáp ứng yêu cầu của OrderItem hay không
					if (latestBookImport.getRemainingQuantity() >= orderItem.getQuantity()) {
						// Trừ remaining_quantity và cập nhật BookImport
						latestBookImport.setRemainingQuantity(latestBookImport.getRemainingQuantity() - orderItem.getQuantity());
						bookImportService.updateBookImport(latestBookImport);
					} else {
						// Xử lý trường hợp không đủ sách...
						redirectAttributes.addAttribute("message", "Có lỗi xảy ra, vui lòng thử lại sau!" + latestBookImport.getRemainingQuantity() +"----------" + orderItem.getQuantity());
						// Chuyển hướng về trang manageorderitems
						return "redirect:/manageorderitems?orderId=" + orderId;
					}

					orderItem.setCostPrice(latestBookImport.getImportPrice());
					orderItemService.updateOrderItem(orderItem);

					// Tính toán lợi nhuận
					double profitAmount = (orderItem.getSellPrice() - latestBookImport.getImportPrice()) * orderItem.getQuantity();

					// Tạo bản ghi lợi nhuận mới
					Profit profit = new Profit();
					profit.setOrderItem(orderItem);
					profit.setCostPrice(latestBookImport.getImportPrice());
					profit.setSellPrice(orderItem.getSellPrice());
					profit.setProfit(profitAmount);
					profit.setDate(new Timestamp(System.currentTimeMillis()));
					profitService.addProfit(profit);
				}
			}
			// Lấy trạng thái thanh toán dựa trên order
			PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
			// Cập nhật trạng thái thanh toán mới
			paymentStatus.setStatus(1); // Đã thanh toán
			paymentStatusService.updatePaymentStatus(paymentStatus);
		}

	    // Cập nhật trạng thái đơn hàng
	    order.setStatus(status);

	    // Cập nhật đơn hàng thông qua Service
	    orderService.updateOrder(order);

	    // Chuyển hướng về trang manageorderitems
	    return "redirect:/manageorderitems?orderId=" + orderId;
	}

	@PostMapping("/updatepaymentstatus")
	public String updatePaymentStatus(@RequestParam("orderId") int orderId,
									@RequestParam("paymentstatus") int paymentstatus,
									HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

		// Kiểm tra xem admin đã đăng nhập hay chưa
		if (admin == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/loginadmin";
		}

		// Lấy đơn hàng từ Service
		Order order = orderService.getOrderById(orderId);
		// Lấy trạng thái thanh toán dựa trên order
		PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
		// Cập nhật trạng thái thanh toán mới
		paymentStatus.setStatus(paymentstatus);
		paymentStatusService.updatePaymentStatus(paymentStatus);

		// Chuyển hướng về trang manageorderitems
		return "redirect:/manageorderitems?orderId=" + orderId;
	}
}
