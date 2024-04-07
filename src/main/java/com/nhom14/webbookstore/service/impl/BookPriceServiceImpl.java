package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.repository.BookPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookPriceServiceImpl {
    private BookPriceRepository bookPriceRepository;

    @Autowired
    public BookPriceServiceImpl(BookPriceRepository bookPriceRepository) {
        super();
        this.bookPriceRepository = bookPriceRepository;
    }
}
