package com.example.currencybank.repository;

import com.example.currencybank.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationsRepository extends JpaRepository<Operation, Long> {

}