package com.nhom14.webbookstore.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "first_name", columnDefinition = "varchar(255)")
	private String firstName;
	@Column(name = "last_name", columnDefinition = "varchar(255)")
	private String lastName;
	@Column(name = "username", columnDefinition = "varchar(255) NOT NULL")
	private String username;
	@Column(name = "password", columnDefinition = "varchar(255) NOT NULL")
	private String password;
	@Column(name = "gender", columnDefinition = "varchar(255)")
	private String gender;
	@Column(name = "img", columnDefinition = "char(255)")
	private String img;
	@Column(name = "date_of_birth", columnDefinition = "DATE")
	private Date dateOfBirth;
	@Column(name = "address", columnDefinition = "VARCHAR(255) NOT NULL")
	private String address;
	@Column(name = "phone_number", columnDefinition = "varchar(255) NOT NULL")
	private String phoneNumber;
	@Column(name = "email", columnDefinition = "char(255) NOT NULL")
	private String email;
	@Column(name = "account_type", columnDefinition = "INT NOT NULL")
	private int accountType; //customer:1, admin:0
	@Column(name = "status", columnDefinition = "INT NOT NULL")
	private int status; //hoạt động:1, ngừng hd:0

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "account")
	private Cart cart;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
	private List<Order> order;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
	private List<BookReview> bookReviews;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
	private List<BookReviewLike> bookReviewLikes;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
	private List<FavoriteBook> favoriteBooks;

	// Các phương thức getter, setter, constructor, toString...

	public Account() {
	    // default constructor
	}

	public Account(String username, String password, String address, String phoneNumber, String email, int accountType, int status) {
		super();
		this.username = username;
		this.password = password;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.accountType = accountType;
		this.status = status;
	}

}
