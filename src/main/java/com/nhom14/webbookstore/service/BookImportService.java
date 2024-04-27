package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookImport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface BookImportService {
    // Truy vấn cơ sở dữ liệu để lấy tất cả các đợt nhập sách có book tương ứng,
    // Sắp xếp kết quả theo thời gian nhập tăng dần (12/04/2024 sẽ đầu danh sách rồi ,mới tới ngày 13 14)
    List<BookImport> getBookImportsByBookOrderByImportDateAsc(Book book);

    void updateBookImport(BookImport bookImport);

    BookImport getLatestBookImportByBook(Book book);

    void saveBookImport(BookImport bookImport);

    // Lấy đợt nhập sách theo sách và trạng thái
    BookImport getBookImportByBookAndStatus(Book book, int status);

    // Sắp xếp kết quả theo thời gian nhập giảm dần
    List<BookImport> getBookImportsByBookOrderByImportDateDesc(Book book);

    // Phương thức lấy đợt nhập theo id
    BookImport getBookImportById(long id);

    // Lấy danh sách đợt nhập sách với các tham số tìm kiếm và lọc, phân trang
    Page<BookImport> getFilteredBookImports(Integer bookId, String searchKeyword, Integer status, Pageable pageable);

    // Lấy tất cả các đợt nhập sách
    List<BookImport> getAllBookImports();
}
