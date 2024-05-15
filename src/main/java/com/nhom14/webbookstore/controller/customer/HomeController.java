package com.nhom14.webbookstore.controller.customer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String showHome() {
		return "redirect:/home";
	}
	
	@GetMapping("/aboutus")
	public String showAboutUs() {
		return "customer/aboutus";
	}

	@GetMapping("/customer/error")
	public String showError() {
		return "customer/error";
	}
}
