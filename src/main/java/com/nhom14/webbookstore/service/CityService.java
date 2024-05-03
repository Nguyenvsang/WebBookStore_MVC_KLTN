package com.nhom14.webbookstore.service;

import com.nhom14.webbookstore.entity.City;

import java.util.List;

public interface CityService {
    List<City> getAllCities();

    City getCityById(int id);
}
