package com.bank.app.ejb.bean;

import com.bank.app.core.model.BankBranch;
import com.bank.app.core.service.BankBranchService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class BankBranchSessionBean implements BankBranchService {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<BankBranch> getAllBankBranches() {
        return em.createNamedQuery("BankBranch.findAll", BankBranch.class).getResultList();
    }

    @Override
    public List<BankBranch> getActiveBankBranches() {
        return em.createNamedQuery("BankBranch.findActive", BankBranch.class).getResultList();
    }

    @Override
    public BankBranch getBankBranchById(Long id) {
        return em.find(BankBranch.class, id);
    }

    @Override
    public void addBankBranch(BankBranch bankBranch) {
        em.persist(bankBranch);
    }

    @Override
    public void updateBankBranch(BankBranch bankBranch) {
        em.merge(bankBranch);
    }

    @Override
    public void deleteBankBranch(Long id) {
        BankBranch bankBranch = em.find(BankBranch.class, id);
        if (bankBranch != null) {
            em.remove(bankBranch);
        }
    }

    @Override
    public void activateBankBranch(Long id) {
        BankBranch bankBranch = em.find(BankBranch.class, id);
        if (bankBranch != null) {
            bankBranch.setIsActive(true);
            em.merge(bankBranch);
        }
    }

    @Override
    public void deactivateBankBranch(Long id) {
        BankBranch bankBranch = em.find(BankBranch.class, id);
        if (bankBranch != null) {
            bankBranch.setIsActive(false);
            em.merge(bankBranch);
        }
    }

    @Override
    public boolean isBankBranchExists(String branchName) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM BankBranch b WHERE b.branchName = :branchName", 
                Long.class);
            query.setParameter("branchName", branchName);
            return query.getSingleResult() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }
} 