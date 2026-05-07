package com.bank.app.ejb.bean;

import com.bank.app.core.model.Transaction;
import com.bank.app.core.service.TransactionService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class TransactionSessionBean implements TransactionService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return em.createNamedQuery("Transaction.findByAccountId", Transaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @Override
    public List<Transaction> getTransactionsByUser(Long userId) {
        return em.createNamedQuery("Transaction.findByUserId", Transaction.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return em.createNamedQuery("Transaction.findAll", Transaction.class).getResultList();
    }

    @Override
    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return em.createNamedQuery("Transaction.findByDateRange", Transaction.class)
                .setParameter("startDate", start)
                .setParameter("endDate", end)
                .getResultList();
    }

    @Override
    public void recordTransaction(Transaction transaction) {
        em.persist(transaction);
    }
} 