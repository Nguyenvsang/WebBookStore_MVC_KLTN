package com.nhom14.webbookstore.controller.customer;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nhom14.webbookstore.service.GoogleAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.service.AccountService;
import com.nhom14.webbookstore.service.CloudinaryService;

@Controller
public class AccountController {
	private AccountService accountService;
	private CloudinaryService cloudinaryService;
	private GoogleAuthService googleAuthService;

	@Autowired
	public AccountController(AccountService accountService, CloudinaryService cloudinaryService, GoogleAuthService googleAuthService) {
		super();
		this.accountService = accountService;
		this.cloudinaryService = cloudinaryService;
		this.googleAuthService = googleAuthService;
	}
	
	@GetMapping("/customer/registeraccount")
	public String registerAccountForm() {
		return "customer/registeraccount";
	}
	
	@PostMapping("/customer/registeraccount")
    public String registerAccount(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("address") String address,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("gender") String gender,
            @RequestParam("dob") String dob,
            RedirectAttributes redirectAttributes
    ) throws ParseException {
		// Kiểm tra mật khẩu có phải là mật khẩu mạnh không 
	    if(!(password.length() >= 8 
	            && password.matches(".*[A-Z].*") 
	            && password.matches(".*[a-z].*") 
	            && password.matches(".*\\d.*") 
	            && password.matches(".*\\W.*"))) {
		    	// Hiển thị thông báo khi mật khẩu yếu
		    	redirectAttributes.addAttribute("message", "Mật khẩu không đủ mạnh! Mật khẩu mới phải có ít nhất 8 ký tự và"
		    			+ " chứa ít nhất một chữ cái viết hoa, một chữ cái viết thường, một số và một ký tự đặc biệt.");
		        return "redirect:/customer/registeraccount";
	    }
        //BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //String hashedPassword = passwordEncoder.encode(password);
		// Băm mật khẩu sử dụng bcrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Date dateOfBirth = Date.valueOf(dob);

        Account account = new Account(username, hashedPassword, address, phoneNumber, email, 1, 1);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setGender(gender);
		account.setDateOfBirth(dateOfBirth);
        account.setImg("");

        try {
            Account existingAccount = accountService.findAccountByUsername(username);
            if(existingAccount == null) {
            	existingAccount = accountService.findAccountByPhoneNumber(phoneNumber);
            }
            if(existingAccount == null) {
            	existingAccount = accountService.findAccountByEmail(email);
            }
            
            if (existingAccount != null) {
                // Username already exists
            	redirectAttributes.addAttribute("message", "Tên tài khoản, số điện thoại hoặc email đã tồn tại. Vui lòng tạo lại.");
            	return "redirect:/customer/registeraccount";
            } else {
                accountService.addAccount(account);
                redirectAttributes.addAttribute("message", "Đăng ký tài khoản thành công.");
                return "redirect:/customer/loginaccount";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("message", "Đã xảy ra lỗi. Vui lòng thử lại sau.");
            return "redirect:/customer/registeraccount";
        } 
    }
	
	@GetMapping("/customer/loginaccount")
	public String loginAccountForm(Model model) {
		// tạo đối tượng account cho form dữ liệu
		Account account = new Account();
		model.addAttribute("account", account);
		return "customer/loginaccount";
	}
	
	@PostMapping("/customer/loginaccount")
    public String loginAccount(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               HttpServletRequest request,
                               Model model,
                               RedirectAttributes redirectAttributes) {
		session.invalidate();
        session = request.getSession(true);
		// Kiểm tra xem người dùng có từng đăng nhập bằng Google và chưa đổi mật khẩu mặc định không
		Account isGoogle = accountService.findAccountByUsername(username);
		if (isGoogle != null && Objects.equals(isGoogle.getPassword(), "logingoogle") && isGoogle.getAccountType() == 1 && isGoogle.getStatus() == 1) {
			redirectAttributes.addAttribute("message", "Lần trước bạn đã đăng nhập bằng Google, vui lòng dùng Google để đăng nhập, bạn chưa thiết lập mật khẩu, nếu muốn dùng mật khẩu hãy thêm mật khẩu nhé!");
			return "redirect:/customer/loginaccount";
		}
        // Kiểm tra đăng nhập bằng phương thức checkLogin
        boolean isValid = accountService.checkLogin(username, password);

        if (isValid) {
            // Nếu đăng nhập thành công, lưu thông tin tài khoản vào session
            Account account = accountService.findAccountByUsername(username);
            //model.addAttribute("account", account);
            session.setAttribute("account", account);
            // Nếu đăng nhập thành công, hiển thị thông báo thành công và quay lại trang chủ
            redirectAttributes.addAttribute("message", "Đăng nhập thành công!");
            return "redirect:/viewbooks";
        } else {
            // Nếu đăng nhập thất bại, hiển thị thông báo lỗi và quay lại trang đăng nhập
        	redirectAttributes.addAttribute("message", "Sai tên đăng nhập hoặc mật khẩu");
            return "redirect:/customer/loginaccount";
        }
    }

	@GetMapping("/customer/login/google")
	public void redirectToGoogleLogin(HttpServletResponse response) throws IOException {
		String clientId = System.getenv("GOOGLE_CLIENT_ID");
		String redirectUri = System.getenv("GOOGLE_REDIRECT_URI");

		GoogleAuthorizationCodeRequestUrl urlBuilder =
				new GoogleAuthorizationCodeRequestUrl(
						clientId,
						redirectUri,
						Arrays.asList("https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email"))
						.setAccessType("offline")
						.setApprovalPrompt("force");

		String loginUrl = urlBuilder.build();

		response.sendRedirect(loginUrl);
	}


	@GetMapping("/customer/login/google_return")
	public String handleGoogleLogin(@RequestParam("code") String authCode,
									HttpSession session,
									RedirectAttributes redirectAttributes) throws IOException {
		String clientId = System.getenv("GOOGLE_CLIENT_ID");
		String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
		String redirectUri = System.getenv("GOOGLE_REDIRECT_URI");
		// Tạo yêu cầu để lấy mã thông báo ID từ Google
		GoogleTokenResponse tokenResponse =
				new GoogleAuthorizationCodeTokenRequest(
						new NetHttpTransport(),
						JacksonFactory.getDefaultInstance(),
						"https://oauth2.googleapis.com/token",
						clientId,
						clientSecret,
						authCode,
						redirectUri)  // Chỉ định cùng URI chuyển hướng mà bạn đã sử dụng trước đó
						.execute();

		// Lấy mã thông báo ID từ phản hồi
		String idToken = tokenResponse.getIdToken();

		GoogleIdToken.Payload payload = googleAuthService.authenticate(idToken);
		if (payload != null) {
			String email = payload.getEmail();
			String username = email.substring(0, email.indexOf('@'));  // Lấy phần chữ trước @ trong email
			Account account = accountService.findAccountByEmail(email);
			if (account == null) {
				// Tạo tài khoản mới
				account = new Account();
				account.setEmail(email);
				account.setFirstName("Chưa");
				account.setLastName("Đặt");
				account.setImg("https://res.cloudinary.com/dosdzo1lg/image/upload/v1687862555/Booktopia/img_account/account_default.jpg");
				Object givenName = payload.get("given_name");
				if (givenName != null) {
					account.setFirstName(givenName.toString());
				}
				Object familyName = payload.get("family_name");
				if (familyName != null) {
					account.setLastName(familyName.toString());
				}
				//Ngày sinh được null nên bỏ qua
				account.setAccountType(1);
				account.setStatus(1);
				//Giới tính được null nên bỏ qua
				account.setAddress("Chưa điền thông tin");
				account.setPhoneNumber("Chưa điền thông tin");

				Object picture = payload.get("picture");
				if (picture != null) {
					account.setImg(picture.toString());
				}  // Thiết lập hình ảnh hồ sơ từ Google
				account.setUsername(username);
				account.setPassword("logingoogle"); // Sửa lại cách xử lý cho đăng nhập và đổi mật khẩu
				// Phần login: tìm tài khoản bằng Username nếu có mk là logingoole thì sẽ từ chối đăng nhập
				// thông báo là vui lòng đăng nhập bằng Google sau đó thêm mật khẩu mới
				// Phần đổi mật khẩu sẽ đổi tên thành thêm mật khẩu
				accountService.addAccount(account);
			}
			// Tạo phiên đăng nhập
			session.setAttribute("account", account);
			// Nếu đăng nhập thành công, hiển thị thông báo thành công và quay lại trang chủ
			redirectAttributes.addAttribute("message", "Đăng nhập bằng Google thành công!");
			return "redirect:/viewaccount";
		} else {
			// Nếu đăng nhập thất bại, hiển thị thông báo lỗi và quay lại trang đăng nhập
			redirectAttributes.addAttribute("message", "Có lỗi xảy ra, vui lòng thử lại!");
			return "redirect:/customer/loginaccount";
		}
	}

	@GetMapping("/customer/login/facebook")
	public String handleFacebookLogin(@RequestParam("code") String authCode,
									HttpSession session,
									RedirectAttributes redirectAttributes) throws IOException {
		String clientId = System.getenv("GOOGLE_CLIENT_ID");
		String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
		String redirectUri = System.getenv("GOOGLE_REDIRECT_URI");
		// Tạo yêu cầu để lấy mã thông báo ID từ Google
		GoogleTokenResponse tokenResponse =
				new GoogleAuthorizationCodeTokenRequest(
						new NetHttpTransport(),
						JacksonFactory.getDefaultInstance(),
						"https://oauth2.googleapis.com/token",
						clientId,
						clientSecret,
						authCode,
						redirectUri)  // Chỉ định cùng URI chuyển hướng mà bạn đã sử dụng trước đó
						.execute();

		// Lấy mã thông báo ID từ phản hồi
		String idToken = tokenResponse.getIdToken();

		GoogleIdToken.Payload payload = googleAuthService.authenticate(idToken);
		if (payload != null) {
			String email = payload.getEmail();
			String username = email.substring(0, email.indexOf('@'));  // Lấy phần chữ trước @ trong email
			Account account = accountService.findAccountByEmail(email);
			if (account == null) {
				// Tạo tài khoản mới
				account = new Account();
				account.setEmail(email);
				account.setFirstName("Chưa");
				account.setLastName("Đặt");
				account.setImg("https://res.cloudinary.com/dosdzo1lg/image/upload/v1687862555/Booktopia/img_account/account_default.jpg");
				Object givenName = payload.get("given_name");
				if (givenName != null) {
					account.setFirstName(givenName.toString());
				}
				Object familyName = payload.get("family_name");
				if (familyName != null) {
					account.setLastName(familyName.toString());
				}
				//Ngày sinh được null nên bỏ qua
				account.setAccountType(1);
				account.setStatus(1);
				//Giới tính được null nên bỏ qua
				account.setAddress("Chưa điền thông tin");
				account.setPhoneNumber("Chưa điền thông tin");

				Object picture = payload.get("picture");
				if (picture != null) {
					account.setImg(picture.toString());
				}  // Thiết lập hình ảnh hồ sơ từ Google
				account.setUsername(username);
				account.setPassword("logingoogle"); // Sửa lại cách xử lý cho đăng nhập và đổi mật khẩu
				// Phần login: tìm tài khoản bằng Username nếu có mk là logingoole thì sẽ từ chối đăng nhập
				// thông báo là vui lòng đăng nhập bằng Google sau đó thêm mật khẩu mới
				// Phần đổi mật khẩu sẽ đổi tên thành thêm mật khẩu
				accountService.addAccount(account);
			}
			// Tạo phiên đăng nhập
			session.setAttribute("account", account);
			// Nếu đăng nhập thành công, hiển thị thông báo thành công và quay lại trang chủ
			redirectAttributes.addAttribute("message", "Đăng nhập bằng Google thành công!");
			return "redirect:/";
		} else {
			// Nếu đăng nhập thất bại, hiển thị thông báo lỗi và quay lại trang đăng nhập
			redirectAttributes.addAttribute("message", "Có lỗi xảy ra, vui lòng thử lại!");
			return "redirect:/customer/loginaccount";
		}
	}
	
	@GetMapping("/customer/logoutaccount")
	public String showLogout() {
		return "customer/logoutaccount";
	}
	
	@PostMapping("/customer/logoutaccount")
	public String logoutAccount(HttpServletRequest request) {
		HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
	}
	
	@GetMapping("/viewaccount")
	public String viewAccount(Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }
	    
	    // Lưu thông tin tài khoản vào model
        model.addAttribute("account", account);
        // Sinh giá trị ngẫu nhiên
        Random random = new Random();
        int randomNumber = random.nextInt();
        model.addAttribute("randomNumber", randomNumber);

        // Forward đến trang xem thông tin tài khoản
        return "customer/viewaccount";
	}
	
