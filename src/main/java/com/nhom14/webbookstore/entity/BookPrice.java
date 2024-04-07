package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "bookprice")
public class BookPrice { // Giá sách bán ra thực tế từ ngày có hiệu lực
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "sell_price", columnDefinition = "double NOT NULL")
    private double sellPrice;

    @Column(name = "effective_date", columnDefinition = "DATETIME(6) NOT NULL")
    private Timestamp effectiveDate; // Ngày có hiệu lực
}
