package com.nhom14.webbookstore.controller.admin;

import com.nhom14.webbookstore.entity.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {

	@GetMapping("/admin")
	public String showAdminHome(HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

		// Kiểm tra xem admin đã đăng nhập hay chưa
		if (admin == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/loginadmin";
		}

		return "admin/indexadmin";
	}
	
	@GetMapping("/aboutusadmin")
	public String showAboutUs() {
		return "admin/aboutusadmin";
	}

	@GetMapping("/erroradmin")
	public String showError() {
		return "admin/erroradmin";
	}
}
