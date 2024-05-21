package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.VoucherInfo;
import com.nhom14.webbookstore.repository.VoucherInfoRepository;
import com.nhom14.webbookstore.service.VoucherInfoService;
import com.nhom14.webbookstore.service.VoucherService;
import org.springframework.stereotype.Service;

@Service
public class VoucherInfoServiceImpl implements VoucherInfoService {
    private VoucherInfoRepository voucherInfoRepository;

    public VoucherInfoServiceImpl(VoucherInfoRepository voucherInfoRepository) {
        super();
        this.voucherInfoRepository = voucherInfoRepository;
    }

    @Override
    public void save(VoucherInfo voucherInfo) {
        voucherInfoRepository.save(voucherInfo);
    }
}
