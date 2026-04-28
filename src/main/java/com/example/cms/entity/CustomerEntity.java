package com.example.cms.entity;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nic_number"})
})
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "nic_number", nullable = false, unique = true)
    private String nicNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CustomerMobileNumberEntity> mobileNumbers = new LinkedHashSet<CustomerMobileNumberEntity>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CustomerAddressEntity> addresses = new LinkedHashSet<CustomerAddressEntity>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "customer_family_members",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "family_member_id"))
    private Set<CustomerEntity> familyMembers = new LinkedHashSet<CustomerEntity>();

    public CustomerEntity() {
    }

    public CustomerEntity(String name, LocalDate dateOfBirth, String nicNumber) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.nicNumber = nicNumber;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public Set<CustomerMobileNumberEntity> getMobileNumbers() {
        return mobileNumbers;
    }

    public Set<CustomerAddressEntity> getAddresses() {
        return addresses;
    }

    public Set<CustomerEntity> getFamilyMembers() {
        return familyMembers;
    }
}