package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "videoreturnorder")
public class VideoReturnOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "unboxing_video", columnDefinition = "varchar(255) NOT NULL")
    private String unboxingVideo; // video 1
    @Column(name = "product_video", columnDefinition = "varchar(255) NOT NULL")
    private String productVideo;  // video 2

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inforeturnorder_id")
    private InfoReturnOrder infoReturnOrder;

    public VideoReturnOrder() {
    }

    public VideoReturnOrder(String unboxingVideo, String productVideo) {
        this.unboxingVideo = unboxingVideo;
        this.productVideo = productVideo;
    }

}
