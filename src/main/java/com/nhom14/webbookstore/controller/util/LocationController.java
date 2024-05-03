package com.nhom14.webbookstore.controller.util;

import com.nhom14.webbookstore.entity.City;
import com.nhom14.webbookstore.entity.District;
import com.nhom14.webbookstore.entity.Ward;
import com.nhom14.webbookstore.service.CityService;
import com.nhom14.webbookstore.service.DistrictService;
import com.nhom14.webbookstore.service.WardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class LocationController {
    private CityService cityService;
    private DistrictService districtService;
    private WardService wardService;

    @Autowired
    public LocationController(CityService cityService, DistrictService districtService, WardService wardService) {
        super();
        this.cityService = cityService;
        this.districtService = districtService;
        this.wardService = wardService;
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getAllCities() {
        try {
            List<City> cities = cityService.getAllCities();
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/districts/{cityId}")
    public ResponseEntity<?> getDistrictsByCityId(@PathVariable int cityId) {
        try {
            List<District> districts = districtService.getDistrictsByCityId(cityId);
            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/wards/{districtId}")
    public ResponseEntity<?> getWardsByDistrictId(@PathVariable int districtId) {
        try {
            List<Ward> wards = wardService.getWardsByDistrictId(districtId);
            return ResponseEntity.ok(wards);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}
