package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.BookPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface BookPriceService {
    void saveBookPrice(BookPrice bookPrice);

    // Phương thức lấy bảng giá theo id
    BookPrice getBookPriceById(long id);

    // Lấy danh sách đợt gán giá sách với các tham số tìm kiếm và lọc, phân trang
    Page<BookPrice> getFilteredBookPrices(Integer bookId, String searchKeyword, Pageable pageable);

    // Lấy tất cả các đợt gán giá bán sách
    List<BookPrice> getAllBookPrices();
}
