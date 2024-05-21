package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.Voucher;
import com.nhom14.webbookstore.entity.VoucherInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherInfoRepository extends JpaRepository<VoucherInfo, Long> {
}
