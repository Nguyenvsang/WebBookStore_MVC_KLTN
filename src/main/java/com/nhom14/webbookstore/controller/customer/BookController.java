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
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/home1")
    public String viewHome1(
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

    @GetMapping("/home")
    public String viewHome(Model model, @PageableDefault(size = 10) Pageable pageable) {
        // Lấy ra 10 cuốn sách có giảm giá
        Page<Book> discountedBooksPage = bookService.getDiscountedBooksPage(pageable);
        Page<BookResponseModel> discountedBooksResponseModels = discountedBooksPage.map(this::convertToBookResponseModel);
        model.addAttribute("discountedBooks", discountedBooksResponseModels);

        // Sách từ danh mục có ID là 1
        Page<Book> booksFromCategory1Page = bookService.getBooksByCategoryPage(1, pageable);
        Page<BookResponseModel> booksFromCategory1ResponseModels = booksFromCategory1Page.map(this::convertToBookResponseModel);
        model.addAttribute("booksFromCategory1", booksFromCategory1ResponseModels);

        // Sách từ danh mục có ID là 2
        Page<Book> booksFromCategory2Page = bookService.getBooksByCategoryPage(2, pageable);
        Page<BookResponseModel> booksFromCategory2ResponseModels = booksFromCategory2Page.map(this::convertToBookResponseModel);
        model.addAttribute("booksFromCategory2", booksFromCategory2ResponseModels);

        // Sách từ danh mục có ID là 3
        Page<Book> booksFromCategory3Page = bookService.getBooksByCategoryPage(3, pageable);
        Page<BookResponseModel> booksFromCategory3ResponseModels = booksFromCategory3Page.map(this::convertToBookResponseModel);
        model.addAttribute("booksFromCategory3", booksFromCategory3ResponseModels);

        // Sách từ danh mục có ID là 4
        Page<Book> booksFromCategory4Page = bookService.getBooksByCategoryPage(4, pageable);
        Page<BookResponseModel> booksFromCategory4ResponseModels = booksFromCategory4Page.map(this::convertToBookResponseModel);
        model.addAttribute("booksFromCategory4", booksFromCategory4ResponseModels);

        // Sách từ danh mục có ID là 5
        Page<Book> booksFromCategory5Page = bookService.getBooksByCategoryPage(5, pageable);
        Page<BookResponseModel> booksFromCategory5ResponseModels = booksFromCategory5Page.map(this::convertToBookResponseModel);
        model.addAttribute("booksFromCategory5", booksFromCategory5ResponseModels);

        // Sách từ danh mục có ID là 6
        Page<Book> booksFromCategory6Page = bookService.getBooksByCategoryPage(6, pageable);
        Page<BookResponseModel> booksFromCategory6ResponseModels = booksFromCategory6Page.map(this::convertToBookResponseModel);
        model.addAttribute("booksFromCategory6", booksFromCategory6ResponseModels);

        // Sách từ NXB Kim Đồng
        Page<Book> booksByPublisherKimDongPage = bookService.getBooksPublisherPage("NXB Kim Đồng", pageable);
        Page<BookResponseModel> booksByPublisherKimDongResponseModels = booksByPublisherKimDongPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherKimDong", booksByPublisherKimDongResponseModels);

        // Sách từ NXB Lao Động
        Page<Book> booksByPublisherLaoDongPage = bookService.getBooksPublisherPage("NXB Lao Động", pageable);
        Page<BookResponseModel> booksByPublisherLaoDongResponseModels = booksByPublisherLaoDongPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherLaoDong", booksByPublisherLaoDongResponseModels);

        // Sách từ NXB Thế giới
        Page<Book> booksByPublisherTheGioiPage = bookService.getBooksPublisherPage("NXB Thế Giới", pageable);
        Page<BookResponseModel> booksByPublisherTheGioiResponseModels = booksByPublisherTheGioiPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherTheGioi", booksByPublisherTheGioiResponseModels);

        // Sách từ NXB Trẻ
        Page<Book> booksByPublisherTrePage = bookService.getBooksPublisherPage("NXB Trẻ", pageable);
        Page<BookResponseModel> booksByPublisherTreResponseModels = booksByPublisherTrePage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherTre", booksByPublisherTreResponseModels);

        // Sách từ NXB Thanh Niên
        Page<Book> booksByPublisherThanhNienPage = bookService.getBooksPublisherPage("NXB Thanh Niên", pageable);
        Page<BookResponseModel> booksByPublisherThanhNienResponseModels = booksByPublisherThanhNienPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherThanhNien", booksByPublisherThanhNienResponseModels);

        // Sách từ NXB Hồng Đức
        Page<Book> booksByPublisherHongDucPage = bookService.getBooksPublisherPage("NXB Hồng Đức", pageable);
        Page<BookResponseModel> booksByPublisherHongDucResponseModels = booksByPublisherHongDucPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherHongDuc", booksByPublisherHongDucResponseModels);

        // Sách từ NXB Chính Trị Quốc Gia
        Page<Book> booksByPublisherChinhTriQuocGiaPage = bookService.getBooksPublisherPage("NXB Chính Trị Quốc Gia", pageable);
        Page<BookResponseModel> booksByPublisherChinhTriQuocGiaResponseModels = booksByPublisherChinhTriQuocGiaPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherChinhTriQuocGia", booksByPublisherChinhTriQuocGiaResponseModels);

        // Sách từ NXB Văn Học
        Page<Book> booksByPublisherVanHocPage = bookService.getBooksPublisherPage("NXB Văn Học", pageable);
        Page<BookResponseModel> booksByPublisherVanHocResponseModels = booksByPublisherVanHocPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherVanHoc", booksByPublisherVanHocResponseModels);

        // Sách từ NXB Hội Nhà Văn
        Page<Book> booksByPublisherHoiNhaVanPage = bookService.getBooksPublisherPage("NXB Hội Nhà Văn", pageable);
        Page<BookResponseModel> booksByPublisherHoiNhaVanResponseModels = booksByPublisherHoiNhaVanPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherHoiNhaVan", booksByPublisherHoiNhaVanResponseModels);

        // Sách từ NXB Dân Trí
        Page<Book> booksByPublisherDanTriPage = bookService.getBooksPublisherPage("NXB Dân Trí", pageable);
        Page<BookResponseModel> booksByPublisherDanTriResponseModels = booksByPublisherDanTriPage.map(this::convertToBookResponseModel);
        model.addAttribute("booksByPublisherDanTri", booksByPublisherDanTriResponseModels);

        // Lấy top 10 sách bán chạy trong một tháng trở lại
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);
        Page<Book> topSellingBooksPage = bookService.getTopSellingBooks(startDate, endDate, pageable);
        Page<BookResponseModel> topSellingBooksResponseModels = topSellingBooksPage.map(this::convertToBookResponseModel);
        model.addAttribute("topSellingBooks", topSellingBooksResponseModels);

        // Lấy top 10 sách nổi bật (có đánh giá cao nhất)
        Page<Book> highlightedBooksPage = bookService.getHighlightedBooks(pageable);
        Page<BookResponseModel> highlightedBooksResponseModels = highlightedBooksPage.map(this::convertToBookResponseModel);
        model.addAttribute("highlightedBooks", highlightedBooksResponseModels);

        // Lấy top 10 sách mới nhất (mới được nhập trong 1 tháng gần đây)
        Page<Book> recentlyImportedBooksPage = bookService.getRecentlyImportedBooks(pageable);
        Page<BookResponseModel> recentlyImportedBooksResponseModels = recentlyImportedBooksPage.map(this::convertToBookResponseModel);
        model.addAttribute("recentlyImportedBooks", recentlyImportedBooksResponseModels);

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
        if(bookReviews == null || bookReviews.isEmpty()) {
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

        if (bookReviews != null) {
            // Tính tổng số đánh giá
            long totalReviews = bookReviews.size();
            // Tính số lượng đánh giá cho mỗi sao
            long star1Count = bookReviews.stream().filter(review -> review.getRating() == 1).count();
            long star2Count = bookReviews.stream().filter(review -> review.getRating() == 2).count();
            long star3Count = bookReviews.stream().filter(review -> review.getRating() == 3).count();
            long star4Count = bookReviews.stream().filter(review -> review.getRating() == 4).count();
            long star5Count = bookReviews.stream().filter(review -> review.getRating() == 5).count();
            // Tính tỷ lệ phần trăm cho mỗi sao
            double star1Percentage = (totalReviews > 0) ? (double) star1Count / totalReviews * 100 : 0;
            double star2Percentage = (totalReviews > 0) ? (double) star2Count / totalReviews * 100 : 0;
            double star3Percentage = (totalReviews > 0) ? (double) star3Count / totalReviews * 100 : 0;
            double star4Percentage = (totalReviews > 0) ? (double) star4Count / totalReviews * 100 : 0;
            double star5Percentage = (totalReviews > 0) ? (double) star5Count / totalReviews * 100 : 0;

            model.addAttribute("totalReviews", totalReviews);
            model.addAttribute("star1Count", star1Count);
            model.addAttribute("star2Count", star2Count);
            model.addAttribute("star3Count", star3Count);
            model.addAttribute("star4Count", star4Count);
            model.addAttribute("star5Count", star5Count);
            model.addAttribute("star1Percentage", star1Percentage);
            model.addAttribute("star2Percentage", star2Percentage);
            model.addAttribute("star3Percentage", star3Percentage);
            model.addAttribute("star4Percentage", star4Percentage);
            model.addAttribute("star5Percentage", star5Percentage);
        }


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
        // và có chứa cụm "/viewbooks" hoặc "/viewcart" hoặc "/home" hoặc "/viewnotifications" (tránh trường hợp vượt quá kiểm soát)
        if (referer != null && !referer.equals(currentUrl) && (referer.contains("/viewbooks") || referer.contains("/viewcart") || referer.contains("/home") || referer.contains("/viewnotifications"))) {
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
