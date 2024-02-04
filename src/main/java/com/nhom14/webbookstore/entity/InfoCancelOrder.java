package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "infocancelorder")
public class InfoCancelOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "type", columnDefinition = "INT")
    private int type;   //0: Đặt nhầm sản phẩm, 1: Phí vận chuyển cao
                        //2: Muốn đổi địa chỉ giao hàng, 3: Muốn thay đổi sản phầm
                        //4: Nhầm phương thức thanh toán, 5: Muốn đổi phương thức thanh toán
                        //6: Đổi ý, không muốn mua nữa,
                        //7: Khác
    @Column(name = "other_type", columnDefinition = "text")
    private String otherType;

    public InfoCancelOrder() {

    }

    public InfoCancelOrder(Order order, int type, String otherType) {
        this.order = order;
        this.type = type;
        this.otherType = otherType;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOtherType() {
        return otherType;
    }

    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }
}
