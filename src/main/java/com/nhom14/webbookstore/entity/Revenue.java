package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "revenue")
public class Revenue { // Doanh thu: Doanh thu thường được tính bằng cách lấy tổng số tiền thu được từ việc bán hàng
    // Ví dụ, nếu bạn bán 5 cuốn sách với giá 200.000 VND mỗi cuốn, doanh thu từ đơn hàng đó sẽ là 5 * 200.000 = 1.000.000 VND.
    // Để tính lợi nhuận, bạn sẽ cần trừ chi phí nhập sách khỏi doanh thu.
    // Nếu phí ship được người dùng trả trực tiếp cho đơn vị vận chuyển, thì số tiền đó không phải là doanh thu của bạn

    // Khi đơn hàng có trạng thái 10 (đã nhận hàng), hệ thống sẽ tự động cập nhật trạng thái thanh toán của đơn hàng đó thành 1 (đã thanh toán).
    // Từ đây, doanh thu và lợi nhuận sẽ được tính cho đơn hàng đó.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //bi-directional one-to-one association with Order
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "revenue", columnDefinition = "double NOT NULL")
    private double revenue; // doanh thu = tổng giá trị order khi order ở trạng thái Đã nhận hàng

    @Column(name = "date", columnDefinition = "DATETIME(6) NOT NULL")
    private Timestamp date; // Ngày ghi nhận
}
