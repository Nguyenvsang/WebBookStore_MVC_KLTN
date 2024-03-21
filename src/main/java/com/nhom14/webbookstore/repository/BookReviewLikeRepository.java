package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.BookReview;
import com.nhom14.webbookstore.entity.BookReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookReviewLikeRepository extends JpaRepository<BookReviewLike, Long> {
    BookReviewLike findByAccountAndBookReview(Account account, BookReview review);

    List<BookReviewLike> findByAccount(Account account);
}
