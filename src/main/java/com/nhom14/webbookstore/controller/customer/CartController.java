package com.nhom14.webbookstore.controller.customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.model.lean_model.DiscountLeanModel;
import com.nhom14.webbookstore.model.response_model.BookResponseModel;
import com.nhom14.webbookstore.model.response_model.CartItemResponseModel;
import com.nhom14.webbookstore.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {

	private CartService cartService;
	private BookService bookService;
	private CartItemService cartItemService;
	private ModelMapper modelMapper;
	private DiscountService discountService;
	private VoucherService voucherService;

	@Autowired
	public CartController(CartService cartService, BookService bookService, CartItemService cartItemService, ModelMapper modelMapper, DiscountService discountService, VoucherService voucherService) {
		super();
		this.cartService = cartService;
		this.bookService = bookService;
		this.cartItemService = cartItemService;
        this.modelMapper = modelMapper;
        this.discountService = discountService;
        this.voucherService = voucherService;
    }
	
	@PostMapping("/addtocart")
	public String addToCart(@RequestParam("bookId") int bookId, 
			@RequestParam("quantity") int quantity, 
			HttpSession session,
			RedirectAttributes redirectAttributes) {
	    Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }

	    // Kiểm tra số lượng hợp lệ
	    if (quantity <= 0) {
	        // Số lượng không hợp lệ, lưu thông báo lỗi vào Model và chuyển hướng về trang chi tiết sách
	        String message = "Số lượng không hợp lệ";
	        redirectAttributes.addAttribute("message", message);
	        return "redirect:/detailbook/" + bookId;
	    }

	    // Kiểm tra xem giỏ hàng của người dùng đã tồn tại hay chưa
	    Cart cart = cartService.getCartByAccount(account);
	    if (cart == null) {
	        // Nếu giỏ hàng chưa tồn tại, thêm giỏ hàng mới
	        cart = new Cart(account);
	        cartService.addCart(cart);
	    }

	    // Tìm CartItem theo cart và book
	    Book book = bookService.getActiveBookById(bookId);
		if (book == null) { // Trường hợp lỡ nhận id sách đã ngừng kinh doanh
			String message = "Đã có lỗi xảy ra, vui lòng thử lại sau!";
			redirectAttributes.addAttribute("message", message);
			return "redirect:/detailbook/" + bookId;
		}
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
	            // Số lượng vượt quá số lượng tồn kho, lưu thông báo lỗi vào Model và chuyển hướng về trang chi tiết sách
	            String message = "Số lượng vượt quá số lượng tồn kho";
	            redirectAttributes.addAttribute("message", message);
	            return "redirect:/detailbook/" + bookId;
	        }

	        cartItem.setQuantity(newQuantity);
	        cartItemService.updateCartItem(cartItem);
	    }

	    // Chuyển hướng về trang chi tiết sản phẩm
	    // Thành công, lưu thông báo thành công vào Model
	    String message = "Thêm vào giỏ hàng thành công";
	    redirectAttributes.addAttribute("message", message);
	    return "redirect:/detailbook/" + bookId;
	}

	@GetMapping("/viewcart")
	public String viewCart(Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("account");

		if (account == null) {
			return "redirect:/customer/loginaccount";
		}

		// Xóa giá trị buyNowBook và buyNowQuantity khi người dùng chọn mua hàng từ giỏ hàng
		session.removeAttribute("buyNowBook");
		session.removeAttribute("buyNowQuantity");

		Cart cart;
		cart = cartService.getCartByAccount(account); //xai account.getCart() se bi loi

		if (cart == null) {
			cart = new Cart(account);
			cartService.addCart(cart);
		}

		List<CartItem> cartItems = cartItemService.getCartItemsByCart(cart);

		if (cartItems.isEmpty()) {
			model.addAttribute("message", "Giỏ hàng trống! Lựa chọn sách để thêm vào giỏ hàng và quay lại đây để xem");
			return "customer/viewcart";
		}

		List<CartItem> availableItems = new ArrayList<>();
		List<CartItem> unavailableItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			processCartItem(cartItem, availableItems, unavailableItems);
		}

		// Chuyển đổi từ List<CartItem> sang List<String>
		List<String> unavailableItemIds = unavailableItems.stream()
				.map(cartItem -> Integer.toString(cartItem.getId()))
				.collect(Collectors.toList());

		// Nối các id lại với nhau bằng dấu phẩy
		String unavailableItemIdsString = String.join(",", unavailableItemIds);

		double totalAmount = calculateTotalAmount(availableItems);
		int totalAllAvailableCartItems = availableItems.size();

		List<CartItemResponseModel> availableItemResponseModels = availableItems.stream()
				.map(this::convertToCartItemResponseModel)
				.toList();

		List<CartItemResponseModel> unavailableItemResponseModels = unavailableItems.stream()
				.map(this::convertToCartItemResponseModel)
				.toList();

		// Lấy tất cả các voucher còn giá trị
		List<Voucher> vouchers = voucherService.getActiveVouchers();

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("availableItems", availableItemResponseModels);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("totalAllAvailableCartItems", totalAllAvailableCartItems);
		model.addAttribute("unavailableItems", unavailableItemResponseModels);
		model.addAttribute("unavailableItemIdsString", unavailableItemIdsString);
		model.addAttribute("vouchers", vouchers);

		return "customer/viewcart";
	}

	private void processCartItem(CartItem cartItem, List<CartItem> availableItems, List<CartItem> unavailableItems) {
		Book book = cartItem.getBook();
		int quantity = cartItem.getQuantity();

		if(book.getStatus() == 0 || book.getQuantity() < quantity){
			unavailableItems.add(cartItem);
		} else {
			availableItems.add(cartItem);
		}
	}


	private double calculateTotalAmount(List<CartItem> cartItems) {
        double totalAmount = 0.0;
        for (CartItem cartItem : cartItems) {
            totalAmount += cartItem.getQuantity() * cartItem.getBook().getSellPrice();
        }
        return totalAmount;
    }

	@GetMapping("/removefromcart")
	public ResponseEntity<String> removeFromCart(@RequestParam("itemId") int itemId, HttpSession session) {
		// Kiểm tra đăng nhập và sở hữu của cart item
		Account account = (Account) session.getAttribute("account");
		if (account == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập để thực hiện hành động này.");
		}

		if (!cartItemService.isOwnerOfCartItem(account.getId(), itemId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện hành động này.");
		}

		// Xóa cart item và trả về phản hồi thành công
		CartItem cartItem = cartItemService.getCartItemById(itemId);
		cartItemService.deleteCartItem(cartItem);
		return ResponseEntity.ok("CartItem removed successfully");
	}

	@PostMapping("/removeallfromcart")
	public String removeAllFromCart(@RequestParam("itemIds") String itemIds,
									RedirectAttributes redirectAttributes,
									HttpSession session) {
		Account account = (Account) session.getAttribute("account");

		// Kiểm tra xem người dùng đã đăng nhập hay chưa
		if (account == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/customer/loginaccount";
		}

		// Kiểm tra xem có phải muốn xóa tất cả (bao gồm sản phẩm có hàng và hết hàng không)
		if (Objects.equals(itemIds, "all")) {
			Cart cart = cartService.getCartByAccount(account);
			List<CartItem> cartItems = cartItemService.getCartItemsByCart(cart);
			for (CartItem cartItem : cartItems) {
				// Xóa CartItem khỏi giỏ hàng
				cartItemService.deleteCartItem(cartItem);
			}
		} else {
			// Chuyển đổi từ chuỗi các id thành danh sách các id
			List<Integer> itemIdList = Arrays.stream(itemIds.split(","))
					.map(Integer::parseInt)
					.toList();

			// Duyệt qua danh sách các id
			for (Integer itemId : itemIdList) {
				CartItem cartItem = cartItemService.getCartItemById(itemId);

				// Kiểm tra xem cartitem có thuộc về giỏ hàng của người đang đăng nhập không
				if (cartItem.getCart().getAccount().getId() != account.getId()) {
					// Nếu không thuộc về thì trả về trang lỗi
					redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
					return "redirect:/customer/error";
				}

				// Xóa CartItem khỏi giỏ hàng
				cartItemService.deleteCartItem(cartItem);
			}
		}

		// Chuyển hướng về trang hiển thị giỏ hàng
		return "redirect:/viewcart";
	}


	@PostMapping("/addtocartfromallbooks")
	public ResponseEntity<?> addToCartFromAllBooks(@RequestParam("bookId") int bookId,
												   @RequestParam("quantity") int quantity,
												   HttpSession session) {
		Account account = (Account) session.getAttribute("account");

		// Kiểm tra xem người dùng đã đăng nhập hay chưa
		if (account == null) {
			// Nếu chưa đăng nhập, trả về thông báo lỗi
			String message = "Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		// Kiểm tra số lượng hợp lệ
		if (quantity <= 0) {
			// Số lượng không hợp lệ, trả về thông báo lỗi
			String message = "Số lượng không hợp lệ";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}

		// Kiểm tra xem giỏ hàng của người dùng đã tồn tại hay chưa
		Cart cart = cartService.getCartByAccount(account);
		if (cart == null) {
			// Nếu giỏ hàng chưa tồn tại, thêm giỏ hàng mới
			cart = new Cart(account);
			cartService.addCart(cart);
		}

		// Tìm CartItem theo cart và book
		Book book = bookService.getActiveBookById(bookId);
		if (book == null) { // Trường hợp lỡ nhận id sách đã ngừng kinh doanh
			String message = "Đã có lỗi xảy ra, vui lòng thử lại sau!";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
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
				// Số lượng vượt quá số lượng tồn kho, trả về thông báo lỗi
				String message = "Số lượng vượt quá số lượng tồn kho";
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
			}

			cartItem.setQuantity(newQuantity);
			cartItemService.updateCartItem(cartItem);
		}

		// Tạo và trả về ResponseEntity
		String message = "Thêm vào giỏ hàng thành công";
		return ResponseEntity.ok(message);
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
