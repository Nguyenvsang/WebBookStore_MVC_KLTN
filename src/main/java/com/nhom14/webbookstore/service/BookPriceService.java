package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.BookPrice;

public interface BookPriceService {
    void saveBookPrice(BookPrice bookPrice);

    // Phương thức lấy bảng giá theo id
    BookPrice getBookPriceById(long id);
}
