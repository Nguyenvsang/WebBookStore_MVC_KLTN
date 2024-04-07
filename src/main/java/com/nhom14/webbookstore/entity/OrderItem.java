package com.nhom14.webbookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "orderitem")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "quantity", columnDefinition = "int NOT NULL")
	private int quantity;
	@Column(name = "price", columnDefinition = "double NOT NULL")
	private double price; // Giá trị bán ra của sách * số lượng ngay thời điểm đặt hàng
	
	//bi-directional many-to-one association with Product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    //bi-directional many-to-one association with Order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "orderItem")
	private Profit profit;
	
	public OrderItem() {
		
	}
	

	public OrderItem(int quantity, double price, Book book, Order order) {
		super();
		this.quantity = quantity;
		this.price = price;
		this.book = book;
		this.order = order;
	}


}