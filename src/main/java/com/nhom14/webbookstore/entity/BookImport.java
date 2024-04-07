package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "bookimport")
public class BookImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //bi-directional many-to-one association with Book
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "quantity", columnDefinition = "INT NOT NULL")
    private int quantity;

    @Column(name = "import_price", columnDefinition = "double NOT NULL")
    private double importPrice;

    @Column(name = "import_date", columnDefinition = "DATE NOT NULL")
    private Timestamp importDate;
}