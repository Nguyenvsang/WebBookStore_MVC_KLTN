package com.nhom14.webbookstore.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.Category;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

	List<Book> findByStatus(int status);

	List<Book> findByCategoryAndStatus(Category category, int status);

	Book findByIdAndStatus(int id, int status);

	List<Book> findByCategory(Category category);

	Book findFirstByOrderByIdDesc();

	Book findByName(String name);

	@Query("SELECT fb.book FROM FavoriteBook fb WHERE " +
			"(fb.account.id = :accountId) and " +
			"(:categoryId is null or fb.book.category.id = :categoryId) and " +
			"(:searchKeyword is null or lower(fb.book.name) like lower(concat('%', :searchKeyword,'%')) or lower(fb.book.detail) like lower(concat('%', :searchKeyword,'%')) or lower(fb.book.publisher) like lower(concat('%', :searchKeyword,'%')) or (fb.book.category != null and lower(fb.book.category.name) like lower(concat('%', :searchKeyword,'%')))) and " +
			"(:priceMin is null or fb.book.sellPrice >= :priceMin) and " +
			"(:priceMax is null or fb.book.sellPrice <= :priceMax) and " +
			"(:publisher is null or lower(fb.book.publisher) like lower(concat('%', :publisher,'%')))")
	Page<Book> findFavoriteBooksWithFilters(@Param("accountId") Integer accountId,
											@Param("categoryId") Integer categoryId,
											@Param("searchKeyword") String searchKeyword,
											@Param("priceMin") Double priceMin,
											@Param("priceMax") Double priceMax,
											@Param("publisher") String publisher,
											Pageable pageable);

	@Query("SELECT b FROM Book b JOIN b.bookAuthors ba JOIN ba.author a WHERE " +
			"LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
	Page<Book> findByAuthorName(@Param("authorName") String authorName, Pageable pageable);

	// Sách còn kinh doanh chỉ khi book.status =1 và book.category.status =1
	@Query("SELECT b FROM Book b " +
			"WHERE b.status = 1 and b.category.status = 1 and " +
			"(:categoryId is null or b.category.id = :categoryId) and " +
			"(:searchKeyword is null or " +
			"lower(b.name) like lower(concat('%', :searchKeyword, '%')) or " +
			"lower(b.publisher) like lower(concat('%', :searchKeyword, '%'))) and " +
			"(:priceMin is null or b.sellPrice >= :priceMin) and " +
			"(:priceMax is null or b.sellPrice <= :priceMax) and " +
			"(:publisher is null or lower(b.publisher) like lower(concat('%', :publisher, '%')))")
	Page<Book> findActiveBooksWithFilters(@Param("categoryId") Integer categoryId,
										  @Param("searchKeyword") String searchKeyword,
										  @Param("priceMin") Double priceMin,
										  @Param("priceMax") Double priceMax,
										  @Param("publisher") String publisher,
										  Pageable pageable);


	@Query("SELECT b FROM Book b WHERE " +
			"(:status is null or b.status = :status) and " +
			"(:searchKeywordAsInteger is null or b.id = :searchKeywordAsInteger) and " +
			"(:categoryId is null or b.category.id = :categoryId) and " +
			"(:searchKeyword is null or lower(b.name) like lower(concat('%', :searchKeyword,'%')) or lower(b.publisher) like lower(concat('%', :searchKeyword,'%'))) and " +
			"(:priceMin is null or b.sellPrice >= :priceMin) and " +
			"(:priceMax is null or b.sellPrice <= :priceMax) and " +
			"(:publisher is null or lower(b.publisher) like lower(concat('%', :publisher,'%')))")
	Page<Book> findWithFilters(@Param("status") Integer status,
										  @Param("categoryId") Integer categoryId,
										  @Param("searchKeyword") String searchKeyword,
										  @Param("searchKeywordAsInteger") Integer searchKeywordAsInteger,
										  @Param("priceMin") Double priceMin,
										  @Param("priceMax") Double priceMax,
										  @Param("publisher") String publisher,
										  Pageable pageable);

	@Query("SELECT b FROM Book b JOIN b.discounts d WHERE b.status = 1 AND b.category.status = 1 AND d.endDate > :now")
	Page<Book> findActiveDiscountedBooks(@Param("now") LocalDateTime now, Pageable pageable);

	@Query("SELECT oi.book, SUM(oi.quantity) as totalQuantity FROM OrderItem oi " +
			"JOIN oi.book b JOIN b.category c WHERE oi.order.isCompleted = 1 AND oi.order.dateOrder BETWEEN :startDate AND :endDate " +
			"AND b.status = 1 AND c.status = 1 GROUP BY oi.book ORDER BY totalQuantity DESC")
	Page<Object[]> findActiveTopSellingBooks(@Param("startDate") LocalDateTime startDate,
											 @Param("endDate") LocalDateTime endDate,
											 Pageable pageable);

	@Query("SELECT br.book, AVG(br.rating) as averageRating FROM BookReview br " +
			"JOIN br.book b JOIN b.category c WHERE br.isPublished = true AND b.status = 1 AND c.status = 1 " +
			"GROUP BY br.book ORDER BY averageRating DESC")
	Page<Object[]> findActiveHighlightedBooks(Pageable pageable);

	@Query("SELECT bi.book FROM BookImport bi " +
			"JOIN bi.book b JOIN b.category c WHERE bi.importDate > :oneMonthAgo AND bi.status = 1 AND b.status = 1 AND c.status = 1 " +
			"ORDER BY bi.importDate DESC")
	Page<Book> findActiveRecentlyImportedBooks(@Param("oneMonthAgo") LocalDateTime oneMonthAgo, Pageable pageable);


	@Query("SELECT b FROM Book b WHERE b.id = :id AND b.status = 1 AND b.category.status = 1")
	Book findActiveBookById(@Param("id") int id);

	@Query("SELECT b FROM Book b WHERE b.status = 1 and b.category.status = 1 ORDER BY RAND()")
	Page<Book> findAllActiveBooksRandom(Pageable pageable);

	List<Book> findByIdIn(List<String> ids);
}
