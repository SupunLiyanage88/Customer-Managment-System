package com.example.cms.service;

import com.example.cms.dto.BulkImportJobResponse;

public interface CustomerImportStatusService {

    BulkImportJobResponse getJob(Long jobId);
}