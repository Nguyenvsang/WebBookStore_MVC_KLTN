package com.nhom14.webbookstore.repository;

import com.nhom14.webbookstore.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findByCityId(int cityId);
}
