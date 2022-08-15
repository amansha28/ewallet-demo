package com.example.ewalletdemo.transaction.repository;

import com.example.ewalletdemo.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Transaction findTransactionByTransactionId(String transactionId);
}
