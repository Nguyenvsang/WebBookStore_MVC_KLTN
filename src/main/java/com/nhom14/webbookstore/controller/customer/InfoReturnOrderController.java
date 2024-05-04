package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Controller
public class InfoReturnOrderController {

    private InfoReturnOrderService infoReturnOrderService;
    private OrderService orderService;
    private PaymentStatusService paymentStatusService;
    private CloudinaryService cloudinaryService;
    private ImgReturnOrderService imgReturnOrderService;
    private VideoReturnOrderService videoReturnOrderService;
    private AccountAddressService accountAddressService;

    @Autowired
    public InfoReturnOrderController(InfoReturnOrderService infoReturnOrderService,
                                     OrderService orderService,
                                     PaymentStatusService paymentStatusService,
                                     CloudinaryService cloudinaryService,
                                     ImgReturnOrderService imgReturnOrderService,
                                     VideoReturnOrderService videoReturnOrderService, AccountAddressService accountAddressService) {
        this.infoReturnOrderService = infoReturnOrderService;
        this.orderService = orderService;
        this.paymentStatusService = paymentStatusService;
        this.cloudinaryService = cloudinaryService;
        this.imgReturnOrderService = imgReturnOrderService;
        this.videoReturnOrderService = videoReturnOrderService;
        this.accountAddressService = accountAddressService;
    }

    @GetMapping("/returnorder")
    public String showReasonReturnOrder(@RequestParam int orderId,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        Order order = orderService.getOrderById(orderId);

        // Kiểm xem order có thuộc về người dùng đang đăng nhập không
        if(order.getAccount().getId()!=account.getId()){
            // Thêm thông báo lỗi
            redirectAttributes.addAttribute("message", "Có lỗi xảy ra vui lòng thử lại sau!");
            return "redirect:/customer/error";
        }

        // Lấy tất cả các địa chỉ để hiển thị trong pop up lựa chọn địa chỉ
        List<AccountAddress> addresses = accountAddressService.getAddressesByAccount(account);

        model.addAttribute("order", order);
        model.addAttribute("account", account);
        model.addAttribute("addresses", addresses);
        return "customer/reasonreturnorder";
    }

    @PostMapping("/returnorder")
    public String returnOrder(@RequestParam("orderId") int orderId,
                              @RequestParam("reason") String reason,
                              @RequestParam("detailreason") String detailReason,
                              @RequestParam("images") List<MultipartFile> images,
                              @RequestParam("video1") MultipartFile video1,
                              @RequestParam("video2") MultipartFile video2,
                              @RequestParam("name") String name,
                              @RequestParam("address") String address,
                              @RequestParam("phoneNumber") String phoneNumber,
                              @RequestParam("email") String email,
                                        HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        // Lấy đối tượng Order từ OrderService bằng orderId
        Order order = orderService.getOrderById(orderId);
        // Đặt trạng thái đơn hàng là "Yêu cầu trả hàng"
        order.setStatus(5); // Yêu cầu trả hàng
        // Lưu vào CSDL
        orderService.updateOrder(order);

        // Tạo đối tượng thông tin trả hàng
        InfoReturnOrder newInfoReturnOrder = new InfoReturnOrder();
        newInfoReturnOrder.setOrder(order);
        newInfoReturnOrder.setReason(reason);
        newInfoReturnOrder.setDetailReason(detailReason);
        newInfoReturnOrder.setRequestDate(new Timestamp(System.currentTimeMillis()));
        newInfoReturnOrder.setName(name);
        newInfoReturnOrder.setAddress(address);
        newInfoReturnOrder.setPhoneNumber(phoneNumber);
        newInfoReturnOrder.setEmail(email);
        // Lưu vào CSDL
        infoReturnOrderService.addInfoReturnOrder(newInfoReturnOrder);

        // Lấy thông tin trả hàng theo order để có được id
        newInfoReturnOrder = infoReturnOrderService.getInfoReturnOrderByOrder(order);

        // Tạo đối tượng ImgReturnOrder để chứa ảnh nếu chưa tồn tại
        ImgReturnOrder imgReturnOrder = imgReturnOrderService.getImgReturnOrderByInfoReturnOrder(newInfoReturnOrder);
        if(imgReturnOrder == null){
            imgReturnOrder = new ImgReturnOrder();
        }
        imgReturnOrder.setInfoReturnOrder(newInfoReturnOrder); // Không cần truy xuất ID mỗi imgReturnOrder từ database
        // vì khi đã addImgReturnOrder
        // thì Hibernate sẽ tự động tạo một ID cho đối tượng này.
        // ID này sau đó sẽ được Hibernate sử dụng để theo dõi và quản lý đối tượng trong phiên làm việc hiện tại

        // Thêm ảnh vào ImgReturnOrder
        boolean success = addImages(order, imgReturnOrder, images);
        // Nếu thêm ảnh bị lỗi thì
        if(!success) {
            // Trở về trạng thái cũ là Đã giao
            order.setStatus(3); // Đã giao
            // Lưu vào CSDL
            orderService.updateOrder(order);
            redirectAttributes.addAttribute("message", "Đã xảy ra lỗi khi thêm ảnh cho thông tin trả hàng.");
            redirectAttributes.addAttribute("orderId", order.getId());
            return "redirect:/returnorder";
        }

        // Nếu không bị lỗi thì tiếp tục thực hiện các lệnh dưới
        // Tạo đối tượng VideoReturnOrder để chứa video nếu chưa tồn tại
        VideoReturnOrder videoReturnOrder = videoReturnOrderService.getVideoReturnOrderByInfoReturnOrder(newInfoReturnOrder);
        if(videoReturnOrder == null){
            videoReturnOrder = new VideoReturnOrder();
        }
        videoReturnOrder.setUnboxingVideo("url1");
        videoReturnOrder.setProductVideo("url2");
        videoReturnOrderService.addVideoReturnOrder(videoReturnOrder);
        boolean addVideos = addVideos(order, videoReturnOrder, video1, video2, true);
        // Nếu thêm video bị lỗi thì
        if(addVideos == false) {
            // Trở về trạng thái cũ là Đã giao
            order.setStatus(3); // Đã giao
            // Lưu vào CSDL
            orderService.updateOrder(order);
            redirectAttributes.addAttribute("message", "Đã xảy ra lỗi khi thêm video cho thông tin trả hàng.");
            redirectAttributes.addAttribute("orderId", order.getId());
            return "redirect:/returnorder";
        }
        // Nếu không bị lỗi thì tiếp tục thực hiện các lệnh dưới
        // Còn PaymentStatus sẽ được đặt là Đã hoàn tiền nếu Admin đặt trạng thái đơn hàng này thành Trả hàng thành công
        redirectAttributes.addAttribute("message", "Đã gửi yêu cầu trả hàng thành công! Vui lòng đợi chúng tôi liên hệ lại!");
        redirectAttributes.addAttribute("orderId", order.getId());
        return "redirect:/vieworderitems";
    }

