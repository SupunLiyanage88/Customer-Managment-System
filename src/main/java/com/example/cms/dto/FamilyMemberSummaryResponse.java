package com.example.cms.dto;

public class FamilyMemberSummaryResponse {

    private Long id;
    private String name;
    private String nicNumber;

    public FamilyMemberSummaryResponse() {
    }

    public FamilyMemberSummaryResponse(Long id, String name, String nicNumber) {
        this.id = id;
        this.name = name;
        this.nicNumber = nicNumber;
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
}