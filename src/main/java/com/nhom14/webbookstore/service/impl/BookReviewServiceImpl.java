package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookReview;
import com.nhom14.webbookstore.repository.BookReviewRepository;
import com.nhom14.webbookstore.service.BookReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookReviewServiceImpl implements BookReviewService {

    private BookReviewRepository bookReviewRepository;

    @Autowired
    public BookReviewServiceImpl(BookReviewRepository bookReviewRepository) {
        super();
        this.bookReviewRepository = bookReviewRepository;
    }

    @Override
    public List<BookReview> getPublishedReviewsByBook(Book book) {
        return bookReviewRepository.findByBookAndIsPublished(book, true);
        // Không tìm thấy sẽ trả về danh sách rỗng, isEmpty()
    }

    @Override
    public BookReview getReviewByAccountAndBook(Account account, Book book) {
        return bookReviewRepository.findByAccountAndBook(account, book); // không thấy sẽ trả về null
    }

    @Override
    public void save(BookReview review) {
        bookReviewRepository.save(review);
    }

    @Override
    public void update(BookReview review) {
        bookReviewRepository.save(review);
    }

    @Override
    public BookReview findById(int reviewId) {
        Optional<BookReview> bookReview = bookReviewRepository.findById(reviewId);
        return bookReview.orElse(null);
    }

    @Override
    public void delete(BookReview review) {
        bookReviewRepository.delete(review);
    }

    public Page<BookReview> getFilteredBookReviews(Integer rating, Boolean isPurchased, Boolean isPublished, String keyword, Pageable pageable) {
        return bookReviewRepository.findWithFilters(rating, isPurchased, isPublished, keyword, pageable);
    }

    @Override
    public List<BookReview> getAllBookReviews() {
        return bookReviewRepository.findAll();
    }

}
