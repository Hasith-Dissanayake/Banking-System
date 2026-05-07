package com.bank.app.core.service;

import com.bank.app.core.model.Transaction;
import jakarta.ejb.Remote;
import java.time.LocalDateTime;
import java.util.List;

@Remote
public interface TransactionService {
    List<Transaction> getTransactionsByAccount(Long accountId);
    List<Transaction> getTransactionsByUser(Long userId);
    List<Transaction> getAllTransactions();
    List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end);
    void recordTransaction(Transaction transaction);
} 