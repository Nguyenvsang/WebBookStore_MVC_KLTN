package com.nhom14.webbookstore.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhom14.webbookstore.entity.Account;
import com.nhom14.webbookstore.entity.Order;
import com.nhom14.webbookstore.repository.OrderRepository;
import com.nhom14.webbookstore.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private OrderRepository orderRepository;

	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository) {
		super();
		this.orderRepository = orderRepository;
	}

	@Override
	public void addOrder(Order order) {
		orderRepository.save(order);
	}

	@Override
	public Order getLastOrder(Account account) {
		return orderRepository.findFirstByAccountOrderByIdDesc(account);
	}

	@Override
	public List<Order> getOrdersByAccount(Account account) {
		return orderRepository.findByAccount(account);
	}

	@Override
	public Order getOrderById(int orderId) {
		Optional<Order> order = orderRepository.findById(orderId);
	    return order.orElse(null);
	}

	@Override
	public List<Order> getAllOrders() {
		return orderRepository.findAllByOrderByDateOrderDesc(); // Sắp xếp theo ngày giảm dần
		// Nếu không có order sẽ trả về empty list
	}

	@Override
	public List<Order> getOrdersByStatus(int statusId) {
		return orderRepository.findByStatus(statusId);
	}

	@Override
	public List<Order> searchOrdersByKeyword(List<Order> orders, String searchKeyword) {
		List<Order> result = new ArrayList<>();
	    String lowercaseKeyword = searchKeyword.toLowerCase().trim();

	    for (Order order : orders) {
	        if (containsIgnoreCase(Integer.toString(order.getId()), lowercaseKeyword)
	                || containsIgnoreCase(order.getDateOrder().toString(), lowercaseKeyword)
	                || containsIgnoreCase(Double.toString(order.getTotalPrice()), lowercaseKeyword)
	                || containsIgnoreCase(order.getName(), lowercaseKeyword)
	                || containsIgnoreCase(order.getAddress(), lowercaseKeyword)
	                || containsIgnoreCase(order.getPhoneNumber(), lowercaseKeyword)
	                || containsIgnoreCase(order.getEmail(), lowercaseKeyword)
	                || containsIgnoreCase(Integer.toString(order.getAccount().getId()), lowercaseKeyword)
	                || containsIgnoreCase(Integer.toString(order.getStatus()), lowercaseKeyword)) {
	            result.add(order);
	        }
	    }

	    return result;
	}
	
	// Kiểm tra xem một chuỗi có chứa một chuỗi con cụ thể hay không,
	// mà không phân biệt chữ hoa chữ thường trong quá trình so sánh
	private boolean containsIgnoreCase(String text, String keyword) {
		return text.toLowerCase().contains(keyword);
	}

	@Override
	public void updateOrder(Order order) {
		orderRepository.save(order);
	}

	@Override
	public Page<Order> getFilteredOrders(Integer accountId, String searchKeyword, LocalDate dateOrder, Integer status, Integer paymentStatus, Integer isCompleted, Pageable pageable) {
		Integer searchKeywordAsInteger = null;
		try {
			searchKeywordAsInteger = Integer.parseInt(searchKeyword);
		} catch (NumberFormatException e) {
			// searchKeyword không phải là một số nguyên, không làm gì cả
		}
		if (searchKeywordAsInteger != null) {
			return orderRepository.findWithFilters(accountId, null, searchKeywordAsInteger, dateOrder, status, paymentStatus, isCompleted, pageable);
		} else {
			return orderRepository.findWithFilters(accountId, searchKeyword, null, dateOrder, status, paymentStatus, isCompleted, pageable);
		}
	}


}
