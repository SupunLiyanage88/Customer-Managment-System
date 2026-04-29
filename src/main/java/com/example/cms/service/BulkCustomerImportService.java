package com.example.cms.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;

import com.example.cms.dto.BulkImportJobResponse;
import com.example.cms.entity.BulkImportJobEntity;
import com.example.cms.entity.BulkImportMode;
import com.example.cms.entity.CustomerEntity;
import com.example.cms.entity.ImportStatus;
import com.example.cms.repository.BulkImportJobRepository;
import com.example.cms.repository.CustomerRepository;
import com.example.cms.util.ExcelCustomerRow;
import com.example.cms.util.ExcelCustomerRowParser;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BulkCustomerImportService {

    private static final int BATCH_SIZE = 1000;

    private final CustomerRepository customerRepository;
    private final BulkImportJobRepository bulkImportJobRepository;
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;
    private final TaskExecutor bulkImportExecutor;

    public BulkCustomerImportService(CustomerRepository customerRepository,
                                     BulkImportJobRepository bulkImportJobRepository,
                                     EntityManager entityManager,
                                     PlatformTransactionManager transactionManager,
                                     @Qualifier("bulkImportExecutor") TaskExecutor bulkImportExecutor) {
        this.customerRepository = customerRepository;
        this.bulkImportJobRepository = bulkImportJobRepository;
        this.entityManager = entityManager;
        this.transactionTemplate = new TransactionTemplate(java.util.Objects.requireNonNull(transactionManager));
        this.bulkImportExecutor = bulkImportExecutor;
    }

    public BulkImportJobResponse startImport(MultipartFile file, BulkImportMode mode) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Excel file is required");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            originalName = "customers.xlsx";
        }

        try {
            Path tempFile = Files.createTempFile("customer-import-", ".xlsx");
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            BulkImportJobEntity job = new BulkImportJobEntity();
            job.setMode(mode);
            job.setStatus(ImportStatus.PENDING);
            job.setFileName(originalName);
            final BulkImportJobEntity savedJob = bulkImportJobRepository.save(job);
            final Long savedJobId = savedJob.getId();

            bulkImportExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    processImport(savedJobId.longValue(), tempFile, mode);
                }
            });

            return toResponse(savedJob);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Failed to read upload: " + exception.getMessage(), exception);
        }
    }

    public BulkImportJobResponse getJob(long jobId) {
        BulkImportJobEntity job = bulkImportJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found"));
        return toResponse(job);
    }

    private void processImport(long jobId, Path file, BulkImportMode mode) {
        BulkImportJobEntity job = bulkImportJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found"));
        job.setStatus(ImportStatus.PROCESSING);
        bulkImportJobRepository.save(job);

        final long[] totalRows = new long[] {0L};
        final long[] successRows = new long[] {0L};
        final long[] failureRows = new long[] {0L};
        final List<ExcelCustomerRow> batch = new ArrayList<ExcelCustomerRow>(BATCH_SIZE);

        try {
            ExcelCustomerRowParser.parse(file, new ExcelCustomerRowParser.RowConsumer() {
                @Override
                public void accept(ExcelCustomerRow row) {
                    totalRows[0]++;
                    batch.add(row);
                    if (batch.size() >= BATCH_SIZE) {
                        ImportCounts counts = persistBatch(batch, mode);
                        successRows[0] += counts.successRows;
                        failureRows[0] += counts.failureRows;
                        batch.clear();
                        updateJob(jobId, totalRows[0], successRows[0], failureRows[0]);
                    }
                }
            });

            if (!batch.isEmpty()) {
                ImportCounts counts = persistBatch(batch, mode);
                successRows[0] += counts.successRows;
                failureRows[0] += counts.failureRows;
            }

            job.setTotalRows(totalRows[0]);
            job.setProcessedRows(totalRows[0]);
            job.setSuccessRows(successRows[0]);
            job.setFailureRows(failureRows[0]);
            job.setStatus(failureRows[0] > 0 ? ImportStatus.COMPLETED_WITH_ERRORS : ImportStatus.COMPLETED);
            bulkImportJobRepository.save(job);
        } catch (Exception exception) {
            job.setTotalRows(totalRows[0]);
            job.setProcessedRows(totalRows[0]);
            job.setSuccessRows(successRows[0]);
            job.setFailureRows(failureRows[0]);
            job.setStatus(ImportStatus.FAILED);
            job.setErrorMessage(exception.getMessage());
            bulkImportJobRepository.save(job);
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) {
            }
        }
    }

    private void updateJob(final long jobId, final long processedRows, final long successRows, final long failureRows) {
        transactionTemplate.execute(status -> {
            BulkImportJobEntity job = bulkImportJobRepository.findById(jobId).get();
            job.setProcessedRows(processedRows);
            job.setSuccessRows(successRows);
            job.setFailureRows(failureRows);
            bulkImportJobRepository.save(job);
            return null;
        });
    }

    private ImportCounts persistBatch(final List<ExcelCustomerRow> batch, final BulkImportMode mode) {
        return transactionTemplate.execute(status -> {
            List<String> nicNumbers = new ArrayList<String>();
            for (ExcelCustomerRow row : batch) {
                nicNumbers.add(row.getNicNumber().trim());
            }

            Map<String, CustomerEntity> existingByNic = new HashMap<String, CustomerEntity>();
            for (CustomerEntity customer : customerRepository.findByNicNumberIn(nicNumbers)) {
                existingByNic.put(customer.getNicNumber().toLowerCase(Locale.ENGLISH), customer);
            }

            List<CustomerEntity> toSave = new ArrayList<CustomerEntity>();
            long successRows = 0L;
            long failureRows = 0L;

            for (ExcelCustomerRow row : batch) {
                String nicKey = row.getNicNumber().trim().toLowerCase(Locale.ENGLISH);
                CustomerEntity existing = existingByNic.get(nicKey);
                if (mode == BulkImportMode.CREATE) {
                    if (existing != null) {
                        failureRows++;
                        continue;
                    }
                    String customerName = row.getName().trim();
                    String nicNumber = row.getNicNumber().trim();
                    System.out.println("Bulk import: adding customer " + customerName + " (NIC: " + nicNumber + ")");
                    toSave.add(new CustomerEntity(customerName, row.getDateOfBirth(), nicNumber));
                    successRows++;
                } else {
                    if (existing == null) {
                        failureRows++;
                        continue;
                    }
                    existing.setName(row.getName().trim());
                    existing.setDateOfBirth(row.getDateOfBirth());
                    toSave.add(existing);
                    successRows++;
                }
            }

            customerRepository.saveAll(toSave);
            customerRepository.flush();
            entityManager.clear();
            return new ImportCounts(successRows, failureRows);
        });
    }

    private BulkImportJobResponse toResponse(BulkImportJobEntity job) {
        return new BulkImportJobResponse(job.getId(), job.getMode(), job.getStatus(), job.getFileName(),
                job.getTotalRows(), job.getProcessedRows(), job.getSuccessRows(), job.getFailureRows(), job.getErrorMessage());
    }

    private static class ImportCounts {
        private final long successRows;
        private final long failureRows;

        private ImportCounts(long successRows, long failureRows) {
            this.successRows = successRows;
            this.failureRows = failureRows;
        }
    }
}