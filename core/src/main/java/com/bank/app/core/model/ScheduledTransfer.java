package com.bank.app.core.model;

import com.bank.app.core.model.User;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NamedQueries({
    @NamedQuery(name = "ScheduledTransfer.findAll", query = "select st from ScheduledTransfer st"),
    @NamedQuery(name = "ScheduledTransfer.findByUserId", query = "select st from ScheduledTransfer st join Account a on st.fromAccountId = a.id where a.user.id = :userId"),
    @NamedQuery(name = "ScheduledTransfer.findPending", query = "select st from ScheduledTransfer st where st.status = :status")
})
public class ScheduledTransfer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private LocalDateTime scheduledDate;
    private LocalDateTime executedDate;
    @Enumerated(EnumType.STRING)
    private TransferStatus status = TransferStatus.PENDING;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime cancelledDate;
    @ManyToOne
    private User createdBy;

    public ScheduledTransfer() {}

    public ScheduledTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount, LocalDateTime scheduledDate) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.scheduledDate = scheduledDate;
        this.status = TransferStatus.PENDING;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    public LocalDateTime getExecutedDate() { return executedDate; }
    public void setExecutedDate(LocalDateTime executedDate) { this.executedDate = executedDate; }
    public TransferStatus getStatus() { return status; }
    public void setStatus(TransferStatus status) { this.status = status; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    public LocalDateTime getCancelledDate() { return cancelledDate; }
    public void setCancelledDate(LocalDateTime cancelledDate) { this.cancelledDate = cancelledDate; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
} 