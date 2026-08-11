package com.example.bank.simulator1.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.bank.simulator1.model.Transaction;


public class TransactionRepository {

	private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
	
	public void save(Transaction transaction) {
		transactions.put(transaction.getPrn(), transaction);
		return;
	}
	
	public Optional<Transaction> findByPrn(String prn){
		return Optional.ofNullable(transactions.get(prn));
	}
	
	public boolean existsByPrn(String prn) {
		return transactions.containsKey(prn);
	}
}
