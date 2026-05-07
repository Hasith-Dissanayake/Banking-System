package com.bank.app.core.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@NamedQueries({
        @NamedQuery(name = "Account.findByAccountNumber", query = "select a from Account a where a.accountNumber=:accountNumber"),
        @NamedQuery(name = "Account.findAll", query = "select a from Account a ORDER BY a.accountNumber"),
        @NamedQuery(name = "Account.findByUserId", query = "select a from Account a where a.user.id=:userId"),
        @NamedQuery(name = "Account.findByNic", query = "select a from Account a where a.nic=:nic"),
        @NamedQuery(name = "Account.findByEmail", query = "select a from Account a where a.email=:email")
})
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;
    
    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_type_id", nullable = false)
    private AccountType accountType;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    
    // Customer Personal Information
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "nic", nullable = false)
    private String nic;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;
    
    @Column(name = "birthday")
    private LocalDate birthday;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "occupation")
    private String occupation;
    
    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;
    
    // Account Status and Dates
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;
    
    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate = LocalDate.now();
    
    @Column(name = "last_transaction_date")
    private LocalDate lastTransactionDate;
    
    // Bank Branch Information
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_branch_id")
    private BankBranch bankBranch;
    
    // Additional Banking Details
    @Column(name = "interest_rate")
    private BigDecimal interestRate;
    
    @Column(name = "minimum_balance")
    private BigDecimal minimumBalance = BigDecimal.ZERO;
    
    @Column(name = "daily_transaction_limit")
    private BigDecimal dailyTransactionLimit;
    
    @Column(name = "monthly_transaction_limit")
    private BigDecimal monthlyTransactionLimit;

    public Account() {}


    public Account(String accountNumber, BigDecimal balance, AccountType accountType, User user) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.user = user;
        this.openingDate = LocalDate.now();
        this.status = AccountStatus.ACTIVE;
    }
    

    public Account(String accountNumber, BigDecimal balance, AccountType accountType, User user,
                   String customerName, String nic, String email, String contactNumber, 
                   LocalDate birthday, String address, BankBranch bankBranch) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.user = user;
        this.customerName = customerName;
        this.nic = nic;
        this.email = email;
        this.contactNumber = contactNumber;
        this.birthday = birthday;
        this.address = address;
        this.bankBranch = bankBranch;
        this.openingDate = LocalDate.now();
        this.status = AccountStatus.ACTIVE;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    // Customer Information Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    
    // Account Status and Dates Getters and Setters
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    
    public LocalDate getOpeningDate() { return openingDate; }
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }
    
    public LocalDate getLastTransactionDate() { return lastTransactionDate; }
    public void setLastTransactionDate(LocalDate lastTransactionDate) { this.lastTransactionDate = lastTransactionDate; }
    
    // Bank Branch Getters and Setters
    public BankBranch getBankBranch() { return bankBranch; }
    public void setBankBranch(BankBranch bankBranch) { this.bankBranch = bankBranch; }
    
    // Banking Details Getters and Setters
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public BigDecimal getMinimumBalance() { return minimumBalance; }
    public void setMinimumBalance(BigDecimal minimumBalance) { this.minimumBalance = minimumBalance; }
    
    public BigDecimal getDailyTransactionLimit() { return dailyTransactionLimit; }
    public void setDailyTransactionLimit(BigDecimal dailyTransactionLimit) { this.dailyTransactionLimit = dailyTransactionLimit; }
    
    public BigDecimal getMonthlyTransactionLimit() { return monthlyTransactionLimit; }
    public void setMonthlyTransactionLimit(BigDecimal monthlyTransactionLimit) { this.monthlyTransactionLimit = monthlyTransactionLimit; }
    

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }
    
    public boolean hasSufficientBalance(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }
    
    public boolean meetsMinimumBalance() {
        return balance.compareTo(minimumBalance) >= 0;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", status=" + status +
                '}';
    }
} 