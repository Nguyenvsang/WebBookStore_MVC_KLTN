package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;

import java.util.List;

public interface BookImportService {
    // Truy vấn cơ sở dữ liệu để lấy tất cả các đợt nhập sách có book tương ứng,
    // Sắp xếp kết quả theo thời gian nhập tăng dần (12/04/2024 sẽ đầu danh sách rồi ,mới tới ngày 13 14)
    List<BookImport> getBookImportsByBookOrderByImportDateAsc(Book book);

    void updateBookImport(BookImport bookImport);

    BookImport getLatestBookImportByBook(Book book);
}
