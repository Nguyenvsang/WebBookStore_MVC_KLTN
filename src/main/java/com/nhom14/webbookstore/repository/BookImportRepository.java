package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookImportRepository extends JpaRepository<BookImport, Long> {
    List<BookImport> findByBookOrderByImportDateAsc(Book book);

    BookImport findTopByBookOrderByImportDateDesc(Book book);
}
