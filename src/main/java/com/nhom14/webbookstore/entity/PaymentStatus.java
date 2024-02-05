package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
