package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.BookPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookPriceRepository extends JpaRepository<BookPrice, Long> {
}
