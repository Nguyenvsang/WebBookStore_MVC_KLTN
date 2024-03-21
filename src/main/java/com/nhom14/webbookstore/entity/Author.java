package com.nhom14.webbookstore.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "author")
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name", columnDefinition = "varchar(255) NOT NULL")
	private String name;
	@Column(name = "bio", columnDefinition = "varchar(255)")
	private String bio;
	
	//bi-directional one-to-many association to BookAuthor
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "author")
	private List<BookAuthor> bookAuthors;

	public Author() {
	}

	public Author(String name, String bio) {
		this.name = name;
		this.bio = bio;
	}


}
