package com.example.cms.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.example.cms.dto.AddressRequest;
import com.example.cms.dto.CustomerRequest;
import com.example.cms.dto.CustomerResponse;
import com.example.cms.dto.CustomerSummaryResponse;
import com.example.cms.dto.CustomerUpdateRequest;
import com.example.cms.entity.CityEntity;
import com.example.cms.entity.CountryEntity;
import com.example.cms.entity.CustomerAddressEntity;
import com.example.cms.entity.CustomerEntity;
import com.example.cms.entity.CustomerMobileNumberEntity;
import com.example.cms.repository.CityRepository;
import com.example.cms.repository.CountryRepository;
import com.example.cms.repository.CustomerRepository;
import com.example.cms.util.CustomerMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CustomerService(CustomerRepository customerRepository,
                           CityRepository cityRepository,
                           CountryRepository countryRepository) {
        this.customerRepository = customerRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public Page<CustomerSummaryResponse> listCustomers(Pageable pageable) {
        return customerRepository.findAllSummaries(pageable);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(long id) {
        CustomerEntity customer = customerRepository.findDetailedById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return CustomerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByNic(String nicNumber) {
        CustomerEntity customer = customerRepository.findDetailedByNicNumber(nicNumber)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return CustomerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        ensureNicUnique(request.getNicNumber(), -1L);
        CustomerEntity customer = new CustomerEntity(request.getName().trim(), request.getDateOfBirth(), request.getNicNumber().trim());
        applyRequest(customer, request);
        customerRepository.save(customer);
        return CustomerMapper.toResponse(customerRepository.findDetailedById(Long.valueOf(customer.getId())).get());
    }

    @Transactional
    public CustomerResponse updateCustomer(long id, CustomerUpdateRequest request) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        ensureNicUnique(request.getNicNumber(), id);
        customer.setName(request.getName().trim());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNicNumber(request.getNicNumber().trim());
        customer.getMobileNumbers().clear();
        customer.getAddresses().clear();
        customer.getFamilyMembers().clear();
        applyRequest(customer, request);
        return CustomerMapper.toResponse(customerRepository.findDetailedById(Long.valueOf(customer.getId())).get());
    }

    private void applyRequest(CustomerEntity customer, CustomerRequest request) {
        if (request.getMobileNumbers() != null) {
            for (String mobileNumber : request.getMobileNumbers()) {
                if (mobileNumber != null && !mobileNumber.trim().isEmpty()) {
                    CustomerMobileNumberEntity mobile = new CustomerMobileNumberEntity(mobileNumber.trim());
                    mobile.setCustomer(customer);
                    customer.getMobileNumbers().add(mobile);
                }
            }
        }

        if (request.getAddresses() != null) {
            for (AddressRequest addressRequest : request.getAddresses()) {
                if (addressRequest == null) {
                    continue;
                }
                long cityId = addressRequest.getCityId().longValue();
                long countryId = addressRequest.getCountryId().longValue();
                CityEntity city = cityRepository.findById(cityId)
                        .orElseThrow(() -> new IllegalArgumentException("City not found"));
                CountryEntity country = countryRepository.findById(countryId)
                        .orElseThrow(() -> new IllegalArgumentException("Country not found"));
                CustomerAddressEntity address = new CustomerAddressEntity(
                        addressRequest.getAddressLine1().trim(),
                        addressRequest.getAddressLine2(),
                        city,
                        country);
                address.setCustomer(customer);
                customer.getAddresses().add(address);
            }
        }

        if (request.getFamilyMemberNicNumbers() != null && !request.getFamilyMemberNicNumbers().isEmpty()) {
            List<CustomerEntity> familyMembers = customerRepository.findByNicNumberIn(request.getFamilyMemberNicNumbers());
            Set<String> existingNics = new LinkedHashSet<String>();
            for (CustomerEntity familyMember : familyMembers) {
                existingNics.add(familyMember.getNicNumber().toLowerCase());
                if (!familyMember.getId().equals(customer.getId())) {
                    customer.getFamilyMembers().add(familyMember);
                }
            }
            for (String nic : request.getFamilyMemberNicNumbers()) {
                if (!existingNics.contains(nic.toLowerCase())) {
                    throw new IllegalArgumentException("Family member not found for NIC: " + nic);
                }
            }
        }
    }

    private void ensureNicUnique(String nicNumber, long customerId) {
        CustomerEntity existing = customerRepository.findByNicNumberIgnoreCase(nicNumber).orElse(null);
        if (existing != null && existing.getId().longValue() != customerId) {
            throw new IllegalArgumentException("NIC number already exists");
        }
    }
}