package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Profit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfitRepository extends JpaRepository<Profit, Long> {
}
