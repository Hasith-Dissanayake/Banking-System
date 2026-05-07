package com.bank.app.core.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "account_policies")
public class AccountPolicy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_type_id", nullable = false, unique = true)
    private AccountType accountType;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate = BigDecimal.ZERO;

    public AccountPolicy() {}

    public AccountPolicy(AccountType accountType, BigDecimal interestRate) {
        this.accountType = accountType;
        this.interestRate = interestRate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
} 