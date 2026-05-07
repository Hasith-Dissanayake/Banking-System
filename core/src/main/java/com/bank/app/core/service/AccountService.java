package com.bank.app.core.service;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.AccountStatus;
import com.bank.app.core.exception.InsufficientFundsException;
import jakarta.ejb.Remote;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Remote
public interface AccountService {

    Account getAccountById(Long id);
    Account getAccountByAccountNumber(String accountNumber);
    List<Account> getAccountsByUser(Long userId);
    List<Account> getAllAccounts();
    void createAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(Long id);
    
    // Customer information methods
    Account getAccountByNic(String nic);
    Account getAccountByEmail(String email);
    List<Account> getAccountsByCustomerName(String customerName);
    List<Account> getAccountsByBankBranch(Long bankBranchId);
    
    // Account status management
    void activateAccount(Long accountId);
    void deactivateAccount(Long accountId);
    void suspendAccount(Long accountId);
    void closeAccount(Long accountId);
    void setAccountDormant(Long accountId);
    List<Account> getAccountsByStatus(AccountStatus status);
    
    // Transaction operations
    void deposit(Long accountId, BigDecimal amount);
    void withdraw(Long accountId, BigDecimal amount) throws InsufficientFundsException;
    void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount);
    void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, com.bank.app.core.model.User createdBy);
    void deposit(Long accountId, BigDecimal amount, Long userId);
    void withdraw(Long accountId, BigDecimal amount, Long userId) throws InsufficientFundsException;
    
    // Account limits and settings
    void setDailyTransactionLimit(Long accountId, BigDecimal limit);
    void setMonthlyTransactionLimit(Long accountId, BigDecimal limit);
    void setMinimumBalance(Long accountId, BigDecimal minimumBalance);
    void setInterestRate(Long accountId, BigDecimal interestRate);
    
    // Bulk update interest rate for all accounts of a given type
    void updateInterestRateForAllAccounts(com.bank.app.core.model.AccountType accountType, java.math.BigDecimal newInterestRate);
    
    // Reporting and analytics
    BigDecimal getTotalBalance();
    BigDecimal getTotalBalanceByAccountType(String accountType);
    BigDecimal getTotalBalanceByBankBranch(Long bankBranchId);
    List<Account> getAccountsOpenedBetween(LocalDate startDate, LocalDate endDate);
    List<Account> getDormantAccounts(int daysInactive);
    
    // Validation methods
    boolean isAccountActive(Long accountId);
    boolean hasSufficientBalance(Long accountId, BigDecimal amount);
    boolean meetsMinimumBalance(Long accountId);
    boolean isWithinDailyLimit(Long accountId, BigDecimal amount);
    boolean isWithinMonthlyLimit(Long accountId, BigDecimal amount);
} 