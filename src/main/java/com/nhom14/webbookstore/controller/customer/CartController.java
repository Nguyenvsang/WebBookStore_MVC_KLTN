package com.nhom14.webbookstore.controller.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.Cart;
import com.nhom14.webbookstore.entity.CartItem;
import com.nhom14.webbookstore.service.BookService;
import com.nhom14.webbookstore.service.CartItemService;
import com.nhom14.webbookstore.service.CartService;

@Controller
public class CartController {

	private CartService cartService;
	private BookService bookService;
	private CartItemService cartItemService;

	@Autowired
	public CartController(CartService cartService, BookService bookService, CartItemService cartItemService) {
		super();
		this.cartService = cartService;
		this.bookService = bookService;
		this.cartItemService = cartItemService;
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
	            return "redirect:/detailbook/" + bookId; // Thay đổi đường dẫn tới trang chi tiết sách của bạn
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

        Cart cart;
        if (account != null) {
            cart = cartService.getCartByAccount(account); //xai account.getCart() se bi loi

            if (cart == null) {
                cart = new Cart(account);
                cartService.addCart(cart);
            }
        } else {
            // Xử lý khi không có tài khoản (chưa đăng nhập)
            // Chuyển hướng đến trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        // Lấy danh sách các mục trong giỏ hàng
        List<CartItem> cartItems = cartItemService.getCartItemsByCart(cart);// xai cart.getCartItems() se bị loi

        // Kiểm tra xem giỏ hàng có hàng không
        if (cartItems.isEmpty()) {
            model.addAttribute("message", "Giỏ hàng trống!");
            model.addAttribute("totalAmount", null);
            // Chuyển hướng đến trang viewcart
            return "customer/viewcart";
        }

        // Tính toán tổng số tiền trong giỏ hàng
        double totalAmount = calculateTotalAmount(cartItems);
        
        // Tổng số đầu sách trong giỏ hàng
        int totalAllCartItems = cartItems.size();

        // Lưu thông tin giỏ hàng và tổng số tiền vào model attribute
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalAllCartItems", totalAllCartItems);

        // Chuyển hướng đến trang viewcart
        return "customer/viewcart";
    }

    private double calculateTotalAmount(List<CartItem> cartItems) {
        double totalAmount = 0.0;
        for (CartItem cartItem : cartItems) {
            totalAmount += cartItem.getQuantity() * cartItem.getBook().getSellPrice();
        }
        return totalAmount;
    }
    
    @GetMapping("/removefromcart")
    public String removeFromCart(@RequestParam("itemId") int itemId,
    		HttpSession session) {
    	
    	Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }
	    
	    // Xóa CartItem khỏi giỏ hàng
	    CartItem cartItem = cartItemService.getCartItemById(itemId);
        cartItemService.deleteCartItem(cartItem);
        
        // Chuyển hướng về trang hiển thị giỏ hàng
        return "redirect:/viewcart";
    }
}
