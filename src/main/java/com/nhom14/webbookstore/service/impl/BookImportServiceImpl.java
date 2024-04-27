package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;
import com.nhom14.webbookstore.repository.BookImportRepository;
import com.nhom14.webbookstore.service.BookImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookImportServiceImpl implements BookImportService {
    private BookImportRepository bookImportRepository;

    @Autowired
    public BookImportServiceImpl(BookImportRepository bookImportRepository) {
        super();
        this.bookImportRepository = bookImportRepository;
    }

    @Override
    public List<BookImport> getBookImportsByBookOrderByImportDateAsc(Book book) {
        return bookImportRepository.findByBookOrderByImportDateAsc(book);
    }

    @Override
    public void updateBookImport(BookImport bookImport) {
        bookImportRepository.save(bookImport);
    }

    @Override
    public BookImport getLatestBookImportByBook(Book book) {
        return bookImportRepository.findTopByBookOrderByImportDateDesc(book);
        // Không tìm thấy sẽ trả về null
    }

    @Override
    public void saveBookImport(BookImport bookImport) {
        bookImportRepository.save(bookImport);
    }

    @Override
    public BookImport getBookImportByBookAndStatus(Book book, int status) {
        return bookImportRepository.findFirstByBookAndStatus(book, status);
    }

    @Override
    public List<BookImport> getBookImportsByBookOrderByImportDateDesc(Book book) {
        return bookImportRepository.findByBookOrderByImportDateDesc(book);
    }

    @Override
    public BookImport getBookImportById(long id) {
        return bookImportRepository.findById(id).orElse(null);
    }

    @Override
    public Page<BookImport> getFilteredBookImports(Integer bookId, String searchKeyword, Integer status, Pageable pageable) {
        return bookImportRepository.findWithFilters(bookId, searchKeyword, status, pageable);
    }

    @Override
    public List<BookImport> getAllBookImports() {
        return bookImportRepository.findAll(); // Nếu không có sách sẽ trả về empty list
    }
}
