package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.InfoReturnOrder;
import com.nhom14.webbookstore.entity.VideoReturnOrder;

public interface VideoReturnOrderService {
    // Phương thức lấy video trả hàng theo thông tin trả hàng
    VideoReturnOrder getVideoReturnOrderByInfoReturnOrder(InfoReturnOrder infoReturnOrder);

    // Phương thức thêm một video trả hàng mới
    void addVideoReturnOrder(VideoReturnOrder videoReturnOrder);

    // Phương thức cập nhật một video trả hàng
    void updateVideoReturnOrder(VideoReturnOrder videoReturnOrder);
}
