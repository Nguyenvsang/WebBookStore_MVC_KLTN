package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.BookImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookImportRepository extends JpaRepository<BookImport, Long> {
}
