package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.BookReview;
import com.nhom14.webbookstore.entity.BookReviewLike;

import java.util.List;

public interface BookReviewLikeService {
    BookReviewLike findByAccountAndBookReview(Account account, BookReview review);

    void delete(BookReviewLike like);

    void save(BookReviewLike like);

    List<BookReview> getLikedReviewsByAccount(Account account);
}
