package com.bank.app.ejb.bean;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.Transaction;
import com.bank.app.core.model.TransactionType;
import com.bank.app.core.service.AccountPolicyService;
import com.bank.app.core.model.AccountPolicy;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class InterestSchedulerBean {
    @PersistenceContext
    private EntityManager em;

    @EJB
    private AccountPolicyService accountPolicyService;

    @Schedule(hour = "0", minute = "0", dayOfMonth = "1", persistent = false)
    public void calculateMonthlyInterest() {
        TypedQuery<Account> query = em.createNamedQuery("Account.findAll", Account.class);
        List<Account> accounts = query.getResultList();
        
        for (Account account : accounts) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                AccountPolicy policy = accountPolicyService.getPolicyByAccountType(account.getAccountType());
                BigDecimal interestRate = (policy != null) ? policy.getInterestRate() : BigDecimal.ZERO;
                BigDecimal interest = account.getBalance().multiply(interestRate).divide(new BigDecimal("12"), 2, java.math.RoundingMode.HALF_UP);
                
                if (interest.compareTo(BigDecimal.ZERO) > 0) {
                    account.setBalance(account.getBalance().add(interest));
                    em.merge(account);
                    
                    // Record interest transaction
                    Transaction tx = new Transaction(
                        account.getId(), 
                        TransactionType.INTEREST, 
                        interest, 
                        LocalDateTime.now(), 
                        "Monthly interest",
                        null // userId is null for system-generated interest
                    );
                    em.persist(tx);
                }
            }
        }
    }
} 