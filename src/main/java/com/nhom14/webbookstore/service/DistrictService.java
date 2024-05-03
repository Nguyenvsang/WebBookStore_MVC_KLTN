package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.District;

import java.util.List;

public interface DistrictService {
    List<District> getAllDistricts();
    List<District> getDistrictsByCityId(int cityId);

    District getDistrictById(int id);
}
