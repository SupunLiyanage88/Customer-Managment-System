package com.example.cms.util;

import java.time.LocalDate;

public class ExcelCustomerRow {

    private final String name;
    private final LocalDate dateOfBirth;
    private final String nicNumber;

    public ExcelCustomerRow(String name, LocalDate dateOfBirth, String nicNumber) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.nicNumber = nicNumber;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNicNumber() {
        return nicNumber;
    }
}