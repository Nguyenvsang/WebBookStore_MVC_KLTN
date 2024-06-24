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
            "(:searchKeywordAsInteger is null or br.id = :searchKeywordAsInteger) and " +
            "(:rating is null or br.rating = :rating) and " +
            "(:isPurchased is null or br.isPurchased = :isPurchased) and " +
            "(:isPublished is null or br.isPublished = :isPublished) and " +
            "(:searchKeyword is null or lower(br.comment) like lower(concat('%', :searchKeyword,'%')) or lower(br.book.name) like lower(concat('%', :searchKeyword,'%')) or lower(br.account.username) like lower(concat('%', :searchKeyword,'%')))")
    Page<BookReview> findWithFilters(@Param("rating") Integer rating,
                                     @Param("isPurchased") Boolean isPurchased,
                                     @Param("isPublished") Boolean isPublished,
                                     @Param("searchKeyword") String searchKeyword,
                                     @Param("searchKeywordAsInteger") Integer searchKeywordAsInteger,
                                     Pageable pageable);

}
