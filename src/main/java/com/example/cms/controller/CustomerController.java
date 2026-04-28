package com.example.cms.controller;

import javax.validation.Valid;

import com.example.cms.dto.BulkImportJobResponse;
import com.example.cms.dto.CustomerRequest;
import com.example.cms.dto.CustomerResponse;
import com.example.cms.dto.CustomerSummaryResponse;
import com.example.cms.dto.CustomerUpdateRequest;
import com.example.cms.service.BulkCustomerImportService;
import com.example.cms.service.CustomerService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final BulkCustomerImportService bulkCustomerImportService;

    public CustomerController(CustomerService customerService,
                              BulkCustomerImportService bulkCustomerImportService) {
        this.customerService = customerService;
        this.bulkCustomerImportService = bulkCustomerImportService;
    }

    @GetMapping
    public Page<CustomerSummaryResponse> list(Pageable pageable) {
        return customerService.listCustomers(pageable);
    }

    @GetMapping("/{id}")
    public CustomerResponse view(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

    @GetMapping("/by-nic/{nicNumber}")
    public CustomerResponse viewByNic(@PathVariable String nicNumber) {
        return customerService.getCustomerByNic(nicNumber);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.updateCustomer(id, request);
    }

    @PostMapping("/bulk/create")
    public BulkImportJobResponse bulkCreate(@RequestParam("file") MultipartFile file) {
        return bulkCustomerImportService.startImport(file, com.example.cms.entity.BulkImportMode.CREATE);
    }

    @PostMapping("/bulk/update")
    public BulkImportJobResponse bulkUpdate(@RequestParam("file") MultipartFile file) {
        return bulkCustomerImportService.startImport(file, com.example.cms.entity.BulkImportMode.UPDATE);
    }
}