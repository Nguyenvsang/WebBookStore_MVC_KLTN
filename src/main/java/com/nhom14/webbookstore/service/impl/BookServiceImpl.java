package com.nhom14.webbookstore.service.impl;

import java.text.Collator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookAuthor;
import com.nhom14.webbookstore.entity.Category;
import com.nhom14.webbookstore.repository.BookAuthorRepository;
import com.nhom14.webbookstore.repository.BookRepository;
import com.nhom14.webbookstore.repository.CategoryRepository;
import com.nhom14.webbookstore.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository bookRepository;
	private CategoryRepository categoryRepository;
	private BookAuthorRepository bookAuthorRepository;
	
	@Autowired
	public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository, BookAuthorRepository bookAuthorRepository) {
		super();
		this.bookRepository = bookRepository;
		this.categoryRepository = categoryRepository;
		this.bookAuthorRepository = bookAuthorRepository;
	}

	@Override
	public List<Book> getActiveBooks() {
		return bookRepository.findByStatus(1);
	}

	@Override
	public List<Book> getActiveBooksByCategory(int categoryId) {
		Category category = categoryRepository.findById(categoryId).orElse(null);
	    if (category != null) {
	        return bookRepository.findByCategoryAndStatus(category, 1);
	    }
	    return Collections.emptyList();
	}

	@Override
	public List<Book> searchBooksByKeyword(List<Book> books, String searchKeyword) {
		List<Book> result = new ArrayList<>();
	    Set<Integer> addedBookIds = new HashSet<>();
	    String lowercaseKeyword = searchKeyword.toLowerCase().trim();

	    for (Book book : books) {
	        List<BookAuthor> bookAuthors = bookAuthorRepository.findByBook(book);
	        boolean isAuthorMatched = false;

	        for (BookAuthor bookAuthor : bookAuthors) {
	            String authorName = bookAuthor.getAuthor().getName();
	            if (containsIgnoreCase(authorName, searchKeyword)) {
	                isAuthorMatched = true;
	                break;
	            }
	        }

	        if (isAuthorMatched
	        		|| containsIgnoreCase(Integer.toString(book.getId()), lowercaseKeyword)
	                || containsIgnoreCase(book.getName(), lowercaseKeyword)
	                || containsIgnoreCase(book.getDetail(), lowercaseKeyword)
	                || containsIgnoreCase(book.getPublisher(), lowercaseKeyword)
	                || containsIgnoreCase(Double.toString(book.getSellPrice()), lowercaseKeyword)
	                || (book.getCategory() != null && containsIgnoreCase(book.getCategory().getName(), lowercaseKeyword))) {
	            if (!addedBookIds.contains(book.getId())) {
	                result.add(book);
	                addedBookIds.add(book.getId());
	            }
	        }
	    }

	    return result;
	}

	// Kiểm tra xem một chuỗi có chứa một chuỗi con cụ thể hay không, 
	// mà không phân biệt chữ hoa chữ thường trong quá trình so sánh
	private boolean containsIgnoreCase(String text, String keyword) {
		return text.toLowerCase().contains(keyword);
	}

	@Override
	public Book getActiveBookById(int id) {
		try {
	        return bookRepository.findActiveBookById(id);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null; // Trả về null nếu không tìm thấy cuốn sách
	}

	@Override
	public Book getBookById(int id) {
		Optional<Book> book = bookRepository.findById(id);
	    return book.orElse(null);
	}

	@Override
	public void updateBook(Book book) {
		bookRepository.save(book);
		
	}

	@Override
	public List<Book> getAllBooks() {
		return bookRepository.findAll(); // Nếu không có sách sẽ trả về empty list
	}

	@Override
	public List<Book> getBooksByCategory(Integer categoryId) {
		Category category = categoryRepository.findById(categoryId).orElse(null);
	    if (category != null) {
	        return bookRepository.findByCategory(category);
	    }
	    return Collections.emptyList();
	}

	@Override
	public void addBook(Book newBook) {
		bookRepository.save(newBook);
	}

	@Override
	public Book getLastBook() {
		return bookRepository.findFirstByOrderByIdDesc();// trả null nếu không tìm thấy
	}

	@Override
	public List<Book> filterBooksByPriceRange(List<Book> books, Double priceMin, Double priceMax) {
	    List<Book> result = new ArrayList<>();

	    for (Book book : books) {
	        if (book.getSellPrice() >= priceMin && book.getSellPrice() <= priceMax) {
	            result.add(book);
	        }
	    }

	    return result;
	}

	@Override
	public List<Book> sortBooksByPriceAscending(List<Book> books) {
		List<Book> sortedBooks = new ArrayList<>(books);
	    sortedBooks.sort(Comparator.comparingDouble(Book::getSellPrice));
	    return sortedBooks;
	}

	@Override
	public List<Book> sortBooksByPriceDescending(List<Book> books) {
		List<Book> sortedBooks = new ArrayList<>(books);
	    sortedBooks.sort(Comparator.comparingDouble(Book::getSellPrice).reversed());
	    return sortedBooks;
	}

	@Override
	public List<Book> sortBooksByNameAscending(List<Book> books) {
		List<Book> sortedBooks = new ArrayList<>(books);
	    Collator collator = Collator.getInstance(new Locale("vi", "VN"));
	    sortedBooks.sort(Comparator.comparing(Book::getName, collator));
	    return sortedBooks;
	}

	@Override
	public List<Book> sortBooksByNameDescending(List<Book> books) {
		List<Book> sortedBooks = new ArrayList<>(books);
	    Collator collator = Collator.getInstance(new Locale("vi", "VN"));
	    sortedBooks.sort(Comparator.comparing(Book::getName, collator).reversed());
	    return sortedBooks;
	}

	@Override
	public List<Book> filterBooksByPublisher(List<Book> books, String string) {
		List<Book> filteredBooks = new ArrayList<>();
		String lowercaseKeyword = string.toLowerCase().trim();
	    for (Book book : books) {
	        if (containsIgnoreCase(book.getPublisher().trim(), lowercaseKeyword)) {
	            filteredBooks.add(book);
	        }
	    }
	    return filteredBooks;
	}

	@Override
	public Book getBookByName(String name) {
		return bookRepository.findByName(name);
	}

	@Override
	public List<Book> getBooksByStatus(int status) {
		return bookRepository.findByStatus(status);
	}

	@Override
	public Page<Book> getFilteredFavoriteBooks(Integer accountId, Integer categoryId, String searchKeyword, Double priceMin, Double priceMax, String publisher, Pageable pageable) {
		return bookRepository.findFavoriteBooksWithFilters(accountId, categoryId, searchKeyword, priceMin, priceMax, publisher, pageable);
		// Không tìm thấy sẽ trả về một đối tượng Page<Book> rỗng
		// nghĩa là .getTotalElements() = 0
	}

	@Override
	public Page<Book> getFilteredActiveBooks(Integer categoryId, String searchKeyword, Double priceMin, Double priceMax, String publisher, Pageable pageable) {
		// Nếu searchKeyword không null, tìm kiếm theo tên tác giả
		if (searchKeyword != null && !searchKeyword.isEmpty()) {
			Page<Book> books = bookRepository.findByAuthorName(searchKeyword, pageable);
			if (books.hasContent()) {
				return books;
			}
		}

		// Nếu searchKeyword null hoặc không tìm thấy sách theo tên tác giả, tìm kiếm theo các bộ lọc khác
		return bookRepository.findActiveBooksWithFilters(categoryId, searchKeyword, priceMin, priceMax, publisher, pageable);
	}

	@Override
	public Page<Book> getFilteredBooks(Integer status, Integer categoryId, String searchKeyword, Double priceMin, Double priceMax, String publisher, Pageable pageable) {
		// Chuyển để tìm kiếm theo mã sách
		Integer searchKeywordAsInteger = null;
		try {
			searchKeywordAsInteger = Integer.parseInt(searchKeyword);
		} catch (NumberFormatException e) {
			// searchKeyword không phải là một số nguyên, không làm gì cả
		}
		if (searchKeywordAsInteger != null) {
			return bookRepository.findWithFilters(status, categoryId, null, searchKeywordAsInteger, priceMin, priceMax, publisher, pageable);
		} else {
			return bookRepository.findWithFilters(status, categoryId, searchKeyword, null, priceMin, priceMax, publisher, pageable);
		}
		// Không tìm thấy sẽ trả về một đối tượng Page<Book> rỗng
		// nghĩa là .getTotalElements() = 0
	}

	@Override
	public Page<Book> getActiveDiscountedBooksPage(Pageable pageable) {
		return bookRepository.findActiveDiscountedBooks(LocalDateTime.now(), pageable);
	}

	@Override
	public Page<Book> getActiveBooksByCategoryPage(Integer categoryId, Pageable pageable) {
		return bookRepository.findActiveBooksWithFilters(categoryId, null, null, null, null, pageable);
	}

	@Override
	public Page<Book> getActiveBooksPublisherPage(String publisher, Pageable pageable) {
		// Sử dụng phương thức findActiveBooksWithFilters đã có trong BookRepository
		// và truyền null cho các tham số không sử dụng
		return bookRepository.findActiveBooksWithFilters(null, null, null, null, publisher, pageable);
	}

	@Override
	public Page<Book> getActiveTopSellingBooks(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		// Chuyển đổi kết quả từ Object[] sang Book và trả về Page<Book>
		return bookRepository.findActiveTopSellingBooks(startDate, endDate, pageable)
				.map(result -> (Book) result[0]);
	}

	@Override
	public Page<Book> getActiveHighlightedBooks(Pageable pageable) {
		return bookRepository.findActiveHighlightedBooks(pageable)
				.map(result -> (Book) result[0]);
	}

	@Override
	public Page<Book> getActiveRecentlyImportedBooks(Pageable pageable) {
		LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
		return bookRepository.findActiveRecentlyImportedBooks(oneMonthAgo, pageable);
	}
}
