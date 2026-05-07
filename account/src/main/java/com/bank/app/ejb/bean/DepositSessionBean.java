package com.bank.app.ejb.bean;

import com.bank.app.core.model.Deposit;
import com.bank.app.core.service.DepositService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class DepositSessionBean implements DepositService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void createDeposit(Deposit deposit) {
        em.persist(deposit);
    }

    @Override
    public Deposit getDepositById(Long id) {
        return em.find(Deposit.class, id);
    }

    @Override
    public List<Deposit> getAllDeposits() {
        return em.createQuery("SELECT d FROM Deposit d ORDER BY d.createdDate DESC", Deposit.class).getResultList();
    }

    @Override
    public List<Deposit> getDepositsByAccount(Long accountId) {
        return em.createQuery("SELECT d FROM Deposit d WHERE d.accountId = :accountId ORDER BY d.createdDate DESC", Deposit.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @Override
    public List<Deposit> getDepositsByUser(Long userId) {
        return em.createQuery("SELECT d FROM Deposit d WHERE d.createdBy.id = :userId ORDER BY d.createdDate DESC", Deposit.class)
                .setParameter("userId", userId)
                .getResultList();
    }
} 