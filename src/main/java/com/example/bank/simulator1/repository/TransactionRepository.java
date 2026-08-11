package com.example.bank.simulator1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bank.simulator1.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByPrn(String prn);

    boolean existsByPrn(String prn);
}