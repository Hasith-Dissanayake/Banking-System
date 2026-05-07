package com.bank.app.core.service;

import com.bank.app.core.model.ScheduledTransfer;
import jakarta.ejb.Remote;
import java.time.LocalDateTime;
import java.util.List;

@Remote
public interface ScheduledTransferService {
    List<ScheduledTransfer> getScheduledTransfersByUser(Long userId);
    List<ScheduledTransfer> getAllScheduledTransfers();
    List<ScheduledTransfer> getPendingScheduledTransfers();
    void createScheduledTransfer(ScheduledTransfer transfer);
    void updateScheduledTransfer(ScheduledTransfer transfer);
    void deleteScheduledTransfer(Long id);
    void executeScheduledTransfer(Long transferId);
} 