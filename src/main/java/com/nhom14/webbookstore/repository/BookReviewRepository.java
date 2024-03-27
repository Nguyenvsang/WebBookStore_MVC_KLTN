package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Book;
import com.nhom14.webbookstore.entity.BookReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Integer>{

    List<BookReview> findByBookAndIsPublished(Book book, boolean isPublished);

    BookReview findByAccountAndBook(Account account, Book book);

    @Query("SELECT br FROM BookReview br WHERE " +
            "(:rating is null or br.rating = :rating) and " +
            "(:isPurchased is null or br.isPurchased = :isPurchased) and " +
            "(:isPublished is null or br.isPublished = :isPublished) and " +
            "(:keyword is null or lower(br.comment) like lower(concat('%', :keyword,'%')))")
    Page<BookReview> findWithFilters(@Param("rating") Integer rating,
                                     @Param("isPurchased") Boolean isPurchased,
                                     @Param("isPublished") Boolean isPublished,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);

}
