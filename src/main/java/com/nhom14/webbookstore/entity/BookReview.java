package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "bookreview")
public class BookReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //bi-directional many-to-one association with Account
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    //bi-directional many-to-one association with Book
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "rating", columnDefinition = "INT NOT NULL")
    private int rating; // 1 đến 5 sao
    @Column(name = "comment", columnDefinition = "TEXT NOT NULL")
    private String comment;
    @Column(name = "date_posted", columnDefinition = "datetime(6)")
    private Timestamp datePosted;
    @Column(name = "is_purchased", columnDefinition = "TINYINT(1) NOT NULL")
    private boolean isPurchased;
    @Column(name = "is_published", columnDefinition = "TINYINT(1) NOT NULL")
    private boolean isPublished; //Được cho phép đăng lên chưa

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bookReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookReviewLike> bookReviewLikes;
}
