package com.nhom14.webbookstore.controller.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.Cart;
import com.nhom14.webbookstore.entity.CartItem;
import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.entity.OrderItem;
import com.nhom14.webbookstore.service.BookService;
import com.nhom14.webbookstore.service.CartItemService;
import com.nhom14.webbookstore.service.CartService;
import com.nhom14.webbookstore.service.OrderItemService;
import com.nhom14.webbookstore.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {

	private OrderService orderService;
	private OrderItemService orderItemService;
	private CartService cartService;
	private CartItemService cartItemService;
	private BookService bookService;

	@Autowired
	public OrderController(OrderService orderService, 
			OrderItemService orderItemService, 
			CartService cartService, 
			CartItemService cartItemService,
			BookService bookService) {
		super();
		this.orderService = orderService;
		this.orderItemService = orderItemService;
		this.cartService = cartService;
		this.cartItemService = cartItemService;
		this.bookService = bookService;
		
	}
	
	@GetMapping("/shippinginformation")
    public String shippingInformation(@RequestParam(value = "totalAmount", required = false) Double totalAmount,
    		//Double chấp nhận totalAmount là null còn double thì không
    		HttpSession session, 
    		Model model) {

        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        // Kiểm tra xem giỏ hàng có hàng không
        Cart cart = cartService.getCartByAccount(account);
        List<CartItem> cartItems = cartItemService.getCartItemsByCart(cart);
        if (cartItems.isEmpty() || totalAmount == null) {
            return "redirect:/viewcart";
        }

        model.addAttribute("account", account);
        model.addAttribute("totalAmount", totalAmount);

        return "customer/shippinginformation";
    }
	
	@PostMapping("/placeorder")
    public String placeOrder(HttpSession session, 
    		@RequestParam("name") String name, 
    		@RequestParam("address") String address, 
    		@RequestParam("phoneNumber") String phoneNumber, 
    		@RequestParam("email") String email,
    		Model model,
    		RedirectAttributes redirectAttributes) {
        // Kiểm tra đăng nhập
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        // Lấy giỏ hàng dựa trên tài khoản người dùng
        Cart cart = cartService.getCartByAccount(account);
        List<CartItem> cartItems = cartItemService.getCartItemsByCart(cart);
        if (cartItems.isEmpty()) {
            // Chuyển hướng nếu giỏ hàng trống
            return "redirect:/viewcart";
        }

        // Tính toán tổng số tiền trong giỏ hàng
        double totalAmount = calculateTotalAmount(cartItems);

        // Tạo đối tượng đơn hàng
        Order order = new Order();
        order.setDateOrder(new Date());
        order.setTotalPrice(totalAmount);
        order.setName(name);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setAccount(cart.getAccount());
        order.setStatus(0);

        // Thêm đơn hàng vào cơ sở dữ liệu
        orderService.addOrder(order);

        // Lấy ID của đơn hàng vừa thêm
        Order lastOrder = orderService.getLastOrder(cart.getAccount());

        // Tạo danh sách các mục đơn hàng (OrderItem) từ giỏ hàng
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            Book book = cartItem.getBook();
            double price = cartItem.getQuantity() * book.getSellPrice();
            orderItem.setPrice(price);
            orderItem.setBook(book);
            orderItem.setOrder(lastOrder);

            // Thêm mục đơn hàng vào cơ sở dữ liệu
            orderItemService.addOrderItem(orderItem);

            // Cập nhật số lượng sách và kiểm tra trạng thái
            int remainingQuantity = book.getQuantity() - cartItem.getQuantity();
            if (remainingQuantity <= 0) {
            	book.setQuantity(0);
            	book.setStatus(0);
            } else {
            	book.setQuantity(remainingQuantity);
            }
            bookService.updateBook(book);
            
            // Xóa mục giỏ hàng khỏi giỏ hàng
            cartItemService.deleteCartItem(cartItem);

            
        }

        // Chuyển hướng đến trang xác nhận đơn hàng với id của đơn hàng
        redirectAttributes.addAttribute("orderId", lastOrder.getId());

        return "redirect:/orderconfirmation";
    }
	
	private double calculateTotalAmount(List<CartItem> cartItems) {
        double totalAmount = 0.0;
        for (CartItem cartItem : cartItems) {
            totalAmount += cartItem.getQuantity() * cartItem.getBook().getSellPrice();
        }
        return totalAmount;
    }
	
	@GetMapping("/vieworders")
	public String viewOrders(Model model, HttpSession session) {
	    // Kiểm tra đăng nhập
	    Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }

	    // Lấy danh sách đơn hàng theo tài khoản 
	    List<Order> orders = orderService.getOrdersByAccount(account);

	    // Đặt danh sách đơn hàng vào thuộc tính model để sử dụng trong View
	    model.addAttribute("orders", orders);

	    // Forward đến trang vieworders.html
	    return "customer/vieworders";
	}
	
	@GetMapping("/orderconfirmation")
	public String orderConfirmation(@RequestParam("orderId") Integer orderId, Model model) {
	    // Lấy đơn hàng từ cơ sở dữ liệu dựa trên id
	    Order order = orderService.getOrderById(orderId);

	    // Đặt đơn hàng vào model để hiển thị trên trang xác nhận đơn hàng
	    model.addAttribute("order", order);
	    List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);
        model.addAttribute("orderItems", orderItems);

	    return "customer/orderconfirmation";
	}
}
