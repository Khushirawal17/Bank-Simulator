package com.example.bank.simulator1.repository;

import com.example.bank.simulator1.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final ConcurrentMap<String, Transaction> transactions =
            new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getPrn(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findByPrn(String prn) {
        return Optional.ofNullable(transactions.get(prn));
    }

    @Override
    public boolean existsByPrn(String prn) {
        return transactions.containsKey(prn);
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public void deleteAll() {
        transactions.clear();
    }
}