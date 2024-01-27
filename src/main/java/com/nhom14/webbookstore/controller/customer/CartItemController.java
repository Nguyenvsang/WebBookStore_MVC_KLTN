package com.nhom14.webbookstore.controller.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.CartItem;
import com.nhom14.webbookstore.service.CartItemService;

@Controller
public class CartItemController {

	private CartItemService cartItemService;

	@Autowired
	public CartItemController(CartItemService cartItemService) {
		super();
		this.cartItemService = cartItemService;
	}
	
	@PostMapping("/updatecartitem")
	public ResponseEntity<String> updateCartItem(@RequestParam("itemId") int itemId, 
	        @RequestParam("quantity") int quantity,
	        HttpSession session) {
	    
	    Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Location", "/customer/loginaccount");
	        return ResponseEntity.status(HttpStatus.FOUND)
	                .headers(headers)
	                .body("User not logged in");
	    }
	    
	    CartItem cartItem = cartItemService.getCartItemById(itemId);
	    if (cartItem == null) {
	        // Không tìm thấy mặt hàng trong giỏ hàng
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CartItem not found");
	    }
	    
	    cartItem.setQuantity(quantity);
	    cartItemService.updateCartItem(cartItem);
	    
	    return ResponseEntity.ok("CartItem updated successfully");
	}
}
