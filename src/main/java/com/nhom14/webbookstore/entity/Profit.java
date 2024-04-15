package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "profit")
public class Profit {// Để tính lợi nhuận, bạn sẽ cần trừ chi phí nhập sách khỏi doanh thu.
    // Khi đơn hàng có trạng thái 10 (đã nhận hàng), hệ thống sẽ tự động cập nhật trạng thái thanh toán của đơn hàng đó thành 1 (đã thanh toán).
    // Từ đây, doanh thu và lợi nhuận sẽ được tính cho đơn hàng đó.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //bi-directional one-to-one association with OrderItem
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orderitem_id")
    private OrderItem orderItem;

    @Column(name = "cost_price", columnDefinition = "double NOT NULL")
    private double costPrice;
    @Column(name = "sell_price", columnDefinition = "double NOT NULL")
    private double sellPrice;
    @Column(name = "profit", columnDefinition = "double NOT NULL")
    private double profit; // Bằng (sellPrice - costPrice)*số lượng
    @Column(name = "date", columnDefinition = "DATETIME(6) NOT NULL")
    private Timestamp date; // Ngày ghi nhận
}
