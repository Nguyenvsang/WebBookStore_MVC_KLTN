package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "code", columnDefinition = "char(255) NOT NULL UNIQUE")
    private String code;

    //bi-directional many-to-one association with Category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "quantity", columnDefinition = "INT NOT NULL")
    private int quantity;

    @Column(name = "remaining_quantity", columnDefinition = "INT NOT NULL")
    private int remainingQuantity;

    @Column(name = "discount_percent", columnDefinition = "INT")
    private int discountPercent;

    @Column(name = "amount_discount", columnDefinition = "DOUBLE")
    private double amountDiscount;

    @Column(name = "minimum_order_value", columnDefinition = "DOUBLE NOT NULL")
    private double minimumOrderValue; //Nếu scope=0 thì sẽ tính giá trị tổng đơn hàng
    // Nếu scope=1 thì sẽ tính theo giá trị của tổng các món hàng thuộc danh mục có trong đơn hàng

    @Column(name = "voucher_scope", columnDefinition = "INT NOT NULL")
    private int voucherScope; //0: Áp dụng cho tất cả sách, 1: Cho 1 danh mục duy nhất

    @Column(name = "status", columnDefinition = "INT NOT NULL")
    private int status;//0: Ngưng áp dụng, 1: Áp dụng, 2: Chưa áp dụng, 3: Hủy bỏ
    // 0: Ngừng áp dụng khi đã quá hạn
    // 2: Chưa áp dụng khi chưa đến ngày bắt đầu
    // 3: Hủy bỏ khi nhầm lẫn

    @Column(name = "start_date", columnDefinition = "DATETIME(6) NOT NULL")
    private Timestamp startDate;

    @Column(name = "end_date", columnDefinition = "DATETIME(6) NOT NULL")
    private Timestamp endDate;

    //bi-directional one-to-many association with VoucherInfo (de quen)
    @OneToMany(mappedBy = "voucher")
    private List<VoucherInfo> voucherInfos;
}
