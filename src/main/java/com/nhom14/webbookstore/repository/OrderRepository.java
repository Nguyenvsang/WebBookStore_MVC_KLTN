package com.nhom14.webbookstore.repository;

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
			"(:orderId is null or o.id = :orderId) and " +
			"(:accountId is null or o.account.id = :accountId) and " +
			"(:dateOrder is null or o.dateOrder = :dateOrder) and " +
			"(:expectedDeliveryDate1 is null or o.expectedDeliveryDate1 = :expectedDeliveryDate1) and " +
			"(:expectedDeliveryDate2 is null or o.expectedDeliveryDate2 = :expectedDeliveryDate2) and " +
			"(:deliveryDate is null or o.deliveryDate = :deliveryDate) and " +
			"(:totalPrice is null or o.totalPrice = :totalPrice) and " +
			"(:name is null or o.name = :name) and " +
			"(:address is null or o.address = :address) and " +
			"(:phoneNumber is null or o.phoneNumber = :phoneNumber) and " +
			"(:email is null or o.email = :email) and " +
			"(:status is null or o.status = :status) and " +
			"(:totalPriceMin is null or o.totalPrice >= :totalPriceMin) and " +
			"(:totalPriceMax is null or o.totalPrice <= :totalPriceMax)")
	Page<Order> findWithFilters(@Param("orderId") Integer orderId,
								@Param("accountId") Integer accountId,
								@Param("dateOrder") LocalDate dateOrder,
								@Param("expectedDeliveryDate1") LocalDate expectedDeliveryDate1,
								@Param("expectedDeliveryDate2") LocalDate expectedDeliveryDate2,
								@Param("deliveryDate") LocalDate deliveryDate,
								@Param("totalPrice") Double totalPrice,
								@Param("name") String name,
								@Param("address") String address,
								@Param("phoneNumber") String phoneNumber,
								@Param("email") String email,
								@Param("status") Integer status,
								@Param("totalPriceMin") Double totalPriceMin,
								@Param("totalPriceMax") Double totalPriceMax,
								Pageable pageable);

}
