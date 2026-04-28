package com.example.cms.repository;

import com.example.cms.entity.BulkImportJobEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BulkImportJobRepository extends JpaRepository<BulkImportJobEntity, Long> {
}