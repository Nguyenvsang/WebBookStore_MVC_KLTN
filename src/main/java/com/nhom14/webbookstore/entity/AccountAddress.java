package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "accountaddress")
public class AccountAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "recipient_name", columnDefinition = "varchar(255) NOT NULL")
    private String recipientName; // tên người nhận

    @Column(name = "phone_number", columnDefinition = "varchar(255) NOT NULL")
    private String phoneNumber; // số điện thoai

    //bi-directional many-to-one association with City
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;// thành phố/tỉnh

    //bi-directional many-to-one association with District
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id")
    private District district; // quận/huyện

    //bi-directional many-to-one association with Ward
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ward_id")
    private Ward ward; // phường/xã

    @Column(name = "address_details", columnDefinition = "varchar(255) NOT NULL")
    private String addressDetails; // tên đường, tòa nhà, số nhà

    @Column(name = "address_note", columnDefinition = "varchar(255)")
    private String addressNote; // Ghi chú về địa chỉ nếu có

    @Column(name = "address_type", columnDefinition = "INT NOT NULL")
    private int addressType; // loại địa chỉ (0: Văn phòng, 1: Nhà)

    @Column(name = "is_default", columnDefinition = "INT NOT NULL")
    private int isDefault; // 0: Không đặt, 1: Đặt làm mặc định

    //bi-directional many-to-one association with Account
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    // Các phương thức getter, setter, constructor, toString...

    public AccountAddress() {
        // default constructor
    }
}