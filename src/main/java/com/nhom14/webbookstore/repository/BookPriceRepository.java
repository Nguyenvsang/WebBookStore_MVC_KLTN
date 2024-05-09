package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;
import com.nhom14.webbookstore.entity.BookPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookPriceRepository extends JpaRepository<BookPrice, Long> {
    @Query("SELECT bp FROM BookPrice bp WHERE " +
            "(:bookId is null or bp.book.id = :bookId) and " +
            "(:searchKeyword is null or lower(bp.book.name) like lower(concat('%', :searchKeyword,'%')))")
    Page<BookPrice> findWithFilters(@Param("bookId") Integer bookId,
                                    @Param("searchKeyword") String searchKeyword,
                                    Pageable pageable);

    @Query("SELECT bp FROM BookPrice bp WHERE " +
            "bp.book = :book and " +
            "bp.effectiveDate <= :now " +
            "ORDER BY bp.effectiveDate DESC")
    List<BookPrice> findByBookAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(@Param("book") Book book, @Param("now") LocalDateTime now, Pageable pageable);

}
