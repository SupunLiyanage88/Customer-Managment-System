package com.example.cms.util;

import java.util.ArrayList;
import java.util.List;

import com.example.cms.dto.CityResponse;
import com.example.cms.dto.CountryResponse;
import com.example.cms.dto.CustomerAddressResponse;
import com.example.cms.dto.CustomerMobileNumberResponse;
import com.example.cms.dto.CustomerResponse;
import com.example.cms.dto.FamilyMemberSummaryResponse;
import com.example.cms.entity.CustomerAddressEntity;
import com.example.cms.entity.CustomerEntity;
import com.example.cms.entity.CustomerMobileNumberEntity;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerResponse toResponse(CustomerEntity customer) {
        List<CustomerMobileNumberResponse> mobileResponses = new ArrayList<CustomerMobileNumberResponse>();
        for (CustomerMobileNumberEntity mobileNumber : customer.getMobileNumbers()) {
            mobileResponses.add(new CustomerMobileNumberResponse(mobileNumber.getId(), mobileNumber.getMobileNumber()));
        }

        List<CustomerAddressResponse> addressResponses = new ArrayList<CustomerAddressResponse>();
        for (CustomerAddressEntity address : customer.getAddresses()) {
            CountryResponse countryResponse = new CountryResponse(
                    address.getCountry().getId(),
                    address.getCountry().getName(),
                    address.getCountry().getCode());
            CityResponse cityResponse = new CityResponse(
                    address.getCity().getId(),
                    address.getCity().getName(),
                    countryResponse);
            addressResponses.add(new CustomerAddressResponse(
                    address.getId(),
                    address.getAddressLine1(),
                    address.getAddressLine2(),
                    cityResponse,
                    countryResponse));
        }

        List<FamilyMemberSummaryResponse> familyResponses = new ArrayList<FamilyMemberSummaryResponse>();
        for (CustomerEntity familyMember : customer.getFamilyMembers()) {
            familyResponses.add(new FamilyMemberSummaryResponse(familyMember.getId(), familyMember.getName(), familyMember.getNicNumber()));
        }

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getDateOfBirth(),
                customer.getNicNumber(),
                mobileResponses,
                addressResponses,
                familyResponses);
    }
}