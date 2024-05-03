package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.Ward;
import com.nhom14.webbookstore.repository.WardRepository;
import com.nhom14.webbookstore.service.WardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WardServiceImpl implements WardService {

    private WardRepository wardRepository;

    @Autowired
    public WardServiceImpl(WardRepository wardRepository) {
        super();
        this.wardRepository = wardRepository;
    }

    @Override
    public List<Ward> getAllWards() {
        return wardRepository.findAll();
    }

    @Override
    public List<Ward> getWardsByDistrictId(int districtId) {
        return wardRepository.findByDistrictId(districtId);
    }

    @Override
    public Ward getWardById(int id) {
        return wardRepository.findById(id).orElse(null);
    }
}
