package com.nhom14.webbookstore.controller.admin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.model.lean_model.DiscountLeanModel;
import com.nhom14.webbookstore.model.response_model.BookResponseModel;
import com.nhom14.webbookstore.service.*;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhom14.webbookstore.service.BookImageService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminBookController {

	private BookService bookService;
	private CategoryService categoryService;
	private CloudinaryService cloudinaryService;
	private BookAuthorService bookAuthorService;
	private AuthorService authorService;
	private BookImageService bookImageService;
	private BookImportService bookImportService;
	private DiscountService discountService;
	private ModelMapper modelMapper;
	
	@Autowired
	public AdminBookController(BookService bookService, CategoryService categoryService,
                               CloudinaryService cloudinaryService, BookAuthorService bookAuthorService,
                               AuthorService authorService, BookImageService bookImageService, BookImportService bookImportService, DiscountService discountService, ModelMapper modelMapper) {
		super();
		this.bookService = bookService;
		this.categoryService = categoryService;
		this.cloudinaryService = cloudinaryService;
		this.bookAuthorService = bookAuthorService;
		this.authorService = authorService;
		this.bookImageService = bookImageService;
        this.bookImportService = bookImportService;
        this.discountService = discountService;
        this.modelMapper = modelMapper;
    }
	
//	@GetMapping("/managebooks")
//	public String manageBooks(@RequestParam(value = "status", required = false) Integer status,
//			@RequestParam(value = "category", required = false) Integer categoryId,
//            @RequestParam(value = "search", required = false) String searchKeyword,
//            @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
//            @RequestParam(value = "pricemin", required = false) Double priceMin,
//            @RequestParam(value = "pricemax", required = false) Double priceMax,
//            @RequestParam(value = "priceoption", required = false) Integer priceOption,
//            Model model,
//            HttpSession session) {
//		Account admin = (Account) session.getAttribute("admin");
//
//	    // Kiểm tra xem admin đã đăng nhập hay chưa
//	    if (admin == null) {
//	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
//	        return "redirect:/loginadmin";
//	    }
//
//		List<Book> books;
//        int totalBooks;
//        // Số sách hiển thị trên mỗi trang
//        int recordsPerPage = 10;
//        int start;
//        int end;
//        int totalPages;
//
//        if (status == null) {
//            books = bookService.getAllBooks();
//            if (books.isEmpty()) {
//                model.addAttribute("message", "Hiện không có sách nào");
//                return "admin/managebooks";
//            }
//        } else {
//            books = bookService.getBooksByStatus(status);
//            if (books.isEmpty()) {
//                model.addAttribute("message", "Không tìm thấy sách theo trạng thái này");
//                return "admin/managebooks";
//            }
//            // Thêm để hiển thị theo status cho các trang phía sau
//            model.addAttribute("status", status);
//        }
//
//        if (categoryId != null) {
//            books = bookService.getBooksByCategory(categoryId);
//            if (books.isEmpty()) {
//                model.addAttribute("message", "Không tìm thấy sách theo danh mục này");
//                return "admin/managebooks";
//            }
//            // Thêm để hiển thị theo catagory cho các trang phía sau
//            model.addAttribute("categoryId", categoryId);
//        }
//
//        if (searchKeyword != null && !searchKeyword.isEmpty()) {
//            books = bookService.searchBooksByKeyword(books, searchKeyword);
//            if (books.isEmpty()) {
//                model.addAttribute("message", "Không tìm thấy sách nào với từ khóa đã nhập");
//                return "admin/managebooks";
//            } else { //Thêm để hiển thị theo từ khóa cho các trang phía sau
//            	model.addAttribute("search", searchKeyword);
//            }
//        }
//
//        // Lọc sách theo khoảng giá
//        if (priceMin != null && priceMax != null) {
//            books = bookService.filterBooksByPriceRange(books, priceMin, priceMax);
//            if (books.isEmpty()) {
//                model.addAttribute("message", "Không tìm thấy sách nào trong khoảng giá đã chọn");
//                return "admin/managebooks";
//            } else { //Thêm để hiển thị theo khoảng giá cho các trang phía sau
//            	model.addAttribute("pricemin", priceMin);
//            	model.addAttribute("pricemax", priceMax);
//            }
//        }
//
//        // Sếp sách tăng dần nếu giá trị priceOption là 12, giảm dần nếu giá trị là 21
//        if (priceOption != null) {
//            if (priceOption == 12) {
//                books = bookService.sortBooksByPriceAscending(books);
//                //Thêm để hiển thị theo khoảng giá cho các trang phía sau
//                model.addAttribute("priceoption", priceOption);
//            } else if (priceOption == 21) {
//                books = bookService.sortBooksByPriceDescending(books);
//                //Thêm để hiển thị theo khoảng giá cho các trang phía sau
//                model.addAttribute("priceoption", priceOption);
//            }
//        }
//
//        totalBooks = books.size();
//
//        // Tính toán vị trí bắt đầu và kết thúc của sách trên trang hiện tại
//        start = (currentPage - 1) * recordsPerPage;
//        end = Math.min(start + recordsPerPage, totalBooks);
//
//        // Lấy danh sách sách trên trang hiện tại
//        List<Book> booksOnPage = books.subList(start, end);
//
//        // Tính toán số trang
//        totalPages = (int) Math.ceil((double) totalBooks / recordsPerPage);
//
//        // Tổng số tất cả các đầu sách
//        int totalAllBooks = bookService.getAllBooks().size();
//
//        model.addAttribute("books", booksOnPage);
//        model.addAttribute("totalBooks", totalBooks);
//        model.addAttribute("totalPages", totalPages);
//        model.addAttribute("currentPage", currentPage);
//        List<Category> categories = categoryService.getAllCategories();
//        model.addAttribute("categories", categories);
//        model.addAttribute("totalAllBooks", totalAllBooks);
//
//        return "admin/managebooks";
//
//	}

	@GetMapping("/managebooks")
	public String manageBooks(
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "categoryId", required = false) Integer categoryId,
			@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			@RequestParam(value = "priceMin", required = false) Double priceMin, // Lọc sách theo khoảng giá
			@RequestParam(value = "priceMax", required = false) Double priceMax, // Lọc sách theo khoảng giá
			@RequestParam(value = "publisher", required = false) String publisher, // Lọc sách theo tên nhà xuất bản
			@RequestParam(value = "sortOption", required = false) String sortOption,
			//asc- tăng dần-12, desc- giảm dần-21
			//các tùy chọn: sp12 (giá bán tăng dần), sp21(giá bán giảm dần),
			// n12 (tên tăng dần theo bảng chữ cái), n21(tên giảm dần theo bảng chữ cái)
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(value = "size", required = false, defaultValue = "12") Integer pageSize,
			Model model,
			HttpSession session
	) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem admin đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

		Sort sort = handleSortOption(sortOption);

		Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

		// Gọi phương thức getFilteredBooks với các tham số tìm kiếm và lọc, còn kinh doanh + ngung kinh doanh
		Page<Book> books = bookService.getFilteredBooks(status, categoryId, searchKeyword, priceMin, priceMax, publisher, pageable);

		Page<BookResponseModel> bookResponseModels = books.map(this::convertToBookResponseModel);

		model.addAttribute("books", bookResponseModels);
		List<Category> categories = categoryService.getAllCategories();
		model.addAttribute("categories", categories);

		// Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
		Map<String, Object> params = new HashMap<>();
		params.put("status", status);
		params.put("categoryId", categoryId);
		params.put("searchKeyword", searchKeyword);
		params.put("priceMin", priceMin);
		params.put("priceMax", priceMax);
		params.put("publisher", publisher);
		params.put("sortOption", sortOption);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (entry.getValue() != null) {
				model.addAttribute(entry.getKey(), entry.getValue());
			}
		}


		return "admin/managebooks";
	}

	public Sort handleSortOption(String sortOption) {
		Sort sort = Sort.unsorted();
		if (sortOption != null) {
			sort = switch (sortOption) {
				case "sp12" -> sort.and(Sort.by("sellPrice").ascending());
				case "sp21" -> sort.and(Sort.by("sellPrice").descending());
				case "n12" -> sort.and(Sort.by("name").ascending());
				case "n21" -> sort.and(Sort.by("name").descending());
				default -> sort;
			};
		}
		return sort;
	}
	
	@PostMapping("/updatestatusbook")
	public ResponseEntity<String> updateStatusBook(@RequestParam("bookId") int bookId, 
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
		
	    // Lấy Book từ bookId 
		Book book = bookService.getBookById(bookId);
	    
	    if (book == null) {
	        return ResponseEntity.badRequest().body("Book not found");
	    }
	    
	    book.setStatus(status);
	    bookService.updateBook(book);
	    
	    return ResponseEntity.ok("success");
	}
	
	@GetMapping("/addbook")
	public String showAddBookForm(Model model, 
			HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Lấy danh sách tất cả tác giả
        List<Author> allAuthors = authorService.getAllAuthors();
	    
	    List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("allAuthors", allAuthors);
		
		return "admin/addbook";
	}
	
	@PostMapping("/addbook")
	public String addBook(@ModelAttribute("book") Book bookParam,
            @RequestParam("image1") MultipartFile image1,
            @RequestParam("image2") MultipartFile image2,
            @RequestParam("image3") MultipartFile image3,
            @RequestParam("image4") MultipartFile image4,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("authorsIds") List<Integer> authorIds,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Kiểm tra xem tên sách mới có trùng với tên sách nào trong cơ sở dữ liệu không
	    if (bookService.getBookByName(bookParam.getName()) != null) {
	    	redirectAttributes.addAttribute("message", "Tên sách đã tồn tại trong cơ sở dữ liệu.");
	        return "redirect:/addbook";
	    }
	    
	    Book newBook = new Book();
	    
	    //Cập nhật thông tin cho cuốn sách mới
	    newBook.setName(bookParam.getName());
	    newBook.setCostPrice(bookParam.getCostPrice());
	    newBook.setSellPrice(bookParam.getSellPrice());
	    Category category = categoryService.getCategoryById(categoryId);
	    newBook.setCategory(category);
	    newBook.setPublisher(bookParam.getPublisher());
	    newBook.setDescription(bookParam.getDescription());
	    newBook.setStatus(bookParam.getStatus());
	    newBook.setDetail(bookParam.getDetail());
	    newBook.setQuantity(bookParam.getQuantity());
	    
	    // Kiểm tra trường hợp số lượng bằng 0 sẽ đưa về ngừng kinh doanh
        if (newBook.getQuantity() == 0) {
        	newBook.setStatus(0);
        }
        
        // Lưu sách trong cơ sở dữ liệu
        bookService.addBook(newBook);
        
        // Lấy cuốn sách vừa được lưu để có id
        newBook = bookService.getLastBook();
        
        // Tạo 4 BookImage mới để chuẩn bị chứa ảnh mới
        BookImage bookImage1 = new BookImage(newBook, "1", 1, "url1");
        BookImage bookImage2 = new BookImage(newBook, "2", 2, "url2");
        BookImage bookImage3 = new BookImage(newBook, "3", 3, "url3");
        BookImage bookImage4 = new BookImage(newBook, "4", 4, "url4");
        
        //Lưu các BookImage và database
        bookImageService.addBookImage(bookImage1);
        bookImageService.addBookImage(bookImage2);
        bookImageService.addBookImage(bookImage3);
        bookImageService.addBookImage(bookImage4);
        
        List<BookImage> bookImages = new ArrayList<>();
        bookImages.add(bookImage1); // Không cần truy xuất ID mỗi bookImage từ database
        bookImages.add(bookImage2); // vì khi đã addBookImage
        bookImages.add(bookImage3); // thì Hibernate sẽ tự động tạo một ID cho đối tượng này.
        bookImages.add(bookImage4); // ID này sau đó sẽ được Hibernate sử dụng để theo dõi và quản lý đối tượng trong phiên làm việc hiện tại
        
        boolean updatedImages = updateImages(newBook, bookImages, image1, image2, image3, image4, true);
	    // Nếu cập nhật ảnh bị lỗi thì
	    if(updatedImages == false) {
	    	redirectAttributes.addAttribute("message", "Đã xảy ra lỗi khi cập nhật ảnh cho sách.");
	        return "redirect:/addbook";
	    }
        
        // Phần cập nhật tác giả cho cuốn sách
	    List<Author> newAuthors = new ArrayList<>();
	    
	    for (Integer authorId : authorIds) {
	        Author author = authorService.getAuthorById(authorId);
	        if (author != null) {
	            newAuthors.add(author);
	        }
	    }
	    
	    // Tạo các bản ghi trong bảng BookAuthor cho tác giả mới
	    for (Author author : newAuthors) {
	        BookAuthor bookAuthor = new BookAuthor();
	        bookAuthor.setBook(newBook);
	        bookAuthor.setAuthor(author);
	        bookAuthorService.addBookAuthor(bookAuthor);
	    }
        
        redirectAttributes.addAttribute("message", "Đã thêm sách thành công!");
        return "redirect:/addbook";
	}
	
	@GetMapping("/managedetailbook")
	public String manageDetailBook(@RequestParam("bookId") Integer bookId, 
			Model model,
			HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    // Truy xuất dữ liệu từ nguồn dữ liệu
	    Book book = bookService.getBookById(bookId);

		// Lấy danh sách các đợt nhập sách cho cuốn sách
		List<BookImport> bookImports = bookImportService.getBookImportsByBookOrderByImportDateDesc(book);
	    
	    List<BookAuthor> bookAuthors = bookAuthorService.getByBook(book);
	    String authors = "";
	    for (int i = 0; i < bookAuthors.size(); i++) {
	        authors += bookAuthors.get(i).getAuthor().getName();
	        if (i < bookAuthors.size() - 1) {
	            authors += ", ";
	        }
	    }

	    // Đặt thuộc tính vào model để sử dụng trong view
	    model.addAttribute("book", book);
	    model.addAttribute("authors", authors);
		model.addAttribute("bookImports", bookImports);

	    // Forward đến trang chi tiết sách
	    return "admin/managedetailbook";
	}
	
	@GetMapping("/updatebook")
	public String showUpdateBookForm(@RequestParam("bookId") Integer bookId,
			Model model,
			HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }
	    
	    //Lấy sách theo Id
	    Book book = bookService.getBookById(bookId);
	    
        // Lấy danh sách tác giả của cuốn sách
        List<BookAuthor> bookAuthors = bookAuthorService.getByBook(book);
        List<Author> authors = new ArrayList<>();

	     // Lặp qua danh sách bookAuthors và lấy ra tác giả tương ứng
	     for (BookAuthor bookAuthor : bookAuthors) {
	         authors.add(bookAuthor.getAuthor());
	     }
        
        // Lấy danh sách tất cả tác giả
        List<Author> allAuthors = authorService.getAllAuthors();
        
        // Lấy danh sách ảnh của cuốn sách
        
        
        // Đặt thuộc tính vào model để sử dụng trong view
        // Sinh giá trị ngẫu nhiên
        Random random = new Random();
        int randomNumber = random.nextInt();
        model.addAttribute("randomNumber", randomNumber);
	    model.addAttribute("book", book);
	    List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("authors", authors);
        model.addAttribute("allAuthors", allAuthors);
        
        return "admin/updatebook";
	}
	
	@PostMapping("/updatebook")
	public String updateBook(@ModelAttribute("book") Book bookParam,
            @RequestParam("image1") MultipartFile image1,
            @RequestParam("image2") MultipartFile image2,
            @RequestParam("image3") MultipartFile image3,
            @RequestParam("image4") MultipartFile image4,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("authorsIds") List<Integer> authorIds,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
		Account admin = (Account) session.getAttribute("admin");

	    // Kiểm tra xem người dùng đã đăng nhập hay chưa
	    if (admin == null) {
	        // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
	        return "redirect:/loginadmin";
	    }

		// Kiểm tra xem tên sách cập nhật lại có trùng với tên sách nào trong cơ sở dữ liệu không
		Book existingBook = bookService.getBookByName(bookParam.getName());
		if (existingBook != null && existingBook.getId() != bookParam.getId()) {
			redirectAttributes.addAttribute("message", "Tên sách đã tồn tại trong cơ sở dữ liệu.");
			redirectAttributes.addAttribute("bookId", bookParam.getId());
			return "redirect:/updatebook";
		}
	    
	    // Tạo sách để cập nhật 
	    Book updateBook = bookService.getBookById(bookParam.getId());
	    
	    
	    
	    //Cập nhật thông tin cho cuốn sách
	    updateBook.setName(bookParam.getName());
	    updateBook.setCostPrice(bookParam.getCostPrice());
	    updateBook.setSellPrice(bookParam.getSellPrice());
	    Category category = categoryService.getCategoryById(categoryId);
	    updateBook.setCategory(category);
	    updateBook.setPublisher(bookParam.getPublisher());
	    updateBook.setDescription(bookParam.getDescription());
	    updateBook.setStatus(bookParam.getStatus());
	    updateBook.setDetail(bookParam.getDetail());
	    updateBook.setQuantity(bookParam.getQuantity());
	   
	    
	    
	    // Kiểm tra trường hợp số lượng bằng 0 sẽ đưa về ngừng kinh doanh
        if (updateBook.getQuantity() == 0) {
        	updateBook.setStatus(0);
        }
        
        // Cập nhật sách trong cơ sở dữ liệu
        bookService.updateBook(updateBook);
        
        // Lấy danh sách BookImage liên quan đến cuốn sách
	    List<BookImage> bookImages = bookImageService.getByBook(updateBook);

	    boolean updatedImages = updateImages(updateBook, bookImages, image1, image2, image3, image4, true);
	    // Nếu cập nhật ảnh bị lỗi thì
	    if(updatedImages == false) {
	    	redirectAttributes.addAttribute("message", "Đã xảy ra lỗi khi cập nhật ảnh cho sách.");
	        return "redirect:/updatebook";
	    }
        
        // Phần cập nhật tác giả cho cuốn sách
	    List<Author> newAuthors = new ArrayList<>();
	    
	    for (Integer authorId : authorIds) {
	        Author author = authorService.getAuthorById(authorId);
	        if (author != null) {
	            newAuthors.add(author);
	        }
	    }
	    
	    // Xóa các bản ghi cũ trong bảng BookAuthor
	    bookAuthorService.deleteBookAuthorsByBook(updateBook);
	    
	    // Tạo các bản ghi mới trong bảng BookAuthor cho tác giả mới
	    for (Author author : newAuthors) {
	        BookAuthor bookAuthor = new BookAuthor();
	        bookAuthor.setBook(updateBook);
	        bookAuthor.setAuthor(author);
	        bookAuthorService.addBookAuthor(bookAuthor);
	    }
	    
        redirectAttributes.addAttribute("message", "Đã cập nhật sách thành công!");
        redirectAttributes.addAttribute("bookId", updateBook.getId());
        return "redirect:/updatebook";
	}
	
	private boolean updateImages(Book book, List<BookImage> bookImages, MultipartFile image1, MultipartFile image2,
			MultipartFile image3, MultipartFile image4,
			boolean success) {
		try {
	        // Tạo public ID cho hình ảnh trên Cloudinary (sử dụng id sách)
	        String publicId = "WebBookStoreKLTN/img_book/book_" + book.getId();
	        
	        // Tạo một danh sách các nhiệm vụ tải lên ảnh
	        List<Callable<String>> uploadTasks = new ArrayList<>();
	        
	        // Kiểm tra và tạo nhiệm vụ tải lên ảnh cho từng ảnh
	        if (!image1.isEmpty()) {
	            uploadTasks.add(() -> cloudinaryService.uploadImage(image1, publicId + "/1"));
	        } else {
	            uploadTasks.add(() -> cloudinaryService.uploadImageFromUrl(bookImages.get(0).getPath(), publicId + "/1"));
	        }
	        if (!image2.isEmpty()) {
	            uploadTasks.add(() -> cloudinaryService.uploadImage(image2, publicId + "/2"));
	        } else {
	            uploadTasks.add(() -> cloudinaryService.uploadImageFromUrl(bookImages.get(1).getPath(), publicId + "/2"));
	        }
	        if (!image3.isEmpty()) {
	            uploadTasks.add(() -> cloudinaryService.uploadImage(image3, publicId + "/3"));
	        } else {
	            uploadTasks.add(() -> cloudinaryService.uploadImageFromUrl(bookImages.get(2).getPath(), publicId + "/3"));
	        }
	        if (!image4.isEmpty()) {
	            uploadTasks.add(() -> cloudinaryService.uploadImage(image4, publicId + "/4"));
	        } else {
	            uploadTasks.add(() -> cloudinaryService.uploadImageFromUrl(bookImages.get(3).getPath(), publicId + "/4"));
	        }
	        
	        // Tạo một ExecutorService để thực hiện các nhiệm vụ tải lên ảnh song song
	        ExecutorService executorService = Executors.newFixedThreadPool(uploadTasks.size());
	        
	        // Thực hiện các nhiệm vụ tải lên ảnh và lấy URL của ảnh đã tải lên
	        List<Future<String>> uploadResults = executorService.invokeAll(uploadTasks);
	        int i = 0;
	        for (Future<String> uploadResult : uploadResults) {
	            String imageUrl = uploadResult.get();
	            // Cập nhật URL hình ảnh vào BookImage 
	            bookImages.get(i).setPath(imageUrl);
	            // Lưu vào cơ sở dữ liệu
	            bookImageService.updateBookImage(bookImages.get(i));
	            i++;
	        }
	        
	        // Đóng ExecutorService sau khi hoàn thành
	        executorService.shutdown();
	        return success;
	    } catch (Exception e) {
	        e.printStackTrace();
	        success = false;
	        return success;
	    }
	}

	private BookResponseModel convertToBookResponseModel(Book book) {
		BookResponseModel bookResponseModel = modelMapper.map(book, BookResponseModel.class);

		bookResponseModel.setCurrentDiscount(null);
		// Lấy đợt giảm giá còn hiệu lực theo sách
		Discount latestActiveDiscount = discountService.getLatestActiveDiscountByBookId(book.getId());

		if (latestActiveDiscount != null)
		{
			// Gán cho Response
			DiscountLeanModel discountLeanModel = modelMapper.map(latestActiveDiscount, DiscountLeanModel.class);
			bookResponseModel.setCurrentDiscount(discountLeanModel);
		}

		return bookResponseModel;
	}

}
