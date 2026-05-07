package com.bank.app.core.service;

import com.bank.app.core.model.BankBranch;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface BankBranchService {
    List<BankBranch> getAllBankBranches();
    List<BankBranch> getActiveBankBranches();
    BankBranch getBankBranchById(Long id);
    void addBankBranch(BankBranch bankBranch);
    void updateBankBranch(BankBranch bankBranch);
    void deleteBankBranch(Long id);
    void activateBankBranch(Long id);
    void deactivateBankBranch(Long id);
    boolean isBankBranchExists(String branchName);
} 