package com.example.cms.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.cms.dto.CustomerSummaryResponse;
import com.example.cms.entity.CustomerEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByNicNumberIgnoreCase(String nicNumber);

    boolean existsByNicNumberIgnoreCase(String nicNumber);

    List<CustomerEntity> findByNicNumberIn(Collection<String> nicNumbers);

    @EntityGraph(attributePaths = {"mobileNumbers", "addresses", "addresses.city", "addresses.country", "familyMembers", "familyMembers.mobileNumbers"})
    @Query("select c from CustomerEntity c where c.id = :id")
    Optional<CustomerEntity> findDetailedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"mobileNumbers", "addresses", "addresses.city", "addresses.country", "familyMembers"})
    @Query("select c from CustomerEntity c where lower(c.nicNumber) = lower(:nicNumber)")
    Optional<CustomerEntity> findDetailedByNicNumber(@Param("nicNumber") String nicNumber);

    @Query("select new com.example.cms.dto.CustomerSummaryResponse(c.id, c.name, c.nicNumber, c.dateOfBirth) from CustomerEntity c")
    Page<CustomerSummaryResponse> findAllSummaries(Pageable pageable);
}