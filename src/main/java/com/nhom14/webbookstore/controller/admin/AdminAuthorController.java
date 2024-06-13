package com.nhom14.webbookstore.controller.admin;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
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
import com.nhom14.webbookstore.entity.Author;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.Category;
import com.nhom14.webbookstore.service.AuthorService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminAuthorController {

	private AuthorService authorService;

	@Autowired
	public AdminAuthorController(AuthorService authorService) {
		super();
		this.authorService = authorService;
	}
	
	@GetMapping("/manageauthors")
	public String manageAuthors(@RequestParam(value = "search", required = false) String searchKeyword,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
            Model model,
            HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    List<Author> authors = authorService.getAllAuthors();
	    int totalAuthors;
	    // Số tác giả hiển thị trên mỗi trang	
        int recordsPerPage = 10;
        int start;
        int end;
        int totalPages;
        
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
        	authors = authorService.searchAuthorsByKeyword(authors, searchKeyword);
        }
        
        totalAuthors = authors.size();
        
        // Tính toán vị trí bắt đầu và kết thúc của tác giả trên trang hiện tại
        start = (currentPage - 1) * recordsPerPage;
        end = Math.min(start + recordsPerPage, totalAuthors);
        
        // Lấy danh sách tác giả trên trang hiện tại
        List<Author> authorsOnPage = authors.subList(start, end);
        
        // Tính toán số trang
        totalPages = (int) Math.ceil((double) totalAuthors / recordsPerPage);
        
        // Tổng số tất cả các tác giả
        int totalAllAuthors = authorService.getAllAuthors().size();
        
        model.addAttribute("authors", authorsOnPage);
        model.addAttribute("totalAuthors", totalAuthors);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalAllAuthors", totalAllAuthors);

        return "admin/manageauthors";
	}
	
	@GetMapping("/addauthor")
	public String showAddAuthorForm(Model model,
									HttpSession session,
									HttpServletRequest request) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

		// Lưu URL trang trước đó vào session
		String referer = request.getHeader("Referer");
		String currentUrl = request.getRequestURL().toString();

		// Kiểm tra xem referer có khác với URL hiện tại hay không (tránh trường hợp 1 trang lặp lại)
		// và có chứa cụm "/manageauthors" hoặc "/addbook"(tránh trường hợp vượt quá kiểm soát)
		if (referer != null && !referer.equals(currentUrl) && (referer.contains("/manageauthors") || referer.contains("/addbook"))) {
			session.setAttribute("previousUrl", referer);
		}
		return "admin/addauthor";
	}
	
	@PostMapping("/addauthor")
	public String addAuthor(@RequestParam("name") String authorName, 
			@RequestParam("bio") String authorBio,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
	    Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

		// Kiểm tra xem tên tác giả đã tồn tại trong cơ sở dữ liệu hay chưa
		Author existingAuthor = authorService.findAuthorByName(authorName);
		if (existingAuthor != null) {
			// Nếu tên tác giả đã tồn tại, trả về thông báo lỗi
			redirectAttributes.addAttribute("message", "Đã tồn tại tác giả này trong cơ sở dữ liệu");
			return "redirect:/addauthor";
		}

	    // Tạo Author mới
	    Author author = new Author(authorName, authorBio);

	    // Gọi phương thức addAuthor từ service để thêm mới tác giả
	    authorService.addAuthor(author);

	    redirectAttributes.addAttribute("message", "Đã thêm tác giả mới thành công");
	    // Chuyển hướng đến trang addauthor.html sau khi thêm mới
	    return "redirect:/addauthor";
	}
	
	@GetMapping("/managedetailauthor")
	public String manageDetailAuthor(@RequestParam("authorId") Integer authorId, 
			Model model,
			HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Truy xuất dữ liệu từ nguồn dữ liệu
	    Author author = authorService.getAuthorById(authorId);
	    
	    // Đặt thuộc tính vào model để sử dụng trong view
	    model.addAttribute("author", author);
	    
	    // Forward đến trang chi tiết tác giả
	    return "admin/managedetailauthor";
	}
	
	@GetMapping("/updateauthor")
	public String showUpdateAuthorForm(@RequestParam("authorId") Integer authorId,
			Model model,
			HttpSession session) {
		
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Lấy author từ authorId
        Author author = authorService.getAuthorById(authorId);
        
        // Đặt thuộc tính vào model để sử dụng trong view
	    model.addAttribute("author", author);
	    
	    return "admin/updateauthor";
	    
	}
	
	@PostMapping("/updateauthor")
	public String updateAuthor(@RequestParam("id") Integer authorId,
			@RequestParam("name") String authorName,
	        @RequestParam("bio") String bio,
			Model model,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

	    // Lấy author từ authorId
	    Author author = authorService.getAuthorById(authorId);
	    
	    if (author != null) {
			// Kiểm tra xem tên tác giả mới có trùng với tên của bất kỳ tác giả hiện có nào không
			Author existingAuthor = authorService.findAuthorByName(authorName);
			if (existingAuthor != null && existingAuthor.getId() != authorId) {
				// Nếu tên tác giả mới trùng với tên của một tác giả hiện có khác, trả về thông báo lỗi
				redirectAttributes.addAttribute("message", "Tên tác giả đã tồn tại trong cơ sở dữ liệu");
				return "redirect:/updateauthor?authorId=" + author.getId();
			}

	    	author.setName(authorName);
	    	author.setBio(bio);
	    	authorService.updateAuthor(author);
	    	// Hiển thị thông báo cập nhật thành công
	        redirectAttributes.addAttribute("message", "Cập nhật thành công");
	        redirectAttributes.addAttribute("authorId", authorId);
	    } else {
	    	redirectAttributes.addAttribute("message", "Không tìm thấy tác giả");
	    }
	    
	    // Chuyển hướng đến trang updateauthor
	    return "redirect:/updateauthor?authorId=" + author.getId();
	}
}
