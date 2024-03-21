package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}
