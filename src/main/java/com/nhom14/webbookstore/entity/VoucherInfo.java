package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "voucherinfo")
public class VoucherInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "total_amount", columnDefinition = "DOUBLE NOT NULL")
    private double totalAmount; // Thành tiền (Tổng tiền ban đầu)

    @Column(name = "voucher_discount", columnDefinition = "DOUBLE NOT NULL")
    private double voucherDiscount; // Giảm giá (Số tiền được giảm)

    @Column(name = "discounted_amount", columnDefinition = "DOUBLE NOT NULL")
    private double discountedAmount; // Tổng cộng (Số tiền cuối cùng, sau khi đã trừ phần được giảm)

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    //bi-directional many-to-one association with Category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
}
