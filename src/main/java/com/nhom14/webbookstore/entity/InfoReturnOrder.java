package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "inforeturnorder")
public class InfoReturnOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "reason", columnDefinition = "text NOT NULL")
    private String reason;

    @Column(name = "detail_reason", columnDefinition = "text NOT NULL")
    private String detailReason;

    @Column(name = "request_date", columnDefinition = "datetime(6) NOT NULL")
    private Timestamp requestDate;

    @Column(name = "name", columnDefinition = "varchar(255) NOT NULL")
    private String name;

    @Column(name = "address", columnDefinition = "varchar(255) NOT NULL")
    private String address;

    @Column(name = "phone_number", columnDefinition = "varchar(255) NOT NULL")
    private String phoneNumber;

    @Column(name = "email", columnDefinition = "char(255) NOT NULL")
    private String email;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "infoReturnOrder")
    private ImgReturnOrder imgReturnOrder;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "infoReturnOrder")
    private VideoReturnOrder videoReturnOrder;

    public InfoReturnOrder() {

    }

    public InfoReturnOrder(Order order, String reason, Timestamp requestDate, String detailReason, String name, String address, String phoneNumber, String email) {
        this.order = order;
        this.reason = reason;
        this.detailReason = detailReason;
        this.requestDate = requestDate;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

}
