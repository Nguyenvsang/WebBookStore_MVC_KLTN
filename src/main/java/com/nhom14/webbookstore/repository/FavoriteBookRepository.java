package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.FavoriteBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {
    FavoriteBook findByAccountAndBook(Account account, Book book);
}
