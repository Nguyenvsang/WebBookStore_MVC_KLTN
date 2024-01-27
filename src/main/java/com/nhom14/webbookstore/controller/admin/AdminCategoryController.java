package com.nhom14.webbookstore.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.nhom14.webbookstore.entity.Category;
import com.nhom14.webbookstore.service.BookService;
import com.nhom14.webbookstore.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminCategoryController {
	private CategoryService categoryService;
	private BookService bookService;
	
	@Autowired
	public AdminCategoryController(CategoryService categoryService, BookService bookService) {
		super();
		this.categoryService = categoryService;
		this.bookService = bookService;
	}
	
	@GetMapping("/managecategories")
	public String manageCategory(@RequestParam(value = "status", required = false) Integer statusId,
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
	    
	    List<Category> categories;
	    int totalCategories;
	    
	    // Số danh mục hiển thị trên mỗi trang	
        int recordsPerPage = 10;
        int start;
        int end;
        int totalPages;
        
        if (statusId == null || (statusId == -1)) {
        	categories = categoryService.getAllCategories();
        } else {
        	categories = categoryService.getCategoriesByStatusID(statusId);
        }
        
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
        	categories = categoryService.searchCategoriesByKeyword(categories, searchKeyword);
        }
        
        // Lấy tổng số lượng danh mục
        totalCategories = categories.size();
        
        // Tính toán vị trí bắt đầu và kết thúc của danh mục trên trang hiện tại
        start = (currentPage - 1) * recordsPerPage;
        end = Math.min(start + recordsPerPage, totalCategories);
        
        // Lấy danh sách danh mục trên trang hiện tại
        List<Category> categoriesOnPage = categories.subList(start, end);
        
        // Tính toán số trang
        totalPages = (int) Math.ceil((double) totalCategories / recordsPerPage);
        
        // Tổng số tất cả các danh mục
        int totalAllCategories = categoryService.getAllCategories().size();
        
        model.addAttribute("categories", categoriesOnPage);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalAllCategories", totalAllCategories);
        
        return "admin/managecategories";
	}
	
	@PostMapping("/updatestatuscategory")
	public ResponseEntity<String> updateStatusCategory(@RequestParam("categoryId") int categoryId, 
			@RequestParam("status") int status,
			HttpSession session) {
	    
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	    	// Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Location", "/loginadmin");
	        return ResponseEntity.status(HttpStatus.FOUND)
	                .headers(headers)
	                .body("User not logged in");
	    }
		
	    // Lấy Category từ categoryId 
		Category category = categoryService.getCategoryById(categoryId);
	    
	    if (category == null) {
	        return ResponseEntity.badRequest().body("Category not found");
	    }
	    
	    // Kiểm tra nếu trạng thái mới của danh mục là 0 và đã được lưu thành công vào cơ sở dữ liệu
        // Thì cập nhật trạng thái của các cuốn sách thuộc danh mục này thành 0
	    category.setStatus(status);
        categoryService.updateCategory(category);
	    if (status == 0) {
            // Cập nhật trạng thái của các cuốn sách thuộc danh mục thành 0
            List<Book> booksInCategory = bookService.getBooksByCategory(categoryId);
            for (Book book : booksInCategory) {
                book.setStatus(status);
                bookService.updateBook(book);
            }
	    }
	    
	    return ResponseEntity.ok("success");
	}
	
	@GetMapping("/updatecategory")
	public String showUpdateCategoryForm(@RequestParam("categoryId") Integer categoryId,
			Model model,
			HttpSession session) {
		
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Lấy category từ categoryId
        Category category = categoryService.getCategoryById(categoryId);
        
        // Đặt thuộc tính vào model để sử dụng trong view
	    model.addAttribute("category", category);
	    
	    return "admin/updatecategory";
	    
	}
	
	@PostMapping("/updatecategory")
	public String updateCategory(
	        @RequestParam("id") int categoryId,
	        @RequestParam("name") String categoryName,
	        @RequestParam("status") int status,
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {
	    Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

	    // Lấy category từ categoryId
	    Category category = categoryService.getCategoryById(categoryId);

	    if (category != null) {
	        // Kiểm tra nếu trạng thái mới của danh mục là 0 và đã được lưu thành công vào cơ sở dữ liệu
	        // Thì cập nhật trạng thái của các cuốn sách thuộc danh mục này thành 0
	        if (status == 0) {
	            category.setName(categoryName);
	            category.setStatus(status);
	            categoryService.updateCategory(category);

	            // Cập nhật trạng thái của các cuốn sách thuộc danh mục thành 0
	            List<Book> booksInCategory = bookService.getBooksByCategory(categoryId);
	            for (Book book : booksInCategory) {
	                book.setStatus(status);
	                bookService.updateBook(book);
	            }
	        } else {
	            // Nếu trạng thái mới của danh mục không phải là 0, chỉ cập nhật thông tin của danh mục
	            category.setName(categoryName);
	            category.setStatus(status);
	            categoryService.updateCategory(category);
	        }

	        // Hiển thị thông báo cập nhật thành công
	        redirectAttributes.addAttribute("message", "Cập nhật thành công");
	        redirectAttributes.addAttribute("categoryId", categoryId);
	    } else {
	        redirectAttributes.addAttribute("message", "Không tìm thấy danh mục");
	    }

	    // Chuyển hướng đến trang updatecategory
	    return "redirect:/updatecategory";
	}
	
	@GetMapping("/addcategory")
	public String showAddCategoryForm(HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    return "admin/addcategory";
	}
	
	@PostMapping("/addcategory")
	public String addCategory(@RequestParam("name") String categoryName, 
			@RequestParam("status") int status, 
			HttpSession session,
			RedirectAttributes redirectAttributes) {
	    Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

	    // Tạo Category mới
	    Category category = new Category(categoryName, status);

	    // Gọi phương thức addCategory từ service để thêm mới danh mục
	    categoryService.addCategory(category);

	    redirectAttributes.addAttribute("message", "Đã thêm danh mục mới thành công");
	    // Chuyển hướng đến trang addcategory.html sau khi thêm mới
	    return "redirect:/addcategory";
	}
}
