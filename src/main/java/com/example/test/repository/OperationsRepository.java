package com.example.test.repository;

import com.example.test.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationsRepository extends JpaRepository<Operation, Long> {

}