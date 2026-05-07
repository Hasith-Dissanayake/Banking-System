package com.bank.app.ejb.bean;

import com.bank.app.core.model.Withdrawal;
import com.bank.app.core.service.WithdrawalService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class WithdrawalSessionBean implements WithdrawalService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void createWithdrawal(Withdrawal withdrawal) {
        em.persist(withdrawal);
    }

    @Override
    public Withdrawal getWithdrawalById(Long id) {
        return em.find(Withdrawal.class, id);
    }

    @Override
    public List<Withdrawal> getAllWithdrawals() {
        return em.createQuery("SELECT w FROM Withdrawal w ORDER BY w.createdDate DESC", Withdrawal.class).getResultList();
    }

    @Override
    public List<Withdrawal> getWithdrawalsByAccount(Long accountId) {
        return em.createQuery("SELECT w FROM Withdrawal w WHERE w.accountId = :accountId ORDER BY w.createdDate DESC", Withdrawal.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @Override
    public List<Withdrawal> getWithdrawalsByUser(Long userId) {
        return em.createQuery("SELECT w FROM Withdrawal w WHERE w.createdBy.id = :userId ORDER BY w.createdDate DESC", Withdrawal.class)
                .setParameter("userId", userId)
                .getResultList();
    }
} 