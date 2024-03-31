package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.FavoriteBook;
import com.nhom14.webbookstore.repository.FavoriteBookRepository;
import com.nhom14.webbookstore.service.FavoriteBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteBookServiceImpl implements FavoriteBookService {

    private final FavoriteBookRepository favoriteBookRepository;

    @Autowired
    public FavoriteBookServiceImpl(FavoriteBookRepository favoriteBookRepository) {
        this.favoriteBookRepository = favoriteBookRepository;
    }

    @Override
    public void addFavoriteBook(FavoriteBook favoriteBook) {
        favoriteBookRepository.save(favoriteBook);
    }

    @Override
    public FavoriteBook findByAccountAndBook(Account account, Book book) {
        return favoriteBookRepository.findByAccountAndBook(account, book);
    }

    @Override
    public void delete(FavoriteBook favoriteBook) {
        favoriteBookRepository.delete(favoriteBook);
    }
}
