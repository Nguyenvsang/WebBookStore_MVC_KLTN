package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "imgreturnorder")
public class ImgReturnOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "img1", columnDefinition = "varchar(255) NOT NULL")
    private String img1;
    @Column(name = "img2", columnDefinition = "varchar(255) NOT NULL")
    private String img2;
    @Column(name = "img3", columnDefinition = "varchar(255) NOT NULL")
    private String img3;
    @Column(name = "img4", columnDefinition = "varchar(255) NOT NULL")
    private String img4;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inforeturnorder_id")
    private InfoReturnOrder infoReturnOrder;

    public ImgReturnOrder() {
    }

    public ImgReturnOrder(String img1, String img2, String img3, String img4, InfoReturnOrder infoReturnOrder) {
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.infoReturnOrder = infoReturnOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public InfoReturnOrder getInfoReturnOrder() {
        return infoReturnOrder;
    }

    public void setInfoReturnOrder(InfoReturnOrder infoReturnOrder) {
        this.infoReturnOrder = infoReturnOrder;
    }
}