	@GetMapping("/updateaccount")
	public String showUpdateAccountForm(Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }
	    
	    // Lưu thông tin tài khoản vào model
	    model.addAttribute("account", account);

	    // Forward đến trang xem thông tin tài khoản
	    return "customer/updateaccount";
	}
	
	@PostMapping("/updateaccount")
	public String updateAccount(@ModelAttribute("account") Account accountParam,
	                            @RequestParam("image") MultipartFile image,
	                            @RequestParam("dob") String dob,
	                            HttpSession session,
	                            Model model,
	                            RedirectAttributes redirectAttributes) {

	    Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }

	    Account updateAccount = account;

		// Kiểm tra xem username, sdt, email mới có bị trùng với của người khác không
		Account existingAccount = null;
		if (!Objects.equals(updateAccount.getUsername(), accountParam.getUsername())) {
			existingAccount = accountService.findAccountByUsername(accountParam.getUsername());
			if (existingAccount != null) {
				redirectAttributes.addAttribute("message", "Tên tài khoản đã tồn tại. Vui lòng nhập giá trị khác.");
				return "redirect:/viewaccount";
			}
		}

		if (!Objects.equals(updateAccount.getPhoneNumber(), accountParam.getPhoneNumber())) {
			existingAccount = accountService.findAccountByPhoneNumber(accountParam.getPhoneNumber());
			if (existingAccount != null) {
				redirectAttributes.addAttribute("message", "Số điện thoại đã tồn tại. Vui lòng nhập giá trị khác.");
				return "redirect:/viewaccount";
			}
		}

		if (!Objects.equals(updateAccount.getEmail(), accountParam.getEmail())) {
			existingAccount = accountService.findAccountByEmail(accountParam.getEmail());
			if (existingAccount != null) {
				redirectAttributes.addAttribute("message", "Email đã tồn tại. Vui lòng nhập giá trị khác.");
				return "redirect:/viewaccount";
			}
		}

	    try {
	        if (!image.isEmpty()) {
	        	// Tạo public ID cho hình ảnh trên Cloudinary (sử dụng id người dùng)
                String publicId = "WebBookStoreKLTN/img_account/account_" + updateAccount.getId();

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
	        accountService.updateAccount(updateAccount);

	        // Lưu thông tin tài khoản mới vào session
	        session.setAttribute("account", updateAccount);

	        // Đến trang xem thông tin tài khoản
	        redirectAttributes.addAttribute("message", "Đã cập nhật thành công!");
	        return "redirect:/viewaccount";
	    } catch (IOException e) {
	        e.printStackTrace();
	        redirectAttributes.addAttribute("message", "Đã xảy ra lỗi khi cập nhật tài khoản.");
	        return "redirect:/viewaccount";
	    }
    }
	

	@GetMapping("/changepassword")
	public String showChangePasswordForm(HttpSession session) {
		Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }
	    
	    return "customer/changepassword";
	}
	
	@PostMapping("/changepassword")
	public String changePassword(HttpSession session, @RequestParam("currentPassword") String currentPassword, 
	                             @RequestParam("newPassword") String newPassword, 
	                             @RequestParam("confirmPassword") String confirmPassword, 
	                             Model model,
	                             RedirectAttributes redirectAttributes) {
	    Account account = (Account) session.getAttribute("account");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (account == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/customer/loginaccount";
	    }
	    
	    // Kiểm tra mật khẩu hiện tại
	    if (!BCrypt.checkpw(currentPassword, account.getPassword())) {
	        // Hiển thị thông báo mật khẩu hiện tại không đúng
	    	redirectAttributes.addAttribute("message", "Mật khẩu hiện tại không đúng. Vui lòng thử lại.");
	        return "redirect:/changepassword";
	    }
	    
	    // Kiểm tra mật khẩu mới có giống mật khẩu hiện tại không
	    if (BCrypt.checkpw(newPassword, account.getPassword())) {
	        // Hiển thị thông báo giống mật khẩu cũ
	    	redirectAttributes.addAttribute("message", "Vui lòng chọn mật khẩu khác mật khẩu cũ!");
	    	return "redirect:/changepassword";
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
		        return "redirect:/changepassword";
	    }
	    
	    // Kiểm tra mật khẩu mới có giống mật khẩu nhập lại không 
	    if (!newPassword.equals(confirmPassword)) {
	        // Hiển thị thông báo mật khẩu nhập lại không khớp
	    	redirectAttributes.addAttribute("message", "Mật khẩu nhập lại không khớp. Vui lòng thử lại.");
	        return "redirect:/changepassword";
	    }
	    
	    // Băm mật khẩu mới sử dụng bcrypt
	    String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
	    // Cập nhật mật khẩu mới cho tài khoản
	    account.setPassword(hashedNewPassword);
	    // Gọi phương thức updateAccount trong AccountService để cập nhật thông tin tài khoản
	    accountService.updateAccount(account);
	    // Hiển thị thông báo thành công
	    redirectAttributes.addAttribute("message", "Thay đổi mật khẩu thành công.");
	    return "redirect:/viewaccount";
	}
	@GetMapping("/addpassword")
	public String showAddPasswordForm(HttpSession session) {
		Account account = (Account) session.getAttribute("account");

		// Kiểm tra xem người dùng đã đăng nhập hay chưa
		if (account == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/customer/loginaccount";
		}

		return "customer/addpassword";
	}

	@PostMapping("/addpassword")
	public String addPassword(HttpSession session,
								 @RequestParam("newPassword") String newPassword,
								 @RequestParam("confirmPassword") String confirmPassword,
								 Model model,
								 RedirectAttributes redirectAttributes) {
		Account account = (Account) session.getAttribute("account");

		// Kiểm tra xem người dùng đã đăng nhập hay chưa
		if (account == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/customer/loginaccount";
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
			return "redirect:/addpassword";
		}

		// Kiểm tra mật khẩu mới có giống mật khẩu nhập lại không
		if (!newPassword.equals(confirmPassword)) {
			// Hiển thị thông báo mật khẩu nhập lại không khớp
			redirectAttributes.addAttribute("message", "Mật khẩu nhập lại không khớp. Vui lòng thử lại.");
			return "redirect:/addpassword";
		}

		// Băm mật khẩu mới sử dụng bcrypt
		String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		// Cập nhật mật khẩu mới cho tài khoản
		account.setPassword(hashedNewPassword);
		// Gọi phương thức updateAccount trong AccountService để cập nhật thông tin tài khoản
		accountService.updateAccount(account);
		// Hiển thị thông báo thành công
		redirectAttributes.addAttribute("message", "Thêm mật khẩu thành công.");
		return "redirect:/viewaccount";
	}
}
