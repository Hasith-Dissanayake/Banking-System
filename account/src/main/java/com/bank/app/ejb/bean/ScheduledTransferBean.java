package com.bank.app.ejb.bean;

import com.bank.app.core.model.ScheduledTransfer;
import com.bank.app.core.model.TransferStatus;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.service.ScheduledTransferService;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ScheduledTransferBean {
    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private AccountService accountService;
    
    @EJB
    private ScheduledTransferService scheduledTransferService;

    @Schedule(hour = "*", minute = "*/5", persistent = false)
    public void executeScheduledTransfers() {
        TypedQuery<ScheduledTransfer> query = em.createNamedQuery("ScheduledTransfer.findPending", ScheduledTransfer.class);
        query.setParameter("status", TransferStatus.PENDING);
        List<ScheduledTransfer> pendingTransfers = query.getResultList();
        LocalDateTime now = LocalDateTime.now();
        for (ScheduledTransfer transfer : pendingTransfers) {
            // Skip cancelled transfers (should not be in pending, but double-check)
            if (transfer.getStatus() == TransferStatus.CANCELLED) {
                System.out.println("[ScheduledTransfer] Skipping cancelled transfer ID: " + transfer.getId());
                continue;
            }
            if (transfer.getScheduledDate().isBefore(now) || transfer.getScheduledDate().isEqual(now)) {
                try {
                    accountService.transfer(
                        transfer.getFromAccountId(),
                        transfer.getToAccountId(),
                        transfer.getAmount(),
                        transfer.getCreatedBy() // Pass the user who scheduled it
                    );
                    transfer.setStatus(TransferStatus.COMPLETED);
                    transfer.setExecutedDate(now);
                    em.merge(transfer);
                    System.out.println("[ScheduledTransfer] Transfer completed. ID: " + transfer.getId());
                    // Notify user of successful transfer (email, etc.)
                } catch (Exception e) {
                    transfer.setStatus(TransferStatus.FAILED);
                    em.merge(transfer);
                    System.out.println("[ScheduledTransfer] Transfer failed. ID: " + transfer.getId() + ", Error: " + e.getMessage());
                    // Notify user of failed transfer (email, etc.)
                }
            } else {
                System.out.println("[ScheduledTransfer] Transfer not due yet. ID: " + transfer.getId());
            }
        }
    }
} 