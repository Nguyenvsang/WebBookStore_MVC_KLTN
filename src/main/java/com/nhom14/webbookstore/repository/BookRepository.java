package com.nhom14.webbookstore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
	Page<Book> findWithFilters(@Param("accountId") Integer accountId,
							   @Param("categoryId") Integer categoryId,
							   @Param("searchKeyword") String searchKeyword,
							   @Param("priceMin") Double priceMin,
							   @Param("priceMax") Double priceMax,
							   @Param("publisher") String publisher,
							   Pageable pageable);
}
