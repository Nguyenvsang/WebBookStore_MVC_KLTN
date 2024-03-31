package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.FavoriteBook;

public interface FavoriteBookService {
    void addFavoriteBook(FavoriteBook favoriteBook);

    FavoriteBook findByAccountAndBook(Account account, Book book);

    void delete(FavoriteBook favoriteBook);
}
