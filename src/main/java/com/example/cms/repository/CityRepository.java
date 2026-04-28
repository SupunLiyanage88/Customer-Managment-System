package com.example.cms.repository;

import java.util.List;

import com.example.cms.entity.CityEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<CityEntity, Long> {

    List<CityEntity> findAllByOrderByNameAsc();

    List<CityEntity> findAllByCountryIdOrderByNameAsc(Long countryId);

    boolean existsByCountryIdAndNameIgnoreCase(Long countryId, String name);
}