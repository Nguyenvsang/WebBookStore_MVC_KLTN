package com.nhom14.webbookstore.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

	// Phương thức để thêm một đơn hàng
	void addOrder(Order order);

	// Lấy đơn hàng cuối cùng theo account
	Order getLastOrder(Account account);

	// Phương thức lấy những đơn hàng theo mã người dùng
	List<Order> getOrdersByAccount(Account account);

	// Lấy đơn hàng theo Id
	Order getOrderById(int orderId);

	// Phương thức để lấy danh sách tất cả các đơn hàng
	List<Order> getAllOrders();

	// Lấy đơn hàng theo mã trạng thái
	List<Order> getOrdersByStatus(int statusId);

	// Lấy đơn hàng theo từ khóa
	List<Order> searchOrdersByKeyword(List<Order> orders, String searchKeyword);

	// Phương thức để cập nhật một đơn hàng
	void updateOrder(Order order);

	// Lấy danh sách đơn hàng với các tham số tìm kiếm và lọc, phân trang
	Page<Order> getFilteredOrders(Integer accountId, String searchKeyword, LocalDate dateOrder, Integer status, Integer paymentStatus, Integer isCompleted, Pageable pageable);
}
