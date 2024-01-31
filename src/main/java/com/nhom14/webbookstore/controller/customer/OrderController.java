package com.nhom14.webbookstore.controller.customer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.nhom14.webbookstore.DTO.PaymentResDTO;
import com.nhom14.webbookstore.config.VNPAYPaymentConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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
            @RequestParam("paymentMethods") String paymentMethods,
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

        // Chuyển hướng đến trang xác nhận đơn hàng hoặc trang thanh toán Momo
        if ("MoMo".equals(paymentMethods)) {
            // Nếu người dùng chọn "Thanh toán bằng Momo", chuyển hướng đến trang thanh toán Momo
            redirectAttributes.addAttribute("orderId", lastOrder.getId());
            return "redirect:/byMoMoPayment";
        } else if ("VNPAY".equals(paymentMethods)){
            // Nếu người dùng chọn "Cổng thanh toán VNPAY", chuyển hướng đến trang đến đó
            redirectAttributes.addAttribute("orderId", lastOrder.getId());
            return "redirect:/createvnpaypayment";
        } else {
            // Nếu không, chuyển hướng đến trang xác nhận đơn hàng
            redirectAttributes.addAttribute("orderId", lastOrder.getId());
            return "redirect:/orderconfirmation";
        }
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

    @GetMapping("/byMoMoPayment")
    public String byMoMoPayment(@RequestParam("orderId") Integer orderId, Model model) {
        // Lấy đơn hàng từ cơ sở dữ liệu dựa trên id
        Order order = orderService.getOrderById(orderId);

        // Thực hiện các bước để tích hợp thanh toán Momo tại đây

        // Đặt đơn hàng vào model để hiển thị trên trang thanh toán Momo
        model.addAttribute("order", order);

        return "customer/bymomopayment";
    }

    @GetMapping("/createvnpaypayment")
    public String createVNPAYPayment(HttpServletRequest req, HttpServletResponse resp,
                                    @RequestParam("orderId") Integer orderId
    ) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        Order order = orderService.getOrderById(orderId);
        long amount = Math.round(order.getTotalPrice()*100.0);
        String bankCode = req.getParameter("bankCode"); // Bỏ trống sẽ hiện danh sách cho người dùng chọn


        // Khóa bí mật
        String secretKey = "SSSSSangSecretKey12345NV"; // Đảm bảo rằng khóa bí mật là 16, 24, 32 ký tự

        // Mã hóa orderId để người dùng không lấy được orderId khi giao dịch VNPAY
        String orderIdStr = String.valueOf(orderId);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getBytes(), "AES"));
        String encodedOrderId = Base64.getEncoder().encodeToString(cipher.doFinal(orderIdStr.getBytes(StandardCharsets.UTF_8)));

        String vnp_TxnRef = encodedOrderId;
        String vnp_IpAddr = VNPAYPaymentConfig.getIpAddress(req);

        String vnp_TmnCode = VNPAYPaymentConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + orderId);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", VNPAYPaymentConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPAYPaymentConfig.hmacSHA512(VNPAYPaymentConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPAYPaymentConfig.vnp_PayUrl + "?" + queryUrl;

        PaymentResDTO paymentResDTO = new PaymentResDTO();
        paymentResDTO.setStatus("Ok");
        paymentResDTO.setMessage("Successfully");
        paymentResDTO.setURL(paymentUrl);

        return "redirect:" + paymentUrl;
    }

    @GetMapping("/vnpay_return")
    public String handleVNPAYReturn(HttpServletRequest req, Model model) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Lấy thông tin từ URL trả về
        String vnp_ResponseCode = req.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = req.getParameter("vnp_TxnRef");
        String vnp_Amount = req.getParameter("vnp_Amount");
        String vnp_OrderInfo = req.getParameter("vnp_OrderInfo");
        String vnp_TransactionNo = req.getParameter("vnp_TransactionNo");
        String vnp_BankCode = req.getParameter("vnp_BankCode");
        String vnp_PayDate = req.getParameter("vnp_PayDate");

        // Khóa bí mật
        String secretKey = "SSSSSangSecretKey12345NV"; // Đảm bảo rằng khóa bí mật là 16, 24, 32 ký tự
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        // Giải mã
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getBytes(), "AES"));
        String decodedOrderId = new String(cipher.doFinal(Base64.getDecoder().decode(vnp_TxnRef)));
        Order order = orderService.getOrderById(Integer.parseInt(decodedOrderId));

        // Kiểm tra mã phản hồi
        String message;
        if ("00".equals(vnp_ResponseCode)) {

            // Cập nhật trạng thái đơn hàng
            order.setStatus(3); // Đã thanh toán
            orderService.updateOrder(order);

            // Tạo thông báo
            message = "Giao dịch thành công! Số tiền: " + Double.parseDouble(vnp_Amount) / 100;
        } else {
            // Tạo thông báo
            message = "Giao dịch thất bại!";
        }

        // Gộp thông tin từ URL trả về vào thông báo
        message += ".\nMã giao dịch thanh toán: " + vnp_TxnRef
                + ".\nMô tả giao dịch: " + vnp_OrderInfo
                + ".\nMã giao dịch tại CTT VNPAY-QR: " + vnp_TransactionNo
                + ".\nMã ngân hàng thanh toán: " + vnp_BankCode
                + ".\nThời gian thanh toán: " + vnp_PayDate;

        // Thêm thông báo giao dịch vào model
        model.addAttribute("message", message);

        // Đặt đơn hàng vào model để hiển thị trên trang xác nhận đơn hàng
        model.addAttribute("order", order);
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);
        model.addAttribute("orderItems", orderItems);

        return "customer/orderconfirmation";
    }

}
