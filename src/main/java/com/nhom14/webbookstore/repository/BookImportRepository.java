package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookImportRepository extends JpaRepository<BookImport, Long> {
    List<BookImport> findByBookOrderByImportDateAsc(Book book);

    BookImport findTopByBookOrderByImportDateDesc(Book book);

    BookImport findFirstByBookAndStatus(Book book, int status);

    List<BookImport> findByBookOrderByImportDateDesc(Book book);

    @Query("SELECT bi FROM BookImport bi WHERE " +
            "(:bookId is null or bi.book.id = :bookId) and " +
            "(:status is null or bi.status = :status) and " +
            "(:searchKeyword is null or lower(bi.book.name) like lower(concat('%', :searchKeyword,'%')))")
    Page<BookImport> findWithFilters(@Param("bookId") Integer bookId,
                                     @Param("searchKeyword") String searchKeyword,
                                     @Param("status") Integer status,
                                     Pageable pageable);

}
