package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.repository.BookImportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookImportServiceImpl {
    private BookImportRepository bookImportRepository;

    @Autowired
    public BookImportServiceImpl(BookImportRepository bookImportRepository) {
        super();
        this.bookImportRepository = bookImportRepository;
    }
}
