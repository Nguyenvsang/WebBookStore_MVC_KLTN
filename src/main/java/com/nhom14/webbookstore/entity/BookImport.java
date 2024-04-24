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
    // chỉ nhập thêm đợt sách mới nếu đợt sách cũ hết,
    // không cần duyệt qua nhiều đợt nhật, mà chỉ dựa vào đợt nhập mới nhất
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //bi-directional many-to-one association with Book
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "quantity", columnDefinition = "INT NOT NULL")
    private int quantity;

    @Column(name = "remaining_quantity", columnDefinition = "INT NOT NULL")
    private int remainingQuantity;

    @Column(name = "import_price", columnDefinition = "double NOT NULL")
    private double importPrice;

    @Column(name = "import_date", columnDefinition = "DATE NOT NULL")
    private Timestamp importDate;

    @Column(name = "status", columnDefinition = "INT NOT NULL")
    private int status; // 0: Ngừng bán, 1: Đang bán, 2: Chưa bán
}
