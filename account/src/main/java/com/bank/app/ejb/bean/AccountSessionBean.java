package com.bank.app.ejb.bean;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.AccountStatus;
import com.bank.app.core.model.Transaction;
import com.bank.app.core.model.TransactionType;
import com.bank.app.core.model.Transfer;
import com.bank.app.core.model.TransferStatus;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.interceptor.Logged;
import com.bank.app.core.exception.InsufficientFundsException;
import com.bank.app.core.interceptor.SecurityLogged;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Stateless
@Logged
public class AccountSessionBean implements AccountService {
    @PersistenceContext
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Account getAccountById(Long id) {
        return em.find(Account.class, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Account getAccountByAccountNumber(String accountNumber) {
        try {
            TypedQuery<Account> query = em.createNamedQuery("Account.findByAccountNumber", Account.class)
                    .setParameter("accountNumber", accountNumber);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Account> getAccountsByUser(Long userId) {
        return em.createNamedQuery("Account.findByUserId", Account.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Account> getAllAccounts() {
        return em.createNamedQuery("Account.findAll", Account.class).getResultList();
    }

    @Override
    public void createAccount(Account account) {
        em.persist(account);
        em.flush(); // Ensure ID is generated
        recordTransaction(account.getId(), TransactionType.DEPOSIT, account.getBalance(), "Initial deposit", null);
    }

    @Override
    public void updateAccount(Account account) {
        em.merge(account);
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = em.find(Account.class, id);
        if (account != null) {
            em.remove(account);
        }
    }
    
    // Customer information methods
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Account getAccountByNic(String nic) {
        try {
            TypedQuery<Account> query = em.createNamedQuery("Account.findByNic", Account.class)
                    .setParameter("nic", nic);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Account getAccountByEmail(String email) {
        try {
            TypedQuery<Account> query = em.createNamedQuery("Account.findByEmail", Account.class)
                    .setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Account> getAccountsByCustomerName(String customerName) {
        return em.createQuery("SELECT a FROM Account a WHERE a.customerName LIKE :name", Account.class)
                .setParameter("name", "%" + customerName + "%")
                .getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Account> getAccountsByBankBranch(Long bankBranchId) {
        return em.createQuery("SELECT a FROM Account a WHERE a.bankBranch.id = :branchId", Account.class)
                .setParameter("branchId", bankBranchId)
                .getResultList();
    }
    
    // Account status management
    @Override
    public void activateAccount(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setStatus(AccountStatus.ACTIVE);
            em.merge(account);
        }
    }

    @Override
    public void deactivateAccount(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setStatus(AccountStatus.INACTIVE);
            em.merge(account);
        }
    }

    @Override
    public void suspendAccount(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setStatus(AccountStatus.SUSPENDED);
            em.merge(account);
        }
    }

    @Override
    public void closeAccount(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setStatus(AccountStatus.CLOSED);
            em.merge(account);
        }
    }

    @Override
    public void setAccountDormant(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setStatus(AccountStatus.DORMANT);
            em.merge(account);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Account> getAccountsByStatus(AccountStatus status) {
        return em.createQuery("SELECT a FROM Account a WHERE a.status = :status", Account.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setBalance(account.getBalance().add(amount));
            account.setLastTransactionDate(LocalDate.now());
            // If account was INACTIVE and new balance >= 3000, activate it
            if (account.getStatus() == AccountStatus.INACTIVE && account.getBalance().compareTo(new BigDecimal("3000")) >= 0) {
                account.setStatus(AccountStatus.ACTIVE);
            }
            em.merge(account);
            recordTransaction(accountId, TransactionType.DEPOSIT, amount, "Deposit", null);
        }
    }

    @Override
    public void withdraw(Long accountId, BigDecimal amount) throws InsufficientFundsException {
        Account account = em.find(Account.class, accountId);
        if (account != null && account.isActive() && account.hasSufficientBalance(amount)) {
            account.setBalance(account.getBalance().subtract(amount));
            account.setLastTransactionDate(LocalDate.now());
            em.merge(account);
            recordTransaction(accountId, TransactionType.WITHDRAWAL, amount, "Withdrawal", null);
        } else if (account != null && !account.isActive()) {
            // If account is INACTIVE, try to activate it by depositing
            if (account.getBalance().compareTo(new BigDecimal("3000")) >= 0) {
                account.setStatus(AccountStatus.ACTIVE);
                em.merge(account);
                recordTransaction(accountId, TransactionType.DEPOSIT, amount, "Deposit to activate account", null);
            } else {
                throw new InsufficientFundsException("Insufficient balance to activate account.");
            }
        } else {
            throw new InsufficientFundsException("Insufficient balance for withdrawal.");
        }
    }

    @Override
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, com.bank.app.core.model.User createdBy) {
        Account from = em.find(Account.class, fromAccountId);
        Account to = em.find(Account.class, toAccountId);
        if (from != null && to != null && from.isActive() && to.isActive() && 
            from.hasSufficientBalance(amount)) {
            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));
            from.setLastTransactionDate(LocalDate.now());
            to.setLastTransactionDate(LocalDate.now());
            em.merge(from);
            em.merge(to);
            recordTransaction(fromAccountId, TransactionType.TRANSFER, amount, "Transfer to account " + toAccountId, createdBy != null ? createdBy.getId() : null);
            recordTransaction(toAccountId, TransactionType.TRANSFER, amount, "Transfer from account " + fromAccountId, createdBy != null ? createdBy.getId() : null);
            // Persist Transfer entity for audit with createdBy
            Transfer transfer = new Transfer(fromAccountId, toAccountId, amount, createdBy);
            transfer.setStatus(TransferStatus.COMPLETED);
            em.persist(transfer);
        }
    }
    
    @Override
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        transfer(fromAccountId, toAccountId, amount, null);
    }
    
    // Account limits and settings
    @Override
    public void setDailyTransactionLimit(Long accountId, BigDecimal limit) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setDailyTransactionLimit(limit);
            em.merge(account);
        }
    }

    @Override
    public void setMonthlyTransactionLimit(Long accountId, BigDecimal limit) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setMonthlyTransactionLimit(limit);
            em.merge(account);
        }
    }

