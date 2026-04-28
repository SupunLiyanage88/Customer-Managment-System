package com.example.cms.dto;

import java.time.LocalDate;

public class CustomerSummaryResponse {

    private Long id;
    private String name;
    private String nicNumber;
    private LocalDate dateOfBirth;

    public CustomerSummaryResponse() {
    }

    public CustomerSummaryResponse(Long id, String name, String nicNumber, LocalDate dateOfBirth) {
        this.id = id;
        this.name = name;
        this.nicNumber = nicNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
}