package com.nhom14.webbookstore.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "`order`")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "date_order", columnDefinition = "date NOT NULL")
	private Date dateOrder; // Ngày đặt hàng
	@Column(name = "expected_delivery_date_1", columnDefinition = "date")
	private Date expectedDeliveryDate1; // Ngày giao hàng dự kiến 1
	@Column(name = "expected_delivery_date_2", columnDefinition = "date")
	private Date expectedDeliveryDate2; // Ngày giao hàng dự kiến 2
	@Column(name = "delivery_date", columnDefinition = "date")
	private Date deliveryDate; // Ngày giao hàng
	@Column(name = "total_price", columnDefinition = "double NOT NULL")
	private double totalPrice;
	
	// Tuy la mot nguoi dung nhung ho co quyen dung nhieu email, sdt 
	@Column(name = "name", columnDefinition = "varchar(255) NOT NULL")
	private String name;
	@Column(name = "address", columnDefinition = "varchar(255) NOT NULL")
	private String address;
	@Column(name = "phone_number", columnDefinition = "varchar(20) NOT NULL")
    private String phoneNumber;
	@Column(name = "email", columnDefinition = "char(255) NOT NULL")
    private String email;
	//bi-directional many-to-one association with Account
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;
    @Column(name = "status", columnDefinition = "int NOT NULL")
    private int status; // 0: Chờ xác nhận, 1: Chờ lấy hàng, 
    					// 2: Đang giao, 3: Đã giao
						// 4: Yêu cầu hủy đơn, 5: Đã hủy đơn
						// 6: Yêu cầu trả hàng, 7: Xử lý trả hàng
						// 8: Trả hàng thành công, 9: Yêu cầu trả hàng bị từ chối
						// 10: Không nhận hàng, 11: Đã nhận hàng

//	Chờ xác nhận > Chờ lấy hàng > Đang giao > Đã giao > Đã nhận hàng
//	Chờ xác nhận > Yêu cầu hủy đơn > Xử lý hủy đơn > Đã hủy đơn
//	Chờ xác nhận > Chờ lấy hàng > Đang giao > Đã giao > Yêu cầu trả hàng > Xử lý trả hàng > Trả hàng thành công
//	Chờ xác nhận > Chờ lấy hàng > Đang giao > Đã giao > Yêu cầu trả hàng > Xử lý trả hàng > Yêu cầu trả hàng bị từ chối > Đã nhận hàng
//	Chờ xác nhận > Chờ lấy hàng > Đang giao > Không nhận hàng
//	Chưa thanh toán > Đã thanh toán > Đã hoàn tiền
//	Khi Trả hàng thành công nghĩa là cũng bao gồm Đã hoàn tiền
//	Nếu Yêu cầu hủy đơn mà đã thanh toán tiền cho đơn đó thì khi ở trạng thái Đã hủy đơn cũng bao gồm việc Đã hoàn tiền cho khách
//	Được yêu cầu trả hàng trong vòng 15 ngày kể từ khi giao hàng thành công (Đã giao). Quá 15 ngày thì nút Trả hàng sẽ biến mất và trên hệ thống admin sẽ chuyển trạng thái đơn hàng thành Đã nhận hàng
//	Được yêu cầu hủy đơn khi đơn hàng ở trạng thái Chờ xác nhận
//	Nếu đã nhấn Đã nhận hàng thì khách hàng không thể chọn Trả hàng sau đó nữa

    //bi-directional one-to-many association with OrderItem
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    private List<OrderItem> orderitems;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "order")
	private PaymentStatus paymentStatus;

	public Order() {

    }

	public Order(Date dateOrder, Date expectedDeliveryDate1, Date expectedDeliveryDate2, Date deliveryDate, double totalPrice, String name, String address, String phoneNumber, String email,
                 Account account, int status, List<OrderItem> orderitems, PaymentStatus paymentStatus) {
		super();
		this.dateOrder = dateOrder;
		this.expectedDeliveryDate1 = expectedDeliveryDate1;
		this.expectedDeliveryDate2 = expectedDeliveryDate2;
		this.deliveryDate = deliveryDate;
		this.totalPrice = totalPrice;
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.account = account;
		this.status = status;
		this.orderitems = orderitems;
        this.paymentStatus = paymentStatus;
    }



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateOrder() {
		return dateOrder;
	}

	public void setDateOrder(Date dateOrder) {
		this.dateOrder = dateOrder;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<OrderItem> getOrderitems() {
		return orderitems;
	}

	public void setOrderitems(List<OrderItem> orderitems) {
		this.orderitems = orderitems;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Date getExpectedDeliveryDate1() {
		return expectedDeliveryDate1;
	}

	public void setExpectedDeliveryDate1(Date expectedDeliveryDate1) {
		this.expectedDeliveryDate1 = expectedDeliveryDate1;
	}

	public Date getExpectedDeliveryDate2() {
		return expectedDeliveryDate2;
	}

	public void setExpectedDeliveryDate2(Date expectedDeliveryDate2) {
		this.expectedDeliveryDate2 = expectedDeliveryDate2;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
}