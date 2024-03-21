package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "paymentstatus")
public class PaymentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "status", columnDefinition = "INT NOT NULL")
    private int status; //0: Chưa thanh toán; 1: Đã thanh toán; 2: Xử lý hoàn tiền
                        //3: Đã hoàn tiền; 4: Không cần thanh toán
    @Column(name = "info", columnDefinition = "text")
    private String info;

    public PaymentStatus() {

    }

    public PaymentStatus(Order order, int status, String info) {
        this.order = order;
        this.status = status;
        this.info = info;
    }

}
