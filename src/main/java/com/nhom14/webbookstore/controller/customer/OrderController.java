package com.nhom14.webbookstore.controller.customer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.nhom14.webbookstore.DTO.PaymentResDTO;
import com.nhom14.webbookstore.config.VNPAYPaymentConfig;
import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.model.lean_model.DiscountLeanModel;
import com.nhom14.webbookstore.model.response_model.CartItemResponseModel;
import com.nhom14.webbookstore.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private PaymentStatusService paymentStatusService;
    private RevenueService revenueService;
    private ProfitService profitService;
    private BookImportService bookImportService;
    private  AccountAddressService accountAddressService;
    private DiscountService discountService;
    private ModelMapper modelMapper;
    private VoucherService voucherService;
    private VoucherInfoService voucherInfoService;
    private NotificationService notificationService;
    private AccountService accountService;

	@Autowired
	public OrderController(OrderService orderService,
                           OrderItemService orderItemService,
                           CartService cartService,
                           CartItemService cartItemService,
                           BookService bookService,
                           PaymentStatusService paymentStatusService, RevenueService revenueService, ProfitService profitService, BookImportService bookImportService, AccountAddressService accountAddressService, DiscountService discountService, ModelMapper modelMapper, VoucherService voucherService, VoucherInfoService voucherInfoService, NotificationService notificationService, AccountService accountService) {
		super();
		this.orderService = orderService;
		this.orderItemService = orderItemService;
		this.cartService = cartService;
		this.cartItemService = cartItemService;
		this.bookService = bookService;
		this.paymentStatusService = paymentStatusService;
        this.revenueService = revenueService;
        this.profitService = profitService;
        this.bookImportService = bookImportService;
        this.accountAddressService = accountAddressService;
        this.discountService = discountService;
        this.modelMapper = modelMapper;
        this.voucherService = voucherService;
        this.voucherInfoService = voucherInfoService;
        this.notificationService = notificationService;
        this.accountService = accountService;
    }

    @GetMapping("/shippinginformation")
    public String showShippingInformationForm(Model model,
                                              HttpSession session,
                                              RedirectAttributes redirectAttributes) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        String idSelectedCartItems = (String) session.getAttribute("idSelectedCartItems");

        // Kiểm tra xem giỏ hàng có hàng không
        Cart cart = cartService.getCartByAccount(account);
        List<CartItem> cartItems = cartItemService.getCartItemsByCart(cart);
        if (cartItems.isEmpty() || idSelectedCartItems == null || idSelectedCartItems.isEmpty()) {
            redirectAttributes.addAttribute("message", "Vui lòng chọn ít nhất 1 món hàng!");
            return "redirect:/viewcart";
        }

        // Chuyển chuỗi selectedItems thành danh sách
        List<Integer> idSelectedCartItemsList = Arrays.stream(idSelectedCartItems.split(","))
                .map(Integer::parseInt)
                .toList();

        // Lọc danh sách cartItems để chỉ giữ lại những mục đã được chọn
        List<CartItem> selectedCartItems = cartItems.stream()
                .filter(cartItem -> idSelectedCartItemsList.contains(cartItem.getId()))
                .toList();

        // Tính toán tổng số tiền cho các mục đã chọn
        double totalAmount = 0;
        for (CartItem cartItem : selectedCartItems) {
            // Kiểm tra xem sản phẩm có giảm giá không
            Discount discount = discountService.getLatestActiveDiscountByBookId(cartItem.getBook().getId());
            if (discount != null) {
                // Nếu có, giá dựa trên giá đã giảm
                totalAmount += cartItem.getQuantity() * discount.getDiscountedPrice();
            } else {
                // Nếu không giá dựa trên giá gốc
                totalAmount += cartItem.getQuantity() * cartItem.getBook().getSellPrice();
            }
        }

        // Tính giá trị nếu có Voucher (giá được giảm, giá đã giảm)
        Voucher selectedVoucher = null;
        double discountedPriceByVoucher = -1;
        Long selectedVoucherId = (Long) session.getAttribute("voucherId");
        if (selectedVoucherId != null && selectedVoucherId != -1) {
            discountedPriceByVoucher = calculateDiscountedPriceByVoucher(selectedVoucherId, totalAmount, selectedCartItems, redirectAttributes);
            if (discountedPriceByVoucher == -1) {
                return "redirect:/viewcart";
            }
            // Nếu có voucher có thể áp dụng thì
            selectedVoucher = voucherService.getVoucherById(selectedVoucherId);
        }

        if (selectedVoucher != null) {
            model.addAttribute("selectedVoucher", selectedVoucher);
            model.addAttribute("selectedVoucherId", selectedVoucherId);
        }

        if (discountedPriceByVoucher != -1) {
            model.addAttribute("discountedPriceByVoucher", discountedPriceByVoucher);
            double voucherDiscount = totalAmount - discountedPriceByVoucher;
            model.addAttribute("voucherDiscount", voucherDiscount);
        }

        // Lấy tất cả các địa chỉ để hiển thị trong pop up lựa chọn địa chỉ
        List<AccountAddress> addresses = accountAddressService.getAddressesByAccount(account);

        List<CartItemResponseModel> selectedCartItemResponseModels = selectedCartItems.stream()
                .map(this::convertToCartItemResponseModel)
                .toList();

        // Lấy tất cả các voucher còn giá trị
        List<Voucher> vouchers = voucherService.getActiveVouchers();

        model.addAttribute("account", account);
        model.addAttribute("idSelectedCartItems", idSelectedCartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("selectedCartItems", selectedCartItemResponseModels);
        model.addAttribute("addresses", addresses);
        model.addAttribute("vouchers", vouchers);

        return "customer/shippinginformation";
    }



    @PostMapping("/shippinginformation")
    public String shippingInformation(@RequestParam String idSelectedCartItems,
                                      @RequestParam(value = "voucherId", required = false) Long voucherId,
                                      HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        // Kiểm tra xem voucherId có bằng -1 hay không
        if (voucherId != null && voucherId == -1) {
            // Nếu có, xóa voucherId khỏi session
            session.removeAttribute("voucherId");
        } else {
            // Nếu không, cập nhật voucherId trong session
            session.setAttribute("voucherId", voucherId);
        }

        session.setAttribute("idSelectedCartItems", idSelectedCartItems);
        return "redirect:/shippinginformation";
    }

    @PostMapping("/placeorder")
    public String placeOrder(HttpSession session, 
    		@RequestParam("name") String name, 
    		@RequestParam("address") String address, 
    		@RequestParam("phoneNumber") String phoneNumber, 
    		@RequestParam("email") String email,
            @RequestParam("paymentMethods") String paymentMethods,
            @RequestParam String idSelectedCartItems,
            @RequestParam(value = "voucherId", required = false) Long voucherId,
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
            redirectAttributes.addAttribute("message", "Vui lòng chọn ít nhất 1 món hàng!");
            return "redirect:/viewcart";
        }

        // Chuyển chuỗi selectedItems thành danh sách
        List<Integer> idSelectedCartItemsList = Arrays.stream(idSelectedCartItems.split(","))
                .map(Integer::parseInt)
                .toList();

        // Lọc danh sách cartItems để chỉ giữ lại những mục đã được chọn
        List<CartItem> selectedCartItems = cartItems.stream()
                .filter(cartItem -> idSelectedCartItemsList.contains(cartItem.getId()))
                .toList();

        // Tính toán tổng số tiền cho các mục đã chọn
        double totalAmount = 0;
        for (CartItem cartItem : selectedCartItems) {
            // Kiểm tra xem sản phẩm có giảm giá không
            Discount discount = discountService.getLatestActiveDiscountByBookId(cartItem.getBook().getId());
            if (discount != null) {
                // Nếu có, giá dựa trên giá đã giảm
                totalAmount += cartItem.getQuantity() * discount.getDiscountedPrice();
            } else {
                // Nếu không giá dựa trên giá gốc
                totalAmount += cartItem.getQuantity() * cartItem.getBook().getSellPrice();
            }
        }

        // Phần tính giá giảm theo Voucher (nếu có)
        double discountedPriceByVoucher = -1;
        // Kiểm tra xem voucherId có tồn tại hay không
        // Gọi hàm calculateDiscountedPriceByVoucher
        if (voucherId != null && voucherId != -1) {
            discountedPriceByVoucher = calculateDiscountedPriceByVoucher(voucherId, totalAmount, selectedCartItems, redirectAttributes);
            if (discountedPriceByVoucher == -1) {
                return "redirect:/viewcart";
            }
        }


        // Nếu voucher áp dụng không có trục trặc gì
        // Tạo đối tượng đơn hàng
        Order order = new Order();
        Timestamp dateOrder = new Timestamp(System.currentTimeMillis());
        order.setDateOrder(dateOrder);
        order.setTotalPrice(totalAmount);
        // Nếu giá sau khi giảm khác -1
        if (discountedPriceByVoucher != -1) {
            order.setTotalPrice(discountedPriceByVoucher);
        }

        order.setName(name);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setAccount(cart.getAccount());
        order.setStatus(0);
        // deliveryDate khi nào giao hàng mới đặt
        // Tính toán ngày giao hàng dự kiến 1 và 2
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateOrder.getTime());
        c.add(Calendar.DATE, 3);  // Ngày giao hàng dự kiến 1: 3 ngày sau ngày đặt hàng
        order.setExpectedDeliveryDate1(new Timestamp(c.getTimeInMillis()));
        c.add(Calendar.DATE, 2);  // Ngày giao hàng dự kiến 2: thêm 2 ngày nữa (tổng cộng 5 ngày sau ngày đặt hàng)
        order.setExpectedDeliveryDate2(new Timestamp(c.getTimeInMillis()));

        // Thêm đơn hàng vào cơ sở dữ liệu
        orderService.addOrder(order);

        // Lấy ID của đơn hàng vừa thêm
        Order lastOrder = orderService.getLastOrder(cart.getAccount());

        // Tạo danh sách các mục đơn hàng (OrderItem) từ giỏ hàng
        for (CartItem cartItem : selectedCartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            Book book = cartItem.getBook();

            double totalPrice = cartItem.getQuantity() * book.getSellPrice();
            orderItem.setSellPrice(book.getSellPrice());// Lưu để dễ tính toán cho doanh thu, lợi nhuận
            // Kiểm tra xem sản phẩm có giảm giá không
            Discount discount = discountService.getLatestActiveDiscountByBookId(cartItem.getBook().getId());
            if (discount != null) {
                // Nếu có, giá dựa trên giá đã giảm
                totalPrice = cartItem.getQuantity() * discount.getDiscountedPrice();
                orderItem.setSellPrice(discount.getDiscountedPrice());// Lưu để dễ tính toán cho doanh thu, lợi nhuận
            }

            orderItem.setTotalPrice(totalPrice);
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

        // Tạo đối tượng trạng thái thanh toán
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setOrder(lastOrder);
        paymentStatus.setStatus(0); // Chưa thanh toán
        paymentStatus.setPaymentMethod(0); // Thanh toán khi nhận hàng

        // Thêm trạng thái thanh toán vào cơ sở dữ liệu
        paymentStatusService.addPaymentStatus(paymentStatus);

        // Nếu giá sau khi giảm khác -1
        if (discountedPriceByVoucher != -1) {
            // Tạo đối tượng VoucherInfo
            VoucherInfo voucherInfo = new VoucherInfo();
            voucherInfo.setTotalAmount(totalAmount);

            Voucher selectedVoucher = voucherService.getVoucherById(voucherId);

            voucherInfo.setVoucherDiscount(totalAmount - discountedPriceByVoucher);
            voucherInfo.setDiscountedAmount(discountedPriceByVoucher);
            voucherInfo.setOrder(lastOrder);
            voucherInfo.setVoucher(selectedVoucher);

            // Lưu voucherInfo vào cơ sở dữ liệu
            voucherInfoService.save(voucherInfo);

            // Giảm số lượng còn lại của Voucher xuống, repository chỉ lấy các voucher >0
            int remainingQuantity = selectedVoucher.getRemainingQuantity() - 1;
            if (remainingQuantity <= 0) {
                selectedVoucher.setRemainingQuantity(0);
                selectedVoucher.setStatus(0); // Ngưng áp dụng do đã hết các mã
            } else {
                selectedVoucher.setRemainingQuantity(remainingQuantity);
            }
            voucherService.saveVoucher(selectedVoucher);
        }



        // Chuyển hướng đến trang xác nhận đơn hàng hoặc trang thanh toán Momo
        if ("VNPAY".equals(paymentMethods)){
            // Nếu người dùng chọn "Cổng thanh toán VNPAY", đổi paymentMethod và chuyển hướng đến trang đến đó
            paymentStatus.setPaymentMethod(1);
            paymentStatusService.addPaymentStatus(paymentStatus);
            redirectAttributes.addAttribute("orderId", lastOrder.getId());
            return "redirect:/createvnpaypayment";
        } else {
            // Nếu không, tạo thông báo, chuyển hướng đến trang xác nhận đơn hàng
            Notification notification = new Notification();
            String content = "Đơn hàng mã " + lastOrder.getId() + " vừa mới được đặt bởi " + account.getUsername();
            notification.setContent(content);
            notification.setStatus(0);// Chưa đọc
            notification.setType(0); // :Loại thông báo về order
            notification.setReferredId(lastOrder.getId());
            // Lấy một admin còn hoạt động
            Account admin = accountService.getOneActiveAdmin();
            notification.setReceiver(admin);
            notification.setTriggerUser(account);
            notification.setSentTime(new Timestamp(System.currentTimeMillis()));
            notification = notificationService.save(notification);
            redirectAttributes.addAttribute("orderId", lastOrder.getId());
            return "redirect:/orderconfirmation";
        }
    }

    private double calculateDiscountedPriceByVoucher(Long voucherId,double totalAmount, List<CartItem> selectedCartItems, RedirectAttributes redirectAttributes) {
        double discountedPriceByVoucher = totalAmount;
        double voucherDiscount = 0; // Giá được giảm
        Voucher selectedVoucher = null;
        if (voucherId != null && voucherId != -1) {
            selectedVoucher = voucherService.getVoucherById(voucherId);
            if (selectedVoucher == null) {
                String message = "Voucher đã chọn hiện không còn, vui lòng chọn voucher khác!";
                redirectAttributes.addAttribute("message", message);
                return -1;
            }
            // Kiểm tra xem voucher (mọi sách) có thể áp dụng cho đơn hàng hay không
            if (selectedVoucher.getCategory() == null && selectedVoucher.getMinimumOrderValue() <= totalAmount) {
                if (selectedVoucher.getDiscountPercent() != -1) {
                    if (selectedVoucher.getMaxDiscountAmount() != -1) {
                        voucherDiscount = selectedVoucher.getMaxDiscountAmount();
                    }
                    if (voucherDiscount >= (totalAmount * selectedVoucher.getDiscountPercent() / 100)) {
                        voucherDiscount = totalAmount * selectedVoucher.getDiscountPercent() / 100;
                    }
                    discountedPriceByVoucher -= voucherDiscount;
                } else if (selectedVoucher.getAmountDiscount() != -1) {
                    voucherDiscount = selectedVoucher.getAmountDiscount();
                    discountedPriceByVoucher -= voucherDiscount;
                }
            } else if (selectedVoucher.getCategory() != null) {
                double totalCategoryValue = 0;
                for (CartItem cartItem : selectedCartItems) {
                    if (cartItem.getBook().getCategory() == selectedVoucher.getCategory()) {
                        // Kiểm tra xem sản phẩm có giảm giá không
                        Discount discount = discountService.getLatestActiveDiscountByBookId(cartItem.getBook().getId());
                        if (discount != null) {
                            // Nếu có, giá dựa trên giá đã giảm
                            totalCategoryValue += cartItem.getQuantity() * discount.getDiscountedPrice();
                        } else {
                            // Nếu không giá dựa trên giá gốc
                            totalCategoryValue += cartItem.getQuantity() * cartItem.getBook().getSellPrice();
                        }
                    }
                }

                // Kiểm tra xem tổng giá trị các mặt hàng thuộc về danh mục có lớn hơn hoặc bằng minimumOrderValue không
                if (totalCategoryValue >= selectedVoucher.getMinimumOrderValue()) {
                    // Tính toán giá đã giảm dựa trên tổng giá trị các mặt hàng thuộc về danh mục
                    if (selectedVoucher.getDiscountPercent() != -1) {
                        // Nếu là % thì giá cuối cùng = tổng giá giỏ hàng - totalCategoryValue * discountPercent / 100
                        if (selectedVoucher.getMaxDiscountAmount() != -1) {
                            voucherDiscount = selectedVoucher.getMaxDiscountAmount();
                        }
                        if (voucherDiscount >= (totalCategoryValue * selectedVoucher.getDiscountPercent() / 100)) {
                            voucherDiscount = totalCategoryValue * selectedVoucher.getDiscountPercent() / 100;
                        }
                        discountedPriceByVoucher -= voucherDiscount;
                    } else if (selectedVoucher.getAmountDiscount() != -1) {
                        // Nếu nó là số tiền mặt thì lấy luôn giá cuối cùng = tổng giá giỏ hàng - amount
                        voucherDiscount = selectedVoucher.getAmountDiscount();
                        discountedPriceByVoucher -= voucherDiscount;
                    }
                } else {
                    String message = "Tổng giá trị các mặt hàng thuộc danh mục " + selectedVoucher.getCategory().getName() + " trong đơn hàng của bạn nhỏ hơn giá trị tối thiểu của voucher. Vui lòng chọn một voucher khác hoặc tăng giá trị đơn hàng của bạn.";
                    redirectAttributes.addAttribute("message", message);
                    return -1;
                }

            } else {
                String message = "Giá trị đơn hàng của bạn nhỏ hơn giá trị tối thiểu của voucher. Vui lòng chọn một voucher khác hoặc tăng giá trị đơn hàng của bạn.";
                redirectAttributes.addAttribute("message", message);
                return -1;
            }
        }

        return discountedPriceByVoucher; // Tính toán xong
    }


    private double calculateTotalAmount(List<CartItem> cartItems) {
        double totalAmount = 0.0;
        for (CartItem cartItem : cartItems) {
            // Kiểm tra xem sản phẩm có giảm giá không
            Discount discount = discountService.getLatestActiveDiscountByBookId(cartItem.getBook().getId());
            if (discount != null) {
                // Nếu có, giá dựa trên giá đã giảm
                totalAmount += cartItem.getQuantity() * discount.getDiscountedPrice();
            } else {
                // Nếu không giá dựa trên giá gốc
                totalAmount += cartItem.getQuantity() * cartItem.getBook().getSellPrice();
            }
        }
        return totalAmount;
    }
	
	@GetMapping("/vieworders")
	public String viewOrders(
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "dateOrderStr", required = false) String dateOrderStr,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "sortOption", required = false, defaultValue = "dateOrder_desc") String sortOption,
            // sortOption: dateOrder_asc (tăng dần), dateOrder_desc
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
	    // Kiểm tra đăng nhập
	    Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }

        Sort sort = Sort.unsorted();
        if (sortOption != null) {
            if ("dateOrder_asc".equals(sortOption)) {
                sort = sort.and(Sort.by("dateOrder").ascending());
            } else if ("dateOrder_desc".equals(sortOption)) {
                sort = sort.and(Sort.by("dateOrder").descending());
            }
        }

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        // Chuyển đổi chuỗi ngày thành đối tượng LocalDate
        LocalDate dateOrder = parseDate(dateOrderStr, redirectAttributes);

        // Gọi phương thức getFilteredOrders với các tham số tìm kiếm và lọc
        Page<Order> orders = orderService.getFilteredOrders(account.getId(), searchKeyword, dateOrder, status, pageable);

	    // Đặt danh sách đơn hàng vào thuộc tính model để sử dụng trong View
	    model.addAttribute("orders", orders);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
        params.put("accountId", account.getId());
        params.put("searchKeyword", searchKeyword);
        params.put("dateOrderStr", dateOrderStr);
        params.put("status", status);
        params.put("sortOption", sortOption);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }

	    // Forward đến trang vieworders.html
	    return "customer/vieworders";
	}

    private LocalDate parseDate(String dateStr, RedirectAttributes redirectAttributes) {
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                // Xử lý trường hợp khi dateStr không phải là một ngày hợp lệ
                // Ví dụ: hiển thị thông báo lỗi cho người dùng
                redirectAttributes.addAttribute("message", "Ngày không hợp lệ!");
                // Chuyển hướng về trang managerevenues
                return null;
            }
        }
        return null;
    }


    @GetMapping("/orderconfirmation")
	public String orderConfirmation(@RequestParam("orderId") Integer orderId, Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

	    // Lấy đơn hàng từ cơ sở dữ liệu dựa trên id
	    Order order = orderService.getOrderById(orderId);

	    // Đặt đơn hàng vào model để hiển thị trên trang xác nhận đơn hàng
	    model.addAttribute("order", order);
	    List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);
        model.addAttribute("orderItems", orderItems);

	    return "customer/orderconfirmation";
	}

    @GetMapping("/createvnpaypayment")
    public String createVNPAYPayment(HttpServletRequest req, HttpServletResponse resp,
                                    @RequestParam("orderId") Integer orderId,
                                    HttpSession session
    ) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        // Kiểm tra đăng nhập
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

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
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + orderId +" vao luc "+ vnp_CreateDate);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", VNPAYPaymentConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);


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
    public String handleVNPAYReturn(HttpServletRequest req, Model model, HttpSession session) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Kiểm tra đăng nhập
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

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

        // Lấy trạng thái thanh toán dựa trên order
        PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);

        // Kiểm tra mã phản hồi
        String message;
        if ("00".equals(vnp_ResponseCode)) {

            // Cập nhật trạng thái thanh toán mới
            paymentStatus.setStatus(1); // Đã thanh toán

            // Tính toán ngày giao hàng dự kiến 1 và 2 (chủ yếu dành cho trường hợp thanh toán lại)
            Timestamp dateOrder = new Timestamp(System.currentTimeMillis());// Hoặc ngày thanh toán lại
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(dateOrder.getTime());
            c.add(Calendar.DATE, 3);  // Ngày giao hàng dự kiến 1: 3 ngày sau ngày đặt hàng
            order.setExpectedDeliveryDate1(new Timestamp(c.getTimeInMillis()));
            c.add(Calendar.DATE, 2);  // Ngày giao hàng dự kiến 2: thêm 2 ngày nữa (tổng cộng 5 ngày sau ngày đặt hàng)
            order.setExpectedDeliveryDate2(new Timestamp(c.getTimeInMillis()));
            orderService.updateOrder(order);

            // Tạo thông báo
            message = "Giao dịch thành công! Số tiền: " + Double.parseDouble(vnp_Amount) / 100;
        } else {
            // Cập nhật trạng thái thanh toán mới
            paymentStatus.setStatus(5); // Chờ thanh toán lại
            // Tạo thông báo
            message = "Giao dịch thất bại!";
        }

        // Gộp thông tin từ URL trả về vào thông báo
        message += ".\nMã giao dịch thanh toán: " + vnp_TxnRef
                + ".\nMô tả giao dịch: " + vnp_OrderInfo
                + ".\nMã giao dịch tại CTT VNPAY-QR: " + vnp_TransactionNo
                + ".\nMã ngân hàng thanh toán: " + vnp_BankCode
                + ".\nThời gian thanh toán: " + vnp_PayDate;

        paymentStatus.setInfo(message);
        // Cập nhật trạng thái thanh toán vào cơ sở dữ liệu
        paymentStatusService.updatePaymentStatus(paymentStatus);

        // Thêm thông báo giao dịch vào model
        model.addAttribute("message", message);

        // Đặt đơn hàng vào model để hiển thị trên trang xác nhận đơn hàng
        model.addAttribute("order", order);
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);
        model.addAttribute("orderItems", orderItems);

        return "customer/orderconfirmation";
    }

    @PostMapping("/receivedorder")
    public String receivedOrder(@RequestParam("orderId") Integer orderId,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                Model model) {
        // Kiểm tra đăng nhập
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        Order order = orderService.getOrderById(orderId);

        // Kiểm xem order có thuộc về người dùng đang đăng nhập không
        if(order.getAccount().getId()!=account.getId()){
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/customer/error";
        }

        // Đặt trạng thái là Đã nhận hàng
        order.setStatus(10);

        // Lưu vào CSDL
        orderService.updateOrder(order);

        // Thông báo cho admin
        Notification notification = new Notification();
        String content = "Đơn hàng mã " + orderId + " được xác nhận là đã nhận hàng bởi " + account.getUsername();
        notification.setContent(content);
        notification.setStatus(0);// Chưa đọc
        notification.setType(0); // :Loại thông báo về order
        notification.setReferredId(orderId);
        // Lấy một admin còn hoạt động
        Account admin = accountService.getOneActiveAdmin();
        notification.setReceiver(admin);
        notification.setTriggerUser(account);
        notification.setSentTime(new Timestamp(System.currentTimeMillis()));
        notification = notificationService.save(notification);

        // Chuyển đến trang xem chi tiết đơn hàng

        redirectAttributes.addAttribute("orderId", orderId);
        return "redirect:/vieworderitems";
    }

    private CartItemResponseModel convertToCartItemResponseModel(CartItem cartItem) {
        CartItemResponseModel cartItemResponseModel = modelMapper.map(cartItem, CartItemResponseModel.class);

        cartItemResponseModel.getBook().setCurrentDiscount(null);
        // Lấy đợt giảm giá còn hiệu lực theo sách
        Discount latestActiveDiscount = discountService.getLatestActiveDiscountByBookId(cartItemResponseModel.getBook().getId());

        if (latestActiveDiscount != null)
        {
            // Gán cho Response
            DiscountLeanModel discountLeanModel = modelMapper.map(latestActiveDiscount, DiscountLeanModel.class);
            cartItemResponseModel.getBook().setCurrentDiscount(discountLeanModel);
        }

        return cartItemResponseModel;
    }

}
