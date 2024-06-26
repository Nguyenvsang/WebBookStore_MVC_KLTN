package com.nhom14.webbookstore.controller.admin;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminOrderController {
	private OrderService orderService;
	private BookService bookService;
	private PaymentStatusService paymentStatusService;
	private OrderItemService orderItemService;
	private BookImportService bookImportService;
	private RevenueService revenueService;
	private ProfitService profitService;
	private InfoReturnOrderService infoReturnOrderService;
	private NotificationService notificationService;

	@Autowired
	public AdminOrderController(OrderService orderService, BookService bookService,
								PaymentStatusService paymentStatusService, OrderItemService orderItemService, BookImportService bookImportService, RevenueService revenueService, ProfitService profitService, InfoReturnOrderService infoReturnOrderService, NotificationService notificationService) {
		super();
		this.orderService = orderService;
		this.bookService = bookService;
		this.paymentStatusService = paymentStatusService;
		this.orderItemService = orderItemService;
		this.bookImportService = bookImportService;
		this.revenueService = revenueService;
		this.profitService = profitService;
		this.infoReturnOrderService = infoReturnOrderService;
		this.notificationService = notificationService;
	}

	@GetMapping("/manageorders")
	public String manageOrders(
			@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			@RequestParam(value = "dateOrderStr", required = false) String dateOrderStr,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "sortOption", required = false, defaultValue = "dateOrder_desc") String sortOption,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(value = "paymentStatus", required = false) Integer paymentStatus,
			@RequestParam(value = "isCompleted", required = false) Integer isCompleted,
			Model model,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		Account admin = (Account) session.getAttribute("admin");

		if (admin == null) {
			return "redirect:/loginadmin";
		}

		Sort sort = Sort.unsorted();
		if (sortOption != null) {
			if ("dateOrder_asc".equals(sortOption)) {
				sort = sort.and(Sort.by("dateOrder").ascending());
			} else if ("dateOrder_desc".equals(sortOption)) {
				sort = sort.and(Sort.by("dateOrder").descending());
			}
		}

		Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

		LocalDate dateOrder = parseDate(dateOrderStr, redirectAttributes);

		Page<Order> orders = orderService.getFilteredOrders(null, searchKeyword, dateOrder, status, paymentStatus, isCompleted, pageable);

		// Tự động cập nhật mức độ hoàn thành đơn hàng
		for (Order order : orders) {
			if (order.getStatus() == 4 &&
					(order.getPaymentStatus().getStatus() == 3 || order.getPaymentStatus().getStatus() == 4)
					&& order.getIsCompleted() != 2) {
				// Nếu đơn hàng đã được hủy và đã được hoàn tiền, hoặc không cần thanh toán
				order.setIsCompleted(2); // Đơn hàng đã hủy thành công
			}

			if (order.getStatus() == 7 &&
					(order.getPaymentStatus().getStatus() == 3)
					&& order.getIsCompleted() != 3) {
				// Nếu đơn hàng đã trả hàng thành công (kèm theo đã được hoàn tiền)
				order.setIsCompleted(3); // Đơn hàng đã trả thành công
			}

			// Tính số ngày giữa ngày giao hàng và ngày hiện tại
			long daysBetween = -1; // Tức là chưa giao hàng
			if (order.getDeliveryDate() != null) {
				LocalDateTime dateOrderLocalDateTime = order.getDeliveryDate().toLocalDateTime();
				LocalDateTime today = LocalDateTime.now();
				daysBetween = ChronoUnit.DAYS.between(dateOrderLocalDateTime.toLocalDate(), today.toLocalDate());

				// Nếu đã giao hàng và quá 15 ngày
				if (daysBetween != -1 && daysBetween > 15) {
					if ((order.getStatus() == 3 || order.getStatus() == 10) &&
							(order.getPaymentStatus().getStatus() == 1)
							&& order.getIsCompleted() != 1) {
						// Nếu đơn hàng là đã giao hoặc đã được nhận bởi khách hàng và đã được thanh toán
						order.setIsCompleted(1); // Đơn hàng đã hoàn thành
						// Kiểm tra xem Order đã tồn tại Revenue chưa
						Revenue existingRevenue = revenueService.getRevenueByOrder(order);
						if (existingRevenue == null) {
							// Tính toán doanh thu
							double revenueAmount = order.getTotalPrice();

							// Tạo bản ghi doanh thu mới
							Revenue revenue = new Revenue();
							revenue.setOrder(order);
							revenue.setRevenue(revenueAmount);
							revenue.setDate(new Timestamp(System.currentTimeMillis()));
							revenueService.addRevenue(revenue);
						}

						// Lấy tất cả các OrderItem của Order
						List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);

						for (OrderItem orderItem : orderItems) {
							// Kiểm tra xem OrderItem đã tồn tại Profit chưa
							Profit existingProfit = profitService.getProfitByOrderItem(orderItem);
							if (existingProfit == null) {
								// Lấy Book tương ứng với OrderItem
								Book book = orderItem.getBook();

								// Lấy đợt nhập sách đang bán của Book
								BookImport bookImport = bookImportService.getBookImportByBookAndStatus(book, 1);

								if (bookImport == null) {
									redirectAttributes.addAttribute("message", "Lỗi khi đang tự động cập nhật mức độ hoàn thành cho đơn hàng: Không tìm thấy đợt nhập sách!");
									// Chuyển hướng về trang manageorderitems
									return "redirect:/manageorderitems?orderId=" + order.getId();
								}

								// Kiểm tra xem số lượng còn lại có đủ để đáp ứng yêu cầu của OrderItem hay không
								if (bookImport.getRemainingQuantity() >= orderItem.getQuantity()) {
									// Trừ remaining_quantity và cập nhật BookImport
									bookImport.setRemainingQuantity(bookImport.getRemainingQuantity() - orderItem.getQuantity());
									bookImportService.updateBookImport(bookImport);
								} else {
									// Xử lý trường hợp không đủ sách...
									redirectAttributes.addAttribute("message", "Lỗi khi đang tự động cập nhật mức độ hoàn thành cho đơn hàng, vui lòng thử lại sau!" + bookImport.getRemainingQuantity() + "----------" + orderItem.getQuantity());
									// Chuyển hướng về trang manageorderitems
									return "redirect:/manageorderitems?orderId=" + order.getId();
								}

								orderItem.setCostPrice(bookImport.getImportPrice());
								orderItemService.updateOrderItem(orderItem);

								// Tính toán lợi nhuận
								double profitAmount = (orderItem.getSellPrice() - bookImport.getImportPrice()) * orderItem.getQuantity();

								// Tạo bản ghi lợi nhuận mới
								Profit profit = new Profit();
								profit.setOrderItem(orderItem);
								profit.setCostPrice(bookImport.getImportPrice());
								profit.setSellPrice(orderItem.getSellPrice());
								profit.setProfit(profitAmount);
								profit.setDate(new Timestamp(System.currentTimeMillis()));
								profitService.addProfit(profit);
							}
						}
					}
				}
			}
			orderService.updateOrder(order);
		}

		// Đặt danh sách đơn hàng vào thuộc tính model để sử dụng trong View
		model.addAttribute("orders", orders);
		model.addAttribute("totalAllOrders", orderService.getAllOrders().size());

		// Thêm các bộ lọc nếu có để dùng cho trang tiếp theo
		Map<String, Object> params = new HashMap<>();
		params.put("searchKeyword", searchKeyword);
		params.put("dateOrderStr", dateOrderStr);
		params.put("status", status);
		params.put("paymentStatus", paymentStatus);
		params.put("isCompleted", isCompleted);
		params.put("sortOption", sortOption);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (entry.getValue() != null) {
				model.addAttribute(entry.getKey(), entry.getValue());
			}
		}

		return "admin/manageorders";
	}

	private LocalDate parseDate(String dateStr, RedirectAttributes redirectAttributes) {
		if (dateStr != null && !dateStr.isEmpty()) {
			try {
				return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} catch (DateTimeParseException e) {
				// Xử lý trường hợp khi dateStr không phải là một ngày hợp lệ
				// Ví dụ: hiển thị thông báo lỗi
				redirectAttributes.addAttribute("message", "Ngày không hợp lệ!");

				return null;
			}
		}
		return null;
	}


	@PostMapping("/updateorderstatus")
	public String updateOrderStatus(@RequestParam("orderId") int orderId,
									@RequestParam("status") int status,
									HttpSession session,
									RedirectAttributes redirectAttributes) {
		Account admin = (Account) session.getAttribute("admin");

		// Kiểm tra xem admin đã đăng nhập hay chưa
		if (admin == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/loginadmin";
		}

		// Lấy đơn hàng từ Service
		Order order = orderService.getOrderById(orderId);

		// Nếu là trạng thái đã giao thì thêm ngày giao hàng vô
		// Kèm theo thanh toán sẽ tự động chuyển sang Đã thanh toán
		if (status == 3) {
			order.setDeliveryDate(new Timestamp(System.currentTimeMillis()));
			// Lấy trạng thái thanh toán dựa trên order
			PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
			// Cập nhật trạng thái thanh toán mới
			paymentStatus.setStatus(1); // Đã thanh toán
			paymentStatusService.updatePaymentStatus(paymentStatus);
		}

		//  Nếu là trạng thái xử lý trả hàng thì TTTT tự động chuyển qua xử lý hoàn tiền
		if (status == 6) {
			// Lấy trạng thái thanh toán dựa trên order
			PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
			// Cập nhật trạng thái thanh toán mới
			paymentStatus.setStatus(2); // Xử lý hoàn tiền
			paymentStatusService.updatePaymentStatus(paymentStatus);
		}

		// Nếu là Trả hàng thành công thì TTTT tự động chuyển qua Đã hoàn tiền
		// Duyệt qua từng món hàng để trả lại số lượng sách
		// Lưu ngày trả hàng thành công vào
		if (status == 7) {
			// Lấy danh sách OrderItem từ OrderItemService
			List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);
			// Duyệt qua từng món hàng để trả lại số lượng sách
			for (OrderItem orderItem : orderItems) {
				Book book = orderItem.getBook();
				// Cập nhật số lượng sách và kiểm tra trạng thái
				int remainingQuantity = book.getQuantity() + orderItem.getQuantity();
				if (remainingQuantity > 0 && book.getStatus() == 0) {
					book.setStatus(1);
					book.setQuantity(remainingQuantity);
				} else {
					book.setQuantity(remainingQuantity);
				}
				bookService.updateBook(book);
			}
			// Lấy trạng thái thanh toán dựa trên order
			PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
			// Cập nhật trạng thái thanh toán mới
			paymentStatus.setStatus(3); // Đã hoàn tiền
			paymentStatusService.updatePaymentStatus(paymentStatus);

			// Lấy thông tin trả hàng theo order để lưu ngày trả hàng thành công, lưu vào CSDL
			InfoReturnOrder infoReturnOrder = infoReturnOrderService.getInfoReturnOrderByOrder(order);
			infoReturnOrder.setReturnDate(new Timestamp(System.currentTimeMillis()));
			infoReturnOrderService.updateInfoReturnOrder(infoReturnOrder);
		}

		// Cập nhật trạng thái đơn hàng
		order.setStatus(status);

		// Cập nhật đơn hàng thông qua Service
		orderService.updateOrder(order);

		Notification notification = new Notification();
		// Chỗ này là chuỗi ghi nội dung ví dụ:
		// Đơn hàng mã 10 của bạn đang chờ lấy hàng
		String content = createNotificationContent(orderId, status, paymentStatusService.getPaymentStatusByOrder(order));
		notification.setContent(content);
		notification.setStatus(0);// Chưa đọc
		notification.setType(0); // :Loại thông báo về order
		notification.setReferredId(orderId);
		notification.setReceiver(order.getAccount());
		notification.setTriggerUser(admin);
		notification.setSentTime(new Timestamp(System.currentTimeMillis()));
		notification = notificationService.save(notification);
		// Chuyển hướng về trang manageorderitems
		return "redirect:/manageorderitems?orderId=" + orderId;
	}

	public String createNotificationContent(int orderId, int status, PaymentStatus paymentStatus) {
		String content = "";
		switch (status) {
			case 0:
				content = "Đơn hàng mã " + orderId + " của bạn đang chờ xác nhận.";
				break;
			case 1:
				content = "Đơn hàng mã " + orderId + " của bạn đang chờ lấy hàng.";
				break;
			case 2:
				content = "Đơn hàng mã " + orderId + " của bạn đang được giao.";
				break;
			case 3:
				content = "Đơn hàng mã " + orderId + " của bạn đã được giao.";
				break;
			case 4:
				content = "Đơn hàng mã " + orderId + " của bạn đã bị hủy.";
				break;
			case 5:
				content = "Đơn hàng mã " + orderId + " của bạn đã yêu cầu trả hàng.";
				break;
			case 6:
				content = "Đơn hàng mã " + orderId + " của bạn đang được xử lý trả hàng.";
				break;
			case 7:
				content = "Đơn hàng mã " + orderId + " của bạn đã được trả hàng thành công.";
				break;
			case 8:
				content = "Yêu cầu trả hàng của đơn hàng mã " + orderId + " đã bị từ chối.";
				break;
			case 9:
				content = "Đơn hàng mã " + orderId + " không được bạn nhận hàng.";
				break;
			case 10:
				content = "Đơn hàng mã " + orderId + " đã được bạn nhận hàng.";
				break;
		}

		switch (paymentStatus.getStatus()) {
			case 0:
				content += " Thanh toán chưa được thực hiện.";
				break;
			case 1:
				content += " Thanh toán đã được thực hiện.";
				break;
			case 2:
				content += " Đang xử lý hoàn tiền.";
				break;
			case 3:
				content += " Đã hoàn tiền.";
				break;
			case 4:
				content += " Không cần thanh toán.";
				break;
			case 5:
				content += " Chờ thanh toán lại.";
				break;
		}
		return content;
	}

	@PostMapping("/updatepaymentstatus")
	public String updatePaymentStatus(@RequestParam("orderId") int orderId,
									  @RequestParam("paymentstatus") int paymentstatus,
									  HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

		// Kiểm tra xem admin đã đăng nhập hay chưa
		if (admin == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/loginadmin";
		}

		// Lấy đơn hàng từ Service
		Order order = orderService.getOrderById(orderId);
		// Lấy trạng thái thanh toán dựa trên order
		PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
		// Cập nhật trạng thái thanh toán mới
		paymentStatus.setStatus(paymentstatus);
		paymentStatusService.updatePaymentStatus(paymentStatus);

		// Chuyển hướng về trang manageorderitems
		return "redirect:/manageorderitems?orderId=" + orderId;
	}

	@PostMapping("/updateiscompleted")
	public String updateIsCompleted(@RequestParam("orderId") int orderId,
									@RequestParam("iscompleted") int iscompleted,
									RedirectAttributes redirectAttributes,
									HttpSession session) {
		Account admin = (Account) session.getAttribute("admin");

		// Kiểm tra xem admin đã đăng nhập hay chưa
		if (admin == null) {
			// Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
			return "redirect:/loginadmin";
		}

		// Lấy đơn hàng từ Service
		Order order = orderService.getOrderById(orderId);
		// Xử lý từng loại iscompleted cho phù hợp
		if (iscompleted == 1){
			// Nếu Admin chọn "Đơn hàng đã hoàn thành" thì sẽ tính doanh thu, lợi nhuận
			// hệ thống sẽ tự động cập nhật trạng thái thanh toán của đơn hàng đó thành 1 (đã thanh toán).
			// Kiểm tra xem Order đã tồn tại Revenue chưa
			Revenue existingRevenue = revenueService.getRevenueByOrder(order);
			if (existingRevenue == null) {
				// Tính toán doanh thu
				double revenueAmount = order.getTotalPrice();

				// Tạo bản ghi doanh thu mới
				Revenue revenue = new Revenue();
				revenue.setOrder(order);
				revenue.setRevenue(revenueAmount);
				revenue.setDate(new Timestamp(System.currentTimeMillis()));
				revenueService.addRevenue(revenue);
			}

			// Lấy tất cả các OrderItem của Order
			List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);

			for (OrderItem orderItem : orderItems) {
				// Kiểm tra xem OrderItem đã tồn tại Profit chưa
				Profit existingProfit = profitService.getProfitByOrderItem(orderItem);
				if (existingProfit == null) {
					// Lấy Book tương ứng với OrderItem
					Book book = orderItem.getBook();

					// Lấy đợt nhập sách đang bán của Book
					BookImport bookImport = bookImportService.getBookImportByBookAndStatus(book, 1);

					if (bookImport == null){
						redirectAttributes.addAttribute("message", "Không tìm thấy đợt nhập sách!");
						// Chuyển hướng về trang manageorderitems
						return "redirect:/manageorderitems?orderId=" + orderId;
					}

					// Kiểm tra xem số lượng còn lại có đủ để đáp ứng yêu cầu của OrderItem hay không
					if (bookImport.getRemainingQuantity() >= orderItem.getQuantity()) {
						// Trừ remaining_quantity và cập nhật BookImport
						bookImport.setRemainingQuantity(bookImport.getRemainingQuantity() - orderItem.getQuantity());
						bookImportService.updateBookImport(bookImport);
					} else {
						// Xử lý trường hợp không đủ sách...
						redirectAttributes.addAttribute("message", "Có lỗi xảy ra, vui lòng thử lại sau!" + bookImport.getRemainingQuantity() +"----------" + orderItem.getQuantity());
						// Chuyển hướng về trang manageorderitems
						return "redirect:/manageorderitems?orderId=" + orderId;
					}

					orderItem.setCostPrice(bookImport.getImportPrice());
					orderItemService.updateOrderItem(orderItem);

					// Tính toán lợi nhuận
					double profitAmount = (orderItem.getSellPrice() - bookImport.getImportPrice()) * orderItem.getQuantity();

					// Tạo bản ghi lợi nhuận mới
					Profit profit = new Profit();
					profit.setOrderItem(orderItem);
					profit.setCostPrice(bookImport.getImportPrice());
					profit.setSellPrice(orderItem.getSellPrice());
					profit.setProfit(profitAmount);
					profit.setDate(new Timestamp(System.currentTimeMillis()));
					profitService.addProfit(profit);
				}
			}
			// Lấy trạng thái thanh toán dựa trên order
			PaymentStatus paymentStatus = paymentStatusService.getPaymentStatusByOrder(order);
			// Cập nhật trạng thái thanh toán mới
			paymentStatus.setStatus(1); // Đã thanh toán
			paymentStatusService.updatePaymentStatus(paymentStatus);
		}
		// Cập nhật trạng thái hoàn thành của đơn hàng
		order.setIsCompleted(iscompleted);
		orderService.updateOrder(order);

		// Chuyển hướng về trang manageorderitems
		return "redirect:/manageorderitems?orderId=" + orderId;
	}
}