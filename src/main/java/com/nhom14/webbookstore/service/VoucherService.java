package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface VoucherService {
    // Phương thức lấy voucher theo mã code
    Voucher getVoucherByCode(String code);
    void saveVoucher(Voucher voucher);
    Voucher getVoucherById(long id);
    Page<Voucher> getFilteredVouchers(Integer categoryId, String searchKeyword, Integer voucherScope, Integer status, Pageable pageable);
    List<Voucher> getAllVouchers();
    Voucher getLatestActiveVoucherByCategoryId(int categoryId);
    List<Voucher> findOverlappingVouchers(Integer categoryId, Timestamp startDate, Timestamp endDate);
    // Phương thức lấy các Voucher đang còn giá trị
    List<Voucher> getActiveVouchers();
}
