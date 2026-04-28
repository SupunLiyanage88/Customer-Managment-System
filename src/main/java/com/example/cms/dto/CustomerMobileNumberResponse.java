package com.example.cms.dto;

public class CustomerMobileNumberResponse {

    private Long id;
    private String mobileNumber;

    public CustomerMobileNumberResponse() {
    }

    public CustomerMobileNumberResponse(Long id, String mobileNumber) {
        this.id = id;
        this.mobileNumber = mobileNumber;
    }

    public Long getId() {
        return id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}