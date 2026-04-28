package com.example.cms.repository;

import java.util.List;
import java.util.Optional;

import com.example.cms.entity.CountryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {

    Optional<CountryEntity> findByCodeIgnoreCase(String code);

    List<CountryEntity> findAllByOrderByNameAsc();
}