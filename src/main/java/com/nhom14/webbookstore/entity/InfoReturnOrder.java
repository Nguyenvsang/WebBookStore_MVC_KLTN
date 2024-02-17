package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;

import java.sql.Date;

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

    @Column(name = "return_date", columnDefinition = "DATE NOT NULL")
    private Date returnDate;

    @Column(name = "img", columnDefinition = "varchar(255) NOT NULL")
    private String img;

    @Column(name = "video", columnDefinition = "varchar(255) NOT NULL")
    private String video;

    @Column(name = "name", columnDefinition = "varchar(255) NOT NULL")
    private String name;

    @Column(name = "address", columnDefinition = "varchar(255) NOT NULL")
    private String address;

    @Column(name = "phone_number", columnDefinition = "varchar(255) NOT NULL")
    private String phoneNumber;

    @Column(name = "email", columnDefinition = "char(255) NOT NULL")
    private String email;

    public InfoReturnOrder() {

    }

    public InfoReturnOrder(Order order, String reason, Date returnDate, String img, String video, String name, String address, String phoneNumber, String email) {
        this.order = order;
        this.reason = reason;
        this.returnDate = returnDate;
        this.img = img;
        this.video = video;
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

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
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
}
