package com.nhom14.webbookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhom14.webbookstore.entity.Author;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Author findByName(String name);
}
