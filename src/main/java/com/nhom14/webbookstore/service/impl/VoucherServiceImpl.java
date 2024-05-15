package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Voucher;
import com.nhom14.webbookstore.repository.VoucherRepository;
import com.nhom14.webbookstore.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherServiceImpl implements VoucherService {
    private VoucherRepository voucherRepository;

    @Autowired
    public VoucherServiceImpl(VoucherRepository voucherRepository) {
        super();
        this.voucherRepository = voucherRepository;
    }

    @Override
    public Voucher getVoucherByCode(String code) {
        return voucherRepository.findByCode(code); // Không có sẽ trả về null
    }

    @Override
    public void saveVoucher(Voucher voucher) {
        voucherRepository.save(voucher);
    }

    @Override
    public Voucher getVoucherById(long id) {
        return voucherRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Voucher> getFilteredVouchers(Integer categoryId, String searchKeyword, Integer voucherScope, Integer status, Pageable pageable) {
        // Chuyển để tìm kiếm theo id voucher
        Integer searchKeywordAsInteger = null;
        try {
            searchKeywordAsInteger = Integer.parseInt(searchKeyword);
        } catch (NumberFormatException e) {
            // searchKeyword không phải là một số nguyên, không làm gì cả
        }
        if (searchKeywordAsInteger != null) {
            return voucherRepository.findWithFilters(categoryId, null, searchKeywordAsInteger, voucherScope, status, pageable);
        } else {
            return voucherRepository.findWithFilters(categoryId, searchKeyword, null, voucherScope, status, pageable);
        }
        // Không tìm thấy sẽ trả về một đối tượng Page<Book> rỗng
        // nghĩa là .getTotalElements() = 0
    }

    @Override
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    @Override
    public Voucher getLatestActiveVoucherByCategoryId(int categoryId) {
        List<Voucher> vouchers = voucherRepository.findActiveVouchersByCategoryId(categoryId, LocalDateTime.now(), PageRequest.of(0, 1));
        if (!vouchers.isEmpty()) {
            return vouchers.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Voucher> findOverlappingVouchers(Integer categoryId, Timestamp startDate, Timestamp endDate) {
        return voucherRepository.findOverlappingVouchers(categoryId, startDate, endDate);
    }
}
