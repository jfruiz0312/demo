package com.example.ms_pago.repository;

import com.example.ms_pago.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerId(String customerId);
    boolean existsByCustomerId(String customerId);
}
