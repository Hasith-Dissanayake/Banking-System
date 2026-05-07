package com.bank.app.core.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@NamedQueries({
    @NamedQuery(name = "Transaction.findAll", query = "select t from Transaction t"),
    @NamedQuery(name = "Transaction.findByAccountId", query = "select t from Transaction t where t.accountId = :accountId"),
    @NamedQuery(name = "Transaction.findByUserId", query = "select t from Transaction t join Account a on t.accountId = a.id where a.user.id = :userId"),
    @NamedQuery(name = "Transaction.findByDateRange", query = "select t from Transaction t where t.date between :startDate and :endDate")
})
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 255)
    private String description;

    @Column(name = "user_id")
    private Long userId;

    public Transaction() {}

    public Transaction(Long accountId, TransactionType type, BigDecimal amount, LocalDateTime date, String description, Long userId) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
} 