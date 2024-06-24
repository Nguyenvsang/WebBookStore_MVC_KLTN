package com.nhom14.webbookstore.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

	Order findFirstByAccountOrderByIdDesc(Account account);

	List<Order> findByAccount(Account account);
	
	List<Order> findByStatus(int status);

	@Query("SELECT o FROM Order o WHERE " +
			"(:accountId is null or o.account.id = :accountId) and " +
			"(:searchKeywordAsInteger is null or o.id = :searchKeywordAsInteger) and " +
			"(:searchKeyword is null or lower(o.name) like lower(concat('%', :searchKeyword, '%'))) and " +
			"(:dateOrder is null or DATE(o.dateOrder) = :dateOrder) and " +
			"(:status is null or o.status = :status)")
	Page<Order> findWithFilters(@Param("accountId") Integer accountId,
								@Param("searchKeyword") String searchKeyword,
								@Param("searchKeywordAsInteger") Integer searchKeywordAsInteger,
								@Param("dateOrder") LocalDate dateOrder,
								@Param("status") Integer status,
								Pageable pageable);

	List<Order> findAllByOrderByDateOrderDesc();

}
