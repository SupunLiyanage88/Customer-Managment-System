package com.example.cms.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerResponse {

    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String nicNumber;
    private List<CustomerMobileNumberResponse> mobileNumbers = new ArrayList<CustomerMobileNumberResponse>();
    private List<CustomerAddressResponse> addresses = new ArrayList<CustomerAddressResponse>();
    private List<FamilyMemberSummaryResponse> familyMembers = new ArrayList<FamilyMemberSummaryResponse>();

    public CustomerResponse() {
    }

    public CustomerResponse(Long id, String name, LocalDate dateOfBirth, String nicNumber,
                            List<CustomerMobileNumberResponse> mobileNumbers,
                            List<CustomerAddressResponse> addresses,
                            List<FamilyMemberSummaryResponse> familyMembers) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.nicNumber = nicNumber;
        this.mobileNumbers = mobileNumbers;
        this.addresses = addresses;
        this.familyMembers = familyMembers;
    }

    public Long getId() {
        return id;
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

    public List<CustomerMobileNumberResponse> getMobileNumbers() {
        return mobileNumbers;
    }

    public List<CustomerAddressResponse> getAddresses() {
        return addresses;
    }

    public List<FamilyMemberSummaryResponse> getFamilyMembers() {
        return familyMembers;
    }
}