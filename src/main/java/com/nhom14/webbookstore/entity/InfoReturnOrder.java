package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getDetailReason() {
        return detailReason;
    }

    public void setDetailReason(String detailReason) {
        this.detailReason = detailReason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ImgReturnOrder getImgReturnOrder() {
        return imgReturnOrder;
    }

    public void setImgReturnOrder(ImgReturnOrder imgReturnOrder) {
        this.imgReturnOrder = imgReturnOrder;
    }

    public VideoReturnOrder getVideoReturnOrder() {
        return videoReturnOrder;
    }

    public void setVideoReturnOrder(VideoReturnOrder videoReturnOrder) {
        this.videoReturnOrder = videoReturnOrder;
    }
}
