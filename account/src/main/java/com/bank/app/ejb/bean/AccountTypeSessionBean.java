package com.bank.app.ejb.bean;

import com.bank.app.core.model.AccountType;
import com.bank.app.core.service.AccountTypeService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class AccountTypeSessionBean implements AccountTypeService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public AccountType getAccountTypeById(Long id) {
        return em.find(AccountType.class, id);
    }

    @Override
    public AccountType getAccountTypeByName(String name) {
        try {
            TypedQuery<AccountType> query = em.createQuery(
                "SELECT t FROM AccountType t WHERE t.name = :name", AccountType.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<AccountType> getAllAccountTypes() {
        return em.createQuery("SELECT t FROM AccountType t", AccountType.class).getResultList();
    }

    @Override
    public void addAccountType(AccountType accountType) {
        em.persist(accountType);
    }

    @Override
    public void updateAccountType(AccountType accountType) {
        em.merge(accountType);
    }

    @Override
    public void deleteAccountType(Long id) {
        AccountType type = em.find(AccountType.class, id);
        if (type != null) {
            em.remove(type);
        }
    }
} 