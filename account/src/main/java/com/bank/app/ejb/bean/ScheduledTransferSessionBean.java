package com.bank.app.ejb.bean;

import com.bank.app.core.model.ScheduledTransfer;
import com.bank.app.core.service.ScheduledTransferService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ScheduledTransferSessionBean implements ScheduledTransferService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ScheduledTransfer> getScheduledTransfersByUser(Long userId) {
        return em.createNamedQuery("ScheduledTransfer.findByUserId", ScheduledTransfer.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<ScheduledTransfer> getAllScheduledTransfers() {
        return em.createNamedQuery("ScheduledTransfer.findAll", ScheduledTransfer.class).getResultList();
    }

    @Override
    public List<ScheduledTransfer> getPendingScheduledTransfers() {
        return em.createNamedQuery("ScheduledTransfer.findPending", ScheduledTransfer.class)
                .setParameter("status", com.bank.app.core.model.TransferStatus.PENDING)
                .getResultList();
    }

    @Override
    public void createScheduledTransfer(ScheduledTransfer transfer) {
        em.persist(transfer);
    }

    @Override
    public void updateScheduledTransfer(ScheduledTransfer transfer) {
        em.merge(transfer);
    }

    @Override
    public void deleteScheduledTransfer(Long id) {
        ScheduledTransfer transfer = em.find(ScheduledTransfer.class, id);
        if (transfer != null) {
            em.remove(transfer);
        }
    }

    @Override
    public void executeScheduledTransfer(Long transferId) {
        ScheduledTransfer transfer = em.find(ScheduledTransfer.class, transferId);
        if (transfer != null && transfer.getStatus() == com.bank.app.core.model.TransferStatus.PENDING) {
            // Execute the transfer logic here
            // This would typically involve calling the AccountService transfer method
            transfer.setStatus(com.bank.app.core.model.TransferStatus.COMPLETED);
            em.merge(transfer);
        }
    }
} 