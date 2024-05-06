package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.BookPrice;
import com.nhom14.webbookstore.repository.BookPriceRepository;
import com.nhom14.webbookstore.service.BookPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookPriceServiceImpl implements BookPriceService {
    private BookPriceRepository bookPriceRepository;

    @Autowired
    public BookPriceServiceImpl(BookPriceRepository bookPriceRepository) {
        super();
        this.bookPriceRepository = bookPriceRepository;
    }

    @Override
    public void saveBookPrice(BookPrice bookPrice) {
        bookPriceRepository.save(bookPrice);
    }

    @Override
    public BookPrice getBookPriceById(long id) {
        return bookPriceRepository.findById(id).orElse(null);
        // Không có sẽ trả về null
    }

    @Override
    public Page<BookPrice> getFilteredBookPrices(Integer bookId, String searchKeyword, Pageable pageable) {
        return bookPriceRepository.findWithFilters(bookId, searchKeyword, pageable);
    }

    @Override
    public List<BookPrice> getAllBookPrices() {
        return bookPriceRepository.findAll(); // Nếu không có sách sẽ trả về empty list
    }
}
