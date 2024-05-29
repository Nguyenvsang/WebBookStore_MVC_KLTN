package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.model.lean_model.DiscountLeanModel;
import com.nhom14.webbookstore.model.response_model.BookResponseModel;
import com.nhom14.webbookstore.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.modelmapper.ModelMapper;

import java.util.*;

@Controller
public class BookController {
	private BookService bookService;
	private CategoryService categoryService;
    private BookReviewService bookReviewService;
    private BookReviewLikeService bookReviewLikeService;
    private FavoriteBookService favoriteBookService;
    private ModelMapper modelMapper;
    private DiscountService discountService;

	@Autowired
	public BookController(BookService bookService, CategoryService categoryService,
                          BookReviewService bookReviewService, BookReviewLikeService bookReviewLikeService, FavoriteBookService favoriteBookService, ModelMapper modelMapper, DiscountService discountService) {
		super();
		this.bookService = bookService;
		this.categoryService = categoryService;
        this.bookReviewService = bookReviewService;
        this.bookReviewLikeService = bookReviewLikeService;
        this.favoriteBookService = favoriteBookService;
        this.modelMapper = modelMapper;
        this.discountService = discountService;
    }

    @GetMapping("/home")
    public String viewHome(
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
        Sort sort = handleSortOption(sortOption);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        // Gọi phương thức getFilteredBooks với các tham số tìm kiếm và lọc, còn kinh doanh
        Page<Book> books = bookService.getFilteredActiveBooks(categoryId, searchKeyword, priceMin, priceMax, publisher, pageable);

        Page<BookResponseModel> bookResponseModels = books.map(this::convertToBookResponseModel);

        model.addAttribute("books", bookResponseModels);
        List<Category> categories = categoryService.getActiveCategories();
        model.addAttribute("categories", categories);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
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


        return "customer/home";
    }

    @GetMapping("/viewbooks")
    public String viewBooks(
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
        Sort sort = handleSortOption(sortOption);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        // Gọi phương thức getFilteredBooks với các tham số tìm kiếm và lọc, còn kinh doanh
        Page<Book> books = bookService.getFilteredActiveBooks(categoryId, searchKeyword, priceMin, priceMax, publisher, pageable);

        Page<BookResponseModel> bookResponseModels = books.map(this::convertToBookResponseModel);

        model.addAttribute("books", bookResponseModels);
        List<Category> categories = categoryService.getActiveCategories();
        model.addAttribute("categories", categories);

        // Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
        Map<String, Object> params = new HashMap<>();
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


        return "customer/viewbooks";
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

	@GetMapping("/detailbook/{id}")
	public String viewDetailBook(@PathVariable Integer id,
                                 Model model,
                                 HttpServletRequest request, //Dùng cho nút Quay lại
                                 HttpSession session) {
		// Lấy thông tin về cuốn sách từ id
	    Book book = bookService.getBookById(id);

        // Nếu mà sách ngừng kinh doanh sẽ có thông báo và ẩn nút tương tác

	    // Lấy danh sách các danh mục
	    List<Category> categories = categoryService.getActiveCategories();

        // Lấy danh sách tất cả các đánh giá theo sách đã được cho phép đăng
        List<BookReview> bookReviews = bookReviewService.getPublishedReviewsByBook(book);
        if(bookReviews.isEmpty()) {
            bookReviews = null;
        }

        // Nếu người dùng đang đăng nhập thì sẽ hiện đánh giá của họ ở nơi riêng
        Account account = (Account) session.getAttribute("account");

        BookReview loggedInUserReview = null;
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account != null) {
            // Tìm kiếm đánh giá của người dùng đã đăng nhập cho cuốn sách này
            loggedInUserReview = bookReviewService.getReviewByAccountAndBook(account, book);
        }

        // Tìm những đánh giá mà người dùng này đã thích
        List<BookReview> likedReviews = new ArrayList<>();
        if (account != null) {
            likedReviews = bookReviewLikeService.getLikedReviewsByAccount(account);
        }
        model.addAttribute("likedReviews", likedReviews);

        // Xem người dùng có thích cuốn sách này chưa
        boolean isFavorite = false;
        FavoriteBook favoriteBook = favoriteBookService.findByAccountAndBook(account, book);
        if (favoriteBook != null){
            isFavorite = true;
        }
        model.addAttribute("isFavorite", isFavorite);

        BookResponseModel bookResponseModels = convertToBookResponseModel(book);

        // Sinh giá trị ngẫu nhiên
        Random random = new Random();
        int randomNumber = random.nextInt();
        model.addAttribute("randomNumber", randomNumber);

	    // Đặt thuộc tính vào model để sử dụng trong View
	    model.addAttribute("book", bookResponseModels);
	    model.addAttribute("categories", categories);
        model.addAttribute("bookReviews", bookReviews);
        model.addAttribute("loggedInUserReview", loggedInUserReview);
        // Lưu URL trang trước đó vào session
        String referer = request.getHeader("Referer");
        String currentUrl = request.getRequestURL().toString();

        // Kiểm tra xem referer có khác với URL hiện tại hay không (tránh trường hợp 1 trang lặp lại)
        // và có chứa cụm "/viewbooks" hoặc "/viewcart" hoặc "/home" (tránh trường hợp vượt quá kiểm soát)
        if (referer != null && !referer.equals(currentUrl) && (referer.contains("/viewbooks") || referer.contains("/viewcart") || referer.contains("/home"))) {
            session.setAttribute("previousUrl", referer);
        }

	    return "customer/detailbook";
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