    @Override
    public void setMinimumBalance(Long accountId, BigDecimal minimumBalance) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setMinimumBalance(minimumBalance);
            em.merge(account);
        }
    }

    @Override
    public void setInterestRate(Long accountId, BigDecimal interestRate) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setInterestRate(interestRate);
            em.merge(account);
        }
    }
    
    // Reporting and analytics
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BigDecimal getTotalBalance() {
        return em.createQuery("SELECT SUM(a.balance) FROM Account a WHERE a.status = 'ACTIVE'", BigDecimal.class)
                .getSingleResult();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BigDecimal getTotalBalanceByAccountType(String accountType) {
        return em.createQuery("SELECT SUM(a.balance) FROM Account a WHERE a.accountType = :type AND a.status = 'ACTIVE'", BigDecimal.class)
                .setParameter("type", accountType)
                .getSingleResult();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BigDecimal getTotalBalanceByBankBranch(Long bankBranchId) {
        return em.createQuery("SELECT SUM(a.balance) FROM Account a WHERE a.bankBranch.id = :branchId AND a.status = 'ACTIVE'", BigDecimal.class)
                .setParameter("branchId", bankBranchId)
                .getSingleResult();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Account> getAccountsOpenedBetween(LocalDate startDate, LocalDate endDate) {
        return em.createQuery("SELECT a FROM Account a WHERE a.openingDate BETWEEN :startDate AND :endDate", Account.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Account> getDormantAccounts(int daysInactive) {
        LocalDate cutoffDate = LocalDate.now().minusDays(daysInactive);
        return em.createQuery("SELECT a FROM Account a WHERE a.lastTransactionDate < :cutoffDate AND a.status = 'ACTIVE'", Account.class)
                .setParameter("cutoffDate", cutoffDate)
                .getResultList();
    }
    
    // Validation methods
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isAccountActive(Long accountId) {
        Account account = em.find(Account.class, accountId);
        return account != null && account.isActive();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean hasSufficientBalance(Long accountId, BigDecimal amount) {
        Account account = em.find(Account.class, accountId);
        return account != null && account.hasSufficientBalance(amount);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean meetsMinimumBalance(Long accountId) {
        Account account = em.find(Account.class, accountId);
        return account != null && account.meetsMinimumBalance();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isWithinDailyLimit(Long accountId, BigDecimal amount) {
        Account account = em.find(Account.class, accountId);
        return account != null && (account.getDailyTransactionLimit() == null || 
               amount.compareTo(account.getDailyTransactionLimit()) <= 0);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isWithinMonthlyLimit(Long accountId, BigDecimal amount) {
        Account account = em.find(Account.class, accountId);
        return account != null && (account.getMonthlyTransactionLimit() == null || 
               amount.compareTo(account.getMonthlyTransactionLimit()) <= 0);
    }

    @Override
    public void updateInterestRateForAllAccounts(com.bank.app.core.model.AccountType accountType, java.math.BigDecimal newInterestRate) {
        em.createQuery("UPDATE Account a SET a.interestRate = :rate WHERE a.accountType = :type")
            .setParameter("rate", newInterestRate)
            .setParameter("type", accountType)
            .executeUpdate();
    }

    private void recordTransaction(Long accountId, TransactionType type, BigDecimal amount, String description, Long userId) {
        Transaction tx = new Transaction(accountId, type, amount, LocalDateTime.now(), description, userId);
        em.persist(tx);
    }

    // Overloaded deposit with userId
    public void deposit(Long accountId, BigDecimal amount, Long userId) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            account.setBalance(account.getBalance().add(amount));
            account.setLastTransactionDate(LocalDate.now());
            if (account.getStatus() == AccountStatus.INACTIVE && account.getBalance().compareTo(new BigDecimal("3000")) >= 0) {
                account.setStatus(AccountStatus.ACTIVE);
            }
            em.merge(account);
            recordTransaction(accountId, TransactionType.DEPOSIT, amount, "Deposit", userId);
        }
    }
    // Overloaded withdraw with userId
    public void withdraw(Long accountId, BigDecimal amount, Long userId) throws InsufficientFundsException {
        Account account = em.find(Account.class, accountId);
        if (account != null && account.isActive()) {
            if (!account.hasSufficientBalance(amount)) {
                throw new InsufficientFundsException("Insufficient balance for withdrawal.");
            }
            account.setBalance(account.getBalance().subtract(amount));
            account.setLastTransactionDate(LocalDate.now());
            em.merge(account);
            recordTransaction(accountId, TransactionType.WITHDRAWAL, amount, "Withdrawal", userId);
        } else if (account != null && !account.isActive()) {
            // If account is INACTIVE, try to activate it by depositing
            if (account.getBalance().compareTo(new BigDecimal("3000")) >= 0) {
                account.setStatus(AccountStatus.ACTIVE);
                em.merge(account);
                recordTransaction(accountId, TransactionType.DEPOSIT, amount, "Deposit to activate account", userId);
            } else {
                throw new InsufficientFundsException("Insufficient balance to activate account.");
            }
        } else {
            throw new InsufficientFundsException("Insufficient balance for withdrawal.");
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Account> getDormantAccountsReport(int daysInactive) {
        return getDormantAccounts(daysInactive);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @SecurityLogged
    public void forceAccountClosure(Long accountId) {
        closeAccount(accountId);
    }
} 