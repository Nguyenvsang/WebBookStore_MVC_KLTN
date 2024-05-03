package com.nhom14.webbookstore.service.impl;

import com.nhom14.webbookstore.entity.City;
import com.nhom14.webbookstore.repository.CityRepository;
import com.nhom14.webbookstore.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private CityRepository cityRepository;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository) {
        super();
        this.cityRepository = cityRepository;
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public City getCityById(int id) {
        return cityRepository.findById(id).orElse(null);
    }
}
