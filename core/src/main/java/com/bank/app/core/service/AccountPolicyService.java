package com.bank.app.core.service;

import com.bank.app.core.model.AccountPolicy;
import com.bank.app.core.model.AccountType;
import java.util.List;

public interface AccountPolicyService {
    AccountPolicy getPolicyByAccountType(AccountType accountType);
    List<AccountPolicy> getAllPolicies();
    void updatePolicy(AccountPolicy policy);
    void addPolicy(AccountPolicy policy);
    void deletePolicy(Long id);
} 