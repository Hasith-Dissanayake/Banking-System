package com.bank.app.ejb.bean;

import com.bank.app.core.model.AccountPolicy;
import com.bank.app.core.model.AccountType;
import com.bank.app.core.service.AccountPolicyService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class AccountPolicySessionBean implements AccountPolicyService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public AccountPolicy getPolicyByAccountType(AccountType accountType) {
        try {
            TypedQuery<AccountPolicy> query = em.createQuery(
                "SELECT p FROM AccountPolicy p WHERE p.accountType = :type", AccountPolicy.class);
            query.setParameter("type", accountType);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<AccountPolicy> getAllPolicies() {
        return em.createQuery("SELECT p FROM AccountPolicy p", AccountPolicy.class).getResultList();
    }

    @Override
    public void updatePolicy(AccountPolicy policy) {
        em.merge(policy);
    }

    @Override
    public void addPolicy(AccountPolicy policy) {
        em.persist(policy);
    }

    @Override
    public void deletePolicy(Long id) {
        AccountPolicy policy = em.find(AccountPolicy.class, id);
        if (policy != null) {
            em.remove(policy);
        }
    }
} 