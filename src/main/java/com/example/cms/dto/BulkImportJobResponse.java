package com.example.cms.dto;

import com.example.cms.entity.BulkImportMode;
import com.example.cms.entity.ImportStatus;

public class BulkImportJobResponse {

    private Long jobId;
    private BulkImportMode mode;
    private ImportStatus status;
    private String fileName;
    private long totalRows;
    private long processedRows;
    private long successRows;
    private long failureRows;
    private String errorMessage;

    public BulkImportJobResponse() {
    }

    public BulkImportJobResponse(Long jobId, BulkImportMode mode, ImportStatus status, String fileName,
                                 long totalRows, long processedRows, long successRows, long failureRows,
                                 String errorMessage) {
        this.jobId = jobId;
        this.mode = mode;
        this.status = status;
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.processedRows = processedRows;
        this.successRows = successRows;
        this.failureRows = failureRows;
        this.errorMessage = errorMessage;
    }

    public Long getJobId() {
        return jobId;
    }

    public BulkImportMode getMode() {
        return mode;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public String getFileName() {
        return fileName;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public long getProcessedRows() {
        return processedRows;
    }

    public long getSuccessRows() {
        return successRows;
    }

    public long getFailureRows() {
        return failureRows;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}