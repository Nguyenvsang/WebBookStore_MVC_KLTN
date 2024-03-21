package com.nhom14.webbookstore.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	@Column(name = "name", columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;
	@Column(name = "status", columnDefinition = "INT NOT NULL")
    private int status; //hoạt động:1, ngừng kd:0
	//bi-directional one-to-many association with Book (de quen)
    @OneToMany(mappedBy = "category")
    private List<Book> books;

    public Category() {
    }

    public Category(String name, int status) {
        this.name = name;
        this.status = status;
    }


}