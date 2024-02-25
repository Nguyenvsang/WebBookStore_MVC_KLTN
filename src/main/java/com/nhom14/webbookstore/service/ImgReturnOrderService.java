package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.ImgReturnOrder;
import com.nhom14.webbookstore.entity.InfoReturnOrder;

public interface ImgReturnOrderService {
    // Phương thức để thêm một nhóm hình ảnh cho thông tin hủy đơn
    void addImgReturnOrder(ImgReturnOrder imgReturnOrder);

    // Phương thức để cập nhật một nhóm hình ảnh cho thông tin hủy đơn
    void updateImgReturnOrder(ImgReturnOrder imgReturnOrder);

    // Lấy hình ảnh trả hàng theo thông tin trả hàng
    ImgReturnOrder getImgReturnOrderByInfoReturnOrder(InfoReturnOrder infoReturnOrder);
}
