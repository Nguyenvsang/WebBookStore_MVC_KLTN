package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {

    List<BookReview> findByBookAndIsPublished(Book book, boolean isPublished);

    BookReview findByAccountAndBook(Account account, Book book);
}
