package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "imgreturnorder")
public class ImgReturnOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "img1", columnDefinition = "varchar(255) NOT NULL")
    private String img1;
    @Column(name = "img2", columnDefinition = "varchar(255)")
    private String img2;
    @Column(name = "img3", columnDefinition = "varchar(255)")
    private String img3;
    @Column(name = "img4", columnDefinition = "varchar(255)")
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

}
