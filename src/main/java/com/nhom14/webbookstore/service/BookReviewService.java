package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface BookReviewService {
    List<BookReview> getPublishedReviewsByBook(Book book);

    BookReview getReviewByAccountAndBook(Account account, Book book);

    void save(BookReview review);

    void update(BookReview review);

    BookReview findById(int reviewId);

    void delete(BookReview review);

    Page<BookReview> getFilteredBookReviews(Integer rating, Boolean isPurchased, Boolean isPublished, String keyword, Pageable pageable);

    List<BookReview> getAllBookReviews();
}
