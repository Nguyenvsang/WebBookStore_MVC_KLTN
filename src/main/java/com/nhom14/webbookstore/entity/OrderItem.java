package com.nhom14.webbookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
	private double price;
	
	//bi-directional many-to-one association with Product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    //bi-directional many-to-one association with Order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
	
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