package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "discount")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //bi-directional many-to-one association with Book
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "discount_percent", columnDefinition = "INT NOT NULL")
    private int discountPercent;

    @Column(name = "discounted_price", columnDefinition = "double")
    private double discountedPrice;

    @Column(name = "status", columnDefinition = "INT NOT NULL")
    private int status;//0: Ngưng áp dụng, 1: Áp dụng, 2: Chưa áp dụng, 3: Hủy bỏ
    // 0: Ngừng áp dụng khi đã quá hạn
    // 2: Chưa áp dụng khi chưa đến ngày bắt đầu
    // 3: Hủy bỏ khi nhầm lẫn

    @Column(name = "start_date", columnDefinition = "DATETIME(6) NOT NULL")
    private Timestamp startDate;

    @Column(name = "end_date", columnDefinition = "DATETIME(6) NOT NULL")
    private Timestamp endDate;
}
