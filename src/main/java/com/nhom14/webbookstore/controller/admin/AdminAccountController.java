package com.nhom14.webbookstore.controller.admin;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.service.AccountService;
import com.nhom14.webbookstore.service.CloudinaryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminAccountController {
	private AccountService accountService;
	private CloudinaryService cloudinaryService;

	@Autowired
	public AdminAccountController(AccountService accountService, CloudinaryService cloudinaryService) {
		super();
		this.accountService = accountService;
		this.cloudinaryService = cloudinaryService;
	}
	
	@GetMapping("/loginadmin")
	public String loginAdminForm() {
		return "admin/loginadmin";
	}
	
	@PostMapping("/loginadmin")
    public String loginAdmin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
		session.invalidate();
        session = request.getSession(true);
        // Kiểm tra đăng nhập bằng phương thức checkLogin
        boolean isValid = accountService.checkLoginAdmin(username, password);

        if (isValid) {
            // Nếu đăng nhập thành công, lưu thông tin admin vào session
            Account admin = accountService.findAccountByUsername(username);
            session.setAttribute("admin", admin);
            // Nếu đăng nhập thành công, hiển thị thông báo thành công và quay lại trang chủ
            redirectAttributes.addAttribute("message", "Đăng nhập thành công!");
            return "redirect:/admin";
        } else {
            // Nếu đăng nhập thất bại, hiển thị thông báo lỗi và quay lại trang đăng nhập
        	redirectAttributes.addAttribute("message", "Sai tên đăng nhập hoặc mật khẩu");
        	return "redirect:/loginadmin";
        }
    }
	
	@GetMapping("/logoutadmin")
	public String logoutAdminForm() {
		return "admin/logoutadmin";
	}
	
	@PostMapping("/logoutadmin")
	public String logoutAdmin(HttpServletRequest request) {
		HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/loginadmin";
	}
	
	@GetMapping("/manageaccounts")
	public String manageAccounts(@RequestParam(value = "status", required = false) Integer status,
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
	    
	    // Lấy danh sách tài khoản theo trạng thái hoặc tất cả tài khoản
        List<Account> accounts;
        int totalAccounts;
        
        // Số danh mục hiển thị trên mỗi trang	
        int recordsPerPage = 10;
        int start;
        int end;
        int totalPages;
        
        if (status == null || (status == -1)) {
        	accounts = accountService.getAllAccounts();
        } else {
        	accounts = accountService.getAccountsByStatus(status);
        }
        
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
        	accounts = accountService.searchAccountsByKeyword(accounts, searchKeyword);
        }
        
        // Lấy tổng số lượng tài khoản
        totalAccounts = accounts.size();
        
        // Tính toán vị trí bắt đầu và kết thúc của tài khoản trên trang hiện tại
        start = (currentPage - 1) * recordsPerPage;
        end = Math.min(start + recordsPerPage, totalAccounts);
        
        // Lấy danh sách tài khoản trên trang hiện tại
        List<Account> accountsOnPage = accounts.subList(start, end);
        
        // Tính toán số trang
        totalPages = (int) Math.ceil((double) totalAccounts / recordsPerPage);
        
        // Tổng số tất cả các tài khoản
        int totalAllAccounts = accountService.getAllAccounts().size();
        
        model.addAttribute("accounts", accountsOnPage);
        model.addAttribute("totalAccounts", totalAccounts);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalAllAccounts", totalAllAccounts);
        
        return "admin/manageaccounts";
	}
	
	@GetMapping("/managedetailaccount")
	public String manageDetailAccount(@RequestParam("accountId") Integer accountId,
			Model model,
		    HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Truy xuất dữ liệu từ nguồn dữ liệu 
        Account account = accountService.getAccountById(accountId);
        
        // Lưu thông tin tài khoản vào model
        model.addAttribute("account", account);
        // Sinh giá trị ngẫu nhiên
        Random random = new Random();
        int randomNumber = random.nextInt();
        model.addAttribute("randomNumber", randomNumber);

        // Forward đến trang xem thông tin tài khoản
        return "admin/managedetailaccount";
	}
	
	@GetMapping("/editaccount")
	public String editAccountForm(@RequestParam("accountId") Integer accountId,
			Model model,
		    HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Truy xuất dữ liệu từ nguồn dữ liệu 
        Account account = accountService.getAccountById(accountId);
        
        // Lưu thông tin tài khoản vào model
        model.addAttribute("account", account);
        // Sinh giá trị ngẫu nhiên
        Random random = new Random();
        int randomNumber = random.nextInt();
        model.addAttribute("randomNumber", randomNumber);

        // Forward đến trang xem sửa tài khoản
        return "admin/editaccount";
	}
	
	@PostMapping("/editaccount")
	public String editAccount(@ModelAttribute("account") Account accountParam,
	                            @RequestParam("image") MultipartFile image,
	                            @RequestParam("dob") String dob,
	                            HttpSession session,
	                            Model model,
	                            RedirectAttributes redirectAttributes) {

		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

	    Account updateAccount = accountService.getAccountById(accountParam.getId());
	    try {
	        if (!image.isEmpty()) {
	        	// Tạo public ID cho hình ảnh trên Cloudinary (sử dụng id người dùng)
                String publicId = "WebBookStore/img_account/account_" + updateAccount.getId();

                // Tải lên hình ảnh lên Cloudinary và lấy URL
                String imageUrl = cloudinaryService.uploadImage(image, publicId);

                // Cập nhật URL hình ảnh vào tài khoản
                updateAccount.setImg(imageUrl);
	        }

	        // Cập nhật thông tin tài khoản
	        updateAccount.setUsername(accountParam.getUsername());
	        updateAccount.setFirstName(accountParam.getFirstName());
	        updateAccount.setLastName(accountParam.getLastName());
	        updateAccount.setGender(accountParam.getGender());
	        updateAccount.setDateOfBirth(Date.valueOf(dob));
	        updateAccount.setAddress(accountParam.getAddress());
	        updateAccount.setPhoneNumber(accountParam.getPhoneNumber());
	        updateAccount.setEmail(accountParam.getEmail());
	        updateAccount.setStatus(accountParam.getStatus());
	        accountService.updateAccount(updateAccount);
	        
	        // Nếu trùng với admin thì lưu thông tin tài khoản mới vào session
            if(admin.getId() == updateAccount.getId()) {
            	session.setAttribute("admin", updateAccount);
            	redirectAttributes.addAttribute("account", updateAccount);
            } else {
            	redirectAttributes.addAttribute("account", updateAccount);
            }

	        // Đến trang xem thông tin tài khoản
	        redirectAttributes.addAttribute("message", "Đã cập nhật thành công!");
	        return "redirect:/managedetailaccount?accountId=" + updateAccount.getId();
	    } catch (IOException e) {
	        e.printStackTrace();
	        redirectAttributes.addAttribute("message", "Đã xảy ra lỗi khi cập nhật tài khoản.");
	        return "redirect:/managedetailaccount?accountId=" + updateAccount.getId();
	    }
	}
	
	@GetMapping("/managechangepassword")
	public String manageChangePasswordForm(@RequestParam("accountId") Integer accountId,
			Model model,
		    HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Truy xuất dữ liệu từ nguồn dữ liệu 
        Account account = accountService.getAccountById(accountId);
        
        // Lưu thông tin tài khoản vào model
        model.addAttribute("account", account);

        // Forward đến trang xem sửa tài khoản
        return "admin/managechangepassword";
	}
	
	@PostMapping("/managechangepassword")
	public String manageChangePassword(@RequestParam("accountId") Integer accountId,
			@RequestParam("currentPassword") String currentPassword, 
            @RequestParam("newPassword") String newPassword, 
            @RequestParam("confirmPassword") String confirmPassword,
			Model model,
		    HttpSession session,
		    RedirectAttributes redirectAttributes) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Truy xuất dữ liệu từ nguồn dữ liệu 
        Account account = accountService.getAccountById(accountId);
        
        // Kiểm tra mật khẩu hiện tại
	    if (!BCrypt.checkpw(currentPassword, account.getPassword())) {
	        // Hiển thị thông báo mật khẩu hiện tại không đúng
	    	redirectAttributes.addAttribute("message", "Mật khẩu hiện tại không đúng. Vui lòng thử lại.");
	        return "redirect:/managechangepassword?accountId=" + account.getId();
	    }
	    
	    // Kiểm tra mật khẩu mới có giống mật khẩu hiện tại không
	    if (BCrypt.checkpw(newPassword, account.getPassword())) {
	        // Hiển thị thông báo giống mật khẩu cũ
	    	redirectAttributes.addAttribute("message", "Vui lòng chọn mật khẩu khác mật khẩu cũ!");
	    	return "redirect:/managechangepassword?accountId=" + account.getId();
	    }
	    
	    // Kiểm tra mật khẩu mới có phải là mật khẩu mạnh không
	    if(!(newPassword.length() >= 8 
            && newPassword.matches(".*[A-Z].*") 
            && newPassword.matches(".*[a-z].*") 
            && newPassword.matches(".*\\d.*") 
            && newPassword.matches(".*\\W.*"))) {
	    	// Hiển thị thông báo khi mật khẩu yếu
	    	redirectAttributes.addAttribute("message", "Mật khẩu không đủ mạnh! Mật khẩu mới phải có ít nhất 8 ký tự và"
	    			+ " chứa ít nhất một chữ cái viết hoa, một chữ cái viết thường, một số và một ký tự đặc biệt.");
	        return "redirect:/managechangepassword?accountId=" + account.getId();
	    }
	    
	    // Kiểm tra mật khẩu mới và mật khẩu nhập lại
	    if (!newPassword.equals(confirmPassword)) {
	        // Hiển thị thông báo mật khẩu nhập lại không khớp
	    	redirectAttributes.addAttribute("message", "Mật khẩu nhập lại không khớp. Vui lòng thử lại.");
	        return "redirect:/managechangepassword?accountId=" + account.getId();
	    }
	    
	    // Băm mật khẩu mới sử dụng bcrypt
	    String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
	    // Cập nhật mật khẩu mới cho tài khoản
	    account.setPassword(hashedNewPassword);
	    // Gọi phương thức updateAccount trong AccountService để cập nhật thông tin tài khoản
	    accountService.updateAccount(account);
	    // Hiển thị thông báo thành công
	    redirectAttributes.addAttribute("message", "Thay đổi mật khẩu thành công.");
	    return "redirect:/managedetailaccount?accountId=" + account.getId();
	}
}
