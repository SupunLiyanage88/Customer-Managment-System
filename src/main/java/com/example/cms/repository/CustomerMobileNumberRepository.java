package com.example.cms.repository;

import com.example.cms.entity.CustomerMobileNumberEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerMobileNumberRepository extends JpaRepository<CustomerMobileNumberEntity, Long> {
}