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
@Table(name = "bookimage")
public class BookImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	//bi-directional many-to-one association with Book
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "book_id")
	private Book book;
	
	@Column(name = "name", columnDefinition = "varchar(255) NOT NULL")
	private String name;
	
	@Column(name = "position", columnDefinition = "INT NOT NULL")
	private int position;
	
	@Column(name = "path", columnDefinition = "varchar(255) NOT NULL")
	private String path;

	public BookImage() {
	}

	public BookImage(Book book, String name, int position, String path) {
		this.book = book;
		this.name = name;
		this.position = position;
		this.path = path;
	}


}
