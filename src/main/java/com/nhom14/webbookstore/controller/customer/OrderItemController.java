package com.nhom14.webbookstore.controller.customer;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderItemController {

	private OrderItemService orderItemService;
	private OrderService orderService;
	private CartService cartService;
	private BookService bookService;
	private CartItemService cartItemService;

	@Autowired
	public OrderItemController(OrderItemService orderItemService, OrderService orderService, CartService cartService, BookService bookService, CartItemService cartItemService) {
		super();
		this.orderItemService = orderItemService;
		this.orderService = orderService;
        this.cartService = cartService;
        this.bookService = bookService;
        this.cartItemService = cartItemService;
    }

	@GetMapping("/vieworderitems")
	public String viewOrderItems(@RequestParam int orderId,
								 HttpSession session,
								 RedirectAttributes redirectAttributes,
								 HttpServletRequest request,
								 Model model) {
		Account account = (Account) session.getAttribute("account");

		// Kiểm tra xem người dùng đã đăng nhập hay chưa
		if (account == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/customer/loginaccount";
		}

		// Lấy đối tượng Order từ OrderService bằng orderId
		Order order = orderService.getOrderById(orderId);

		// Kiểm xem order có thuộc về người dùng đang đăng nhập không
		if(order.getAccount().getId()!=account.getId()){
			// Thêm thông báo lỗi
			redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
			return "redirect:/customer/error";
		}

		// Lấy danh sách OrderItem từ OrderItemService
		List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);

		// Tính số ngày giữa ngày giao hàng và ngày hiện tại
		long daysBetween = -1; // Tức là chưa giao hàng
		if (order.getDeliveryDate() != null){
			LocalDateTime dateOrderLocalDateTime = order.getDeliveryDate().toLocalDateTime();
			LocalDateTime today = LocalDateTime.now();
			daysBetween = ChronoUnit.DAYS.between(dateOrderLocalDateTime.toLocalDate(), today.toLocalDate());
			// Tính ngày tối đa có thể trả hàng được để hiển thị lên view
			Timestamp lastReturnDate = Timestamp.valueOf(dateOrderLocalDateTime.plusDays(15).toLocalDate().atStartOfDay());
			// Đặt ngày cuối cùng có thể trả hàng vào thuộc tính model để sử dụng trong Thymeleaf
			model.addAttribute("lastReturnDate", lastReturnDate);
		}

		// Đặt đối tượng Order, orderItems và daysBetween vào thuộc tính model để sử dụng trong Thymeleaf
		model.addAttribute("order", order);
		model.addAttribute("orderItems", orderItems);
		model.addAttribute("daysBetween", daysBetween);
		// Lưu URL trang trước đó vào session
		String referer = request.getHeader("Referer");
		String currentUrl = request.getRequestURL().toString();

		// Kiểm tra xem referer có khác với URL hiện tại hay không (tránh trường hợp 1 trang lặp lại)
		// và có chứa cụm "/vieworders" hoặc "/viewaccount" hoặc "/viewnotifications" (tránh trường hợp vượt quá kiểm soát)
		if (referer != null && !referer.equals(currentUrl) && (referer.contains("/vieworders") || referer.contains("/viewaccount") || referer.contains("/viewnotifications"))) {
			session.setAttribute("previousUrl", referer);
		}

		// Trả về tên của view để render ra giao diện
		return "customer/vieworderitems";
	}

	@PostMapping("/repurchase")
	public String repurchase(@RequestParam("orderId") int orderId,
							 HttpSession session,
							 RedirectAttributes redirectAttributes) {
		Account account = (Account) session.getAttribute("account");

		// Kiểm tra xem người dùng đã đăng nhập hay chưa
		if (account == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/customer/loginaccount";
		}

		// Lấy đơn hàng cũ
		Order oldOrder = orderService.getOrderById(orderId);

		// Kiểm tra xem đơn hàng có tồn tại và thuộc về người dùng hiện tại hay không
		if(oldOrder == null || oldOrder.getAccount().getId()!=account.getId()){
			// Thêm thông báo lỗi
			redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
			return "redirect:/customer/error";
		}

		// Lấy danh sách các mặt hàng từ đơn hàng cũ
		List<OrderItem> oldOrderItems = orderItemService.getOrderItemsByOrder(oldOrder);

		// Kiểm tra xem giỏ hàng của người dùng đã tồn tại hay chưa
		Cart cart = cartService.getCartByAccount(account);
		if (cart == null) {
			// Nếu giỏ hàng chưa tồn tại, thêm giỏ hàng mới
			cart = new Cart(account);
			cartService.addCart(cart);
		}

		// Biến để kiểm tra xem có mặt hàng nào không thể thêm vào giỏ hàng hay không
		int unavailableItemCount = 0;

		// Duyệt qua danh sách các mặt hàng
		for (OrderItem oldOrderItem : oldOrderItems) {
			Book book = oldOrderItem.getBook();
			int quantity = oldOrderItem.getQuantity();
			// Nếu sách đã bị ngừng kinh doanh hoặc số lượng trong kho < số lượng yêu cầu
			// thì tăng unavailableItemCount lên và
			// Bỏ qua oldOrderItem này và duyệt cái tiếp theo
			if(book.getStatus() == 0 || book.getQuantity() < quantity){
				unavailableItemCount = unavailableItemCount + 1;
				continue;
			}
			// Nếu sách còn kinh doanh và tạm thời còn số lượng thì
			// Kiểm xem giỏ hàng đã chứa cuốn sách này chưa
			CartItem cartItem = cartItemService.getCartItemByCartAndBook(cart, book);
			if (cartItem == null) {
				// Nếu CartItem chưa tồn tại, tạo mới và thêm vào giỏ hàng
				cartItem = new CartItem(quantity, cart, book);
				cartItemService.addCartItem(cartItem);
			} else {
				// Nếu CartItem đã tồn tại, cộng dồn số lượng
				int currentQuantity = cartItem.getQuantity();
				int newQuantity = currentQuantity + quantity;

				// Kiểm tra số lượng tồn kho
				if (newQuantity > book.getQuantity()) {
					// Số lượng vượt quá số lượng tồn kho,
					// thì tăng unavailableItemCount lên và
					// Bỏ qua oldOrderItem này và duyệt cái tiếp theo
					unavailableItemCount = unavailableItemCount + 1;
					continue;
				}

				cartItem.setQuantity(newQuantity);
				cartItemService.updateCartItem(cartItem);
			}
		}

		String message = "";

		// Kiểm tra xem có mặt hàng nào không thể thêm vào giỏ hàng hay không
		if (unavailableItemCount > 0) {
			if (unavailableItemCount == 1) {
				message = "Không thể thêm sách này vào giỏ hàng vì đã hết hàng hoặc quá số lượng cho phép";
			} else if (unavailableItemCount < oldOrderItems.size()) {
				message = "Không thể thêm một số sách vào giỏ hàng vì đã hết hàng hoặc quá số lượng cho phép";
			} else {
				message = "Các sách này không thể thêm vào giỏ hàng vì đã hết hàng hoặc quá số lượng cho phép";
			}
			redirectAttributes.addAttribute("message", message);
		}

		// Chuyển hướng về trang giỏ hàng
		return "redirect:/viewcart";
	}


}
