package com.nhom14.webbookstore.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "book")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name", columnDefinition = "varchar(255) NOT NULL")
	private String name;
	
	@Column(name = "cost_price", columnDefinition = "double NOT NULL")
	private double costPrice;
	@Column(name = "sell_price", columnDefinition = "double NOT NULL")
	private double sellPrice;

	//bi-directional many-to-one association with Category
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "publisher", columnDefinition = "varchar(255) NOT NULL")
	private String publisher;
	@Column(name = "description", columnDefinition = "text")
	private String description; // mô tả sơ lược
	@Column(name = "status", columnDefinition = "INT NOT NULL")
	private int status; //hoạt động:1, ngừng kd:0
	@Column(name = "detail", columnDefinition = "text")
	private String detail; // chi tiết nội dung 
	@Column(name = "quantity", columnDefinition = "INT NOT NULL")
	private int quantity;
	
	//bi-directional one-to-many association to BookImage
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "book")
	private List<BookImage> bookImages;

	//bi-directional one-to-many association to OrderItem
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "book")
	private List<OrderItem> orderItems;

	//bi-directional one-to-many association with CartItem
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "book")
	private List<CartItem> cartItems;
	
	//bi-directional one-to-many association to BookAuthor
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "book")
	private List<BookAuthor> bookAuthors;

	//bi-directional one-to-many association to BookReview
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "book")
	private List<BookReview> bookReviews;

	//bi-directional one-to-many association to BookReview
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "book")
	private List<FavoriteBook> favoriteBooks;

    public Book() {
    }

	public Book(int id, String name, double costPrice, double sellPrice, Category category, String publisher,
			String description, int status, String detail, int quantity, List<BookImage> bookImages,
			List<OrderItem> orderItems, List<CartItem> cartItems, List<BookAuthor> bookAuthors) {
		super();
		this.id = id;
		this.name = name;
		this.costPrice = costPrice;
		this.sellPrice = sellPrice;
		this.category = category;
		this.publisher = publisher;
		this.description = description;
		this.status = status;
		this.detail = detail;
		this.quantity = quantity;
		this.bookImages = bookImages;
		this.orderItems = orderItems;
		this.cartItems = cartItems;
		this.bookAuthors = bookAuthors;
	}


}