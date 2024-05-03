package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.Ward;

import java.util.List;

public interface WardService {
    List<Ward> getAllWards();
    List<Ward> getWardsByDistrictId(int districtId);

    Ward getWardById(int id);
}
