package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookReview;

import java.util.List;

public interface BookReviewService {
    List<BookReview> getPublishedReviewsByBook(Book book);

    BookReview getReviewByAccountAndBook(Account account, Book book);

    void save(BookReview review);

    BookReview findById(int reviewId);

    void delete(BookReview review);
}
