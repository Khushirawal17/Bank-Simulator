package com.example.bank.simulator1.repository;

import com.example.bank.simulator1.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findByPrn(String prn);

    boolean existsByPrn(String prn);

    List<Transaction> findAll();

    void deleteAll();
}