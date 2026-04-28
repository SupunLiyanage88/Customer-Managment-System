package com.example.cms.repository;

import com.example.cms.entity.CustomerAddressEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddressEntity, Long> {
}