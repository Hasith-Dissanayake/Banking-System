package com.bank.app.core.service;

import com.bank.app.core.model.Withdrawal;
import java.util.List;

public interface WithdrawalService {
    void createWithdrawal(Withdrawal withdrawal);
    Withdrawal getWithdrawalById(Long id);
    List<Withdrawal> getAllWithdrawals();
    List<Withdrawal> getWithdrawalsByAccount(Long accountId);
    List<Withdrawal> getWithdrawalsByUser(Long userId);
} 