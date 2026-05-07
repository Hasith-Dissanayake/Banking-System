package com.bank.app.core.service;

import com.bank.app.core.model.AccountType;
import java.util.List;

public interface AccountTypeService {
    AccountType getAccountTypeById(Long id);
    AccountType getAccountTypeByName(String name);
    List<AccountType> getAllAccountTypes();
    void addAccountType(AccountType accountType);
    void updateAccountType(AccountType accountType);
    void deleteAccountType(Long id);
} 