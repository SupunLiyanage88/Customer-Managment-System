package com.example.cms;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @GetMapping("/api/customers")
    public List<Customer> customers() {
        return Arrays.asList(
                new Customer(1L, "Alice Johnson", "alice@example.com"),
                new Customer(2L, "Ben Smith", "ben@example.com")
        );
    }
}