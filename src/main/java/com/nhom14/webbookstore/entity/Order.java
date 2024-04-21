package com.nhom14.webbookstore.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "`order`")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "date_order", columnDefinition = "datetime(6) NOT NULL")
	private Timestamp dateOrder; // Ngày đặt hàng
	@Column(name = "expected_delivery_date_1", columnDefinition = "datetime(6)")
	private Timestamp expectedDeliveryDate1; // Ngày giao hàng dự kiến 1
	@Column(name = "expected_delivery_date_2", columnDefinition = "datetime(6)")
	private Timestamp expectedDeliveryDate2; // Ngày giao hàng dự kiến 2
	@Column(name = "delivery_date", columnDefinition = "datetime(6)")
	private Timestamp deliveryDate; // Ngày giao hàng
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
	@Column(name = "is_completed", columnDefinition = "int NOT NULL DEFAULT 0")
	private int isCompleted; // 0: Đơn hàng chưa hoàn thành, 1: Đơn hàng đã hoàn thành
							 // 2: Đơn hàng đã hủy thành công, 3: Đơn hàng đã trả thành công
    @Column(name = "status", columnDefinition = "int NOT NULL")
    private int status; // 0: Chờ xác nhận, 1: Chờ lấy hàng, 
    					// 2: Đang giao, 3: Đã giao
						// 4: Đã hủy đơn, 5: Yêu cầu trả hàng
						// 6: Xử lý trả hàng, 7: Trả hàng thành công
						// 8: Yêu cầu trả hàng bị từ chối, 9: Không nhận hàng
						// 10: Đã nhận hàng

//	Chờ xác nhận > Chờ lấy hàng > Đang giao > Đã giao > Đã nhận hàng
//	Chờ xác nhận > Đã hủy đơn
//	Chờ xác nhận > Chờ lấy hàng > Đang giao > Đã giao > Yêu cầu trả hàng > Xử lý trả hàng > Trả hàng thành công
//	Chờ xác nhận > Chờ lấy hàng > Đang giao > Đã giao > Yêu cầu trả hàng > Xử lý trả hàng > Yêu cầu trả hàng bị từ chối > Đã nhận hàng
//	Chờ xác nhận > Chờ lấy hàng > Đang giao > Không nhận hàng
//	Chưa thanh toán > Đã thanh toán > Đã hoàn tiền
//	Khi Trả hàng thành công nghĩa là cũng bao gồm Đã hoàn tiền
//	Nếu Yêu cầu hủy đơn mà đã thanh toán tiền cho đơn đó thì khi ở trạng thái Đã hủy đơn cũng bao gồm Xử lý hoàn tiền cho khách
//	Được yêu cầu trả hàng trong vòng 15 ngày (không kể giờ phút giây) kể từ khi giao hàng thành công (Đã giao). Quá 15 ngày thì nút Trả hàng sẽ biến mất và trên hệ thống admin sẽ chuyển trạng thái đơn hàng thành Đã nhận hàng
//	Được yêu cầu hủy đơn khi đơn hàng ở trạng thái Chờ xác nhận
//	Nếu đã nhấn Đã nhận hàng thì khách hàng vẫn có thể chọn Trả hàng sau đó
// Khi đơn hàng có trạng thái 10 (đã nhận hàng), hệ thống sẽ tự động cập nhật trạng thái thanh toán của đơn hàng đó thành 1 (đã thanh toán).
// Khi Admin xác nhận Đơn hàng Đã hoàn thành thì doanh thu và lợi nhuận sẽ được tính cho đơn hàng đó.
// Nếu đơn đã Hủy thì Admin phải chuyển isCompleted về Đơn hàng đã hủy thành công
// Nếu đơn đã Trả thành công thì Admin phải chuyển isCompleted về Đơn hàng đã trả thành công

    //bi-directional one-to-many association with OrderItem
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    private List<OrderItem> orderitems;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "order")
	private PaymentStatus paymentStatus;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "order")
	private InfoCancelOrder infoCancelOrder;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "order")
	private InfoReturnOrder infoReturnOrder;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "order")
	private Revenue revenue;

	public Order() {

    }

	public Order(Timestamp dateOrder, Timestamp expectedDeliveryDate1, Timestamp expectedDeliveryDate2, Timestamp deliveryDate, double totalPrice, String name, String address, String phoneNumber, String email,
                 Account account, int status, List<OrderItem> orderitems, PaymentStatus paymentStatus, InfoCancelOrder infoCancelOrder) {
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
		this.infoCancelOrder = infoCancelOrder;
    }


}