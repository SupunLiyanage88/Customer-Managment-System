package com.example.cms.service;

import com.example.cms.dto.BulkImportJobResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/imports")
public class BulkImportController {

    private final BulkCustomerImportService bulkCustomerImportService;

    public BulkImportController(BulkCustomerImportService bulkCustomerImportService) {
        this.bulkCustomerImportService = bulkCustomerImportService;
    }

    @GetMapping("/{jobId}")
    public BulkImportJobResponse getJob(@PathVariable Long jobId) {
        return bulkCustomerImportService.getJob(jobId);
    }
}