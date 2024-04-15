package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;
import com.nhom14.webbookstore.repository.BookImportRepository;
import com.nhom14.webbookstore.service.BookImportService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
