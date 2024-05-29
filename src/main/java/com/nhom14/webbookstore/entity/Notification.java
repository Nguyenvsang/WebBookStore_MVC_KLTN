package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "content", columnDefinition = "varchar(255)")
    private String content; // nội dung thông báo

    @Column(name = "status", columnDefinition = "INT NOT NULL")
    private int status; //0: chưa đọc, 1: đã đọc

    @Column(name = "type", columnDefinition = "INT NOT NULL")
    private int type; //loại thông báo, 0: order

    @Column(name = "referred_id", columnDefinition = "INT NOT NULL")
    private int referredId; //mã order liên quan, mã khác nếu có

    //bi-directional many-to-one association with Account
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver")
    private Account receiver;

    //bi-directional many-to-one association with Account
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trigger_user")
    private Account triggerUser;

    @Column(name = "sent_time", columnDefinition = "DATETIME(6) NOT NULL")
    private Timestamp sentTime;
}
