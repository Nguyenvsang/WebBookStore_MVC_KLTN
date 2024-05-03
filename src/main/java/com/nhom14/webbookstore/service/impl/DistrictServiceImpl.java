package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.District;
import com.nhom14.webbookstore.repository.DistrictRepository;
import com.nhom14.webbookstore.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictServiceImpl implements DistrictService {

    private DistrictRepository districtRepository;

    @Autowired
    public DistrictServiceImpl(DistrictRepository districtRepository) {
        super();
        this.districtRepository = districtRepository;
    }

    @Override
    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    @Override
    public List<District> getDistrictsByCityId(int cityId) {
        return districtRepository.findByCityId(cityId);
    }

    @Override
    public District getDistrictById(int id) {
        return districtRepository.findById(id).orElse(null);
    }
}
