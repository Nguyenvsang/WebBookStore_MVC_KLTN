package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.BookReview;
import com.nhom14.webbookstore.entity.BookReviewLike;
import com.nhom14.webbookstore.repository.BookReviewLikeRepository;
import com.nhom14.webbookstore.service.BookReviewLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookReviewLikeServiceImpl implements BookReviewLikeService {
    private BookReviewLikeRepository bookReviewLikeRepository;

    @Autowired
    public BookReviewLikeServiceImpl(BookReviewLikeRepository bookReviewLikeRepository) {
        super();
        this.bookReviewLikeRepository = bookReviewLikeRepository;
    }

    @Override
    public BookReviewLike findByAccountAndBookReview(Account account, BookReview review) {
        return bookReviewLikeRepository.findByAccountAndBookReview(account, review);
        // Không tìm thấy sẽ trả về null
    }

    @Override
    public void delete(BookReviewLike like) {
        bookReviewLikeRepository.delete(like);
    }

    @Override
    public void save(BookReviewLike like) {
        bookReviewLikeRepository.save(like);
    }

    @Override
    public List<BookReview> getLikedReviewsByAccount(Account account) {
        List<BookReviewLike> likes = bookReviewLikeRepository.findByAccount(account);
        List<BookReview> reviews = new ArrayList<>();
        for (BookReviewLike like : likes) {
            reviews.add(like.getBookReview());
        }
        return reviews;
        // Không tìm thấy sẽ trả về danh sách rỗng, isEmpty()
    }
}