    private boolean addImages(Order order, ImgReturnOrder imgReturnOrder, List<MultipartFile> images) {
        boolean success = true;
        try {
            // Tạo public ID cho hình ảnh trên Cloudinary (sử dụng id order)
            String publicId = "WebBookStoreKLTN/img_returnorder/order_" + order.getId();

            // Tạo một danh sách các nhiệm vụ tải lên ảnh
            List<Callable<String>> uploadTasks = new ArrayList<>();

            // Kiểm tra và tạo nhiệm vụ tải lên ảnh cho từng ảnh
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);
                final int index = i + 1;
                if (!image.isEmpty()) {
                    uploadTasks.add(() -> cloudinaryService.uploadImage(image, publicId + "/" + index));
                }
            }

            // Tạo một ExecutorService để thực hiện các nhiệm vụ tải lên ảnh song song
            ExecutorService executorService = Executors.newFixedThreadPool(uploadTasks.size());

            // Thực hiện các nhiệm vụ tải lên ảnh và lấy URL của ảnh đã tải lên
            List<Future<String>> uploadResults = executorService.invokeAll(uploadTasks);

            // Gán URL của ảnh đã tải lên cho các thuộc tính tương ứng trong imgReturnOrder
            for (int i = 0; i < uploadResults.size(); i++) {
                switch (i) {
                    case 0:
                        imgReturnOrder.setImg1(uploadResults.get(i).get());
                        break;
                    case 1:
                        imgReturnOrder.setImg2(uploadResults.get(i).get());
                        break;
                    case 2:
                        imgReturnOrder.setImg3(uploadResults.get(i).get());
                        break;
                    case 3:
                        imgReturnOrder.setImg4(uploadResults.get(i).get());
                        break;
                }
            }

            // Cập nhật vào CSDL
            imgReturnOrderService.updateImgReturnOrder(imgReturnOrder);

            // Đóng ExecutorService sau khi hoàn thành
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }


    private boolean addVideos(Order order, VideoReturnOrder videoReturnOrder, MultipartFile video1, MultipartFile video2,
                              boolean success) {
        try {
            // Tạo public ID cho video trên Cloudinary (sử dụng id order)
            String publicId = "WebBookStoreKLTN/video_returnorder/order_" + order.getId();

            // Tạo một danh sách các nhiệm vụ tải video
            List<Callable<String>> uploadTasks = new ArrayList<>();

            // Kiểm tra và tạo nhiệm vụ tải lên ảnh cho từng ảnh
            if (!video1.isEmpty()) {
                uploadTasks.add(() -> cloudinaryService.uploadVideo(video1, publicId + "/1"));
            }
            if (!video2.isEmpty()) {
                uploadTasks.add(() -> cloudinaryService.uploadVideo(video2, publicId + "/2"));
            }

            // Tạo một ExecutorService để thực hiện các nhiệm vụ tải lên video song song
            ExecutorService executorService = Executors.newFixedThreadPool(uploadTasks.size());

            // Thực hiện các nhiệm vụ tải lên video và lấy URL của video đã tải lên
            List<Future<String>> uploadResults = executorService.invokeAll(uploadTasks);

            videoReturnOrder.setUnboxingVideo(uploadResults.get(0).get());
            videoReturnOrder.setProductVideo(uploadResults.get(1).get());

            // Cập nhật vào CSDL
            videoReturnOrderService.updateVideoReturnOrder(videoReturnOrder);

            // Đóng ExecutorService sau khi hoàn thành
            executorService.shutdown();
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            return success;
        }
    }
}
