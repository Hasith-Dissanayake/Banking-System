package com.bank.app.core.service;

import com.bank.app.core.model.Deposit;
import java.util.List;

public interface DepositService {
    void createDeposit(Deposit deposit);
    Deposit getDepositById(Long id);
    List<Deposit> getAllDeposits();
    List<Deposit> getDepositsByAccount(Long accountId);
    List<Deposit> getDepositsByUser(Long userId);
} 