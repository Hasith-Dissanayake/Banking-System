package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.AccountType;
import com.bank.app.core.model.User;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.bank.app.core.model.BankBranch;
import com.bank.app.core.model.AccountStatus;
import com.bank.app.core.service.BankBranchService;
import com.bank.app.core.service.AccountPolicyService;
import com.bank.app.core.model.AccountPolicy;
import com.bank.app.core.service.AccountTypeService;

@WebServlet("/admin/accounts")
@RolesAllowed({"ADMIN", "SUPER_ADMIN"})
public class AccountManagement extends HttpServlet {
    
    @EJB
    private AccountService accountService;
    
    @EJB
    private UserService userService;

    @EJB
    private BankBranchService bankBranchService;

    @EJB
    private AccountPolicyService accountPolicyService;

    @EJB
    private AccountTypeService accountTypeService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            request.setAttribute("accounts", accounts);
            List<AccountType> accountTypes = accountTypeService.getAllAccountTypes();
            request.setAttribute("accountTypes", accountTypes);
            request.getRequestDispatcher("accounts.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("accounts.jsp?error=load_failed");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            addAccount(request, response);
        } else if ("update".equals(action)) {
            updateAccount(request, response);
        } else if ("delete".equals(action)) {
            deleteAccount(request, response);
        } else {
            response.sendRedirect("accounts.jsp?error=invalid_action");
        }
    }
    
    private void addAccount(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // Ignore accountNumber from request, generate it instead
        String generatedAccountNumber = generateAccountNumber();
        String balanceStr = request.getParameter("balance");
        String accountTypeStr = request.getParameter("accountType");
        String userIdStr = request.getParameter("userId");
        
        // Customer information
        String customerName = request.getParameter("customerName");
        String nic = request.getParameter("nic");
        String email = request.getParameter("email");
        String contactNumber = request.getParameter("contactNumber");
        String birthdayStr = request.getParameter("birthday");
        String address = request.getParameter("address");
        String occupation = request.getParameter("occupation");
        String monthlyIncomeStr = request.getParameter("monthlyIncome");
        String bankBranchIdStr = request.getParameter("bankBranchId");

        // Validation
        if (generatedAccountNumber == null || generatedAccountNumber.trim().isEmpty() ||
            balanceStr == null || balanceStr.trim().isEmpty() ||
            accountTypeStr == null || accountTypeStr.trim().isEmpty() ||
            customerName == null || customerName.trim().isEmpty() ||
            nic == null || nic.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            contactNumber == null || contactNumber.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            bankBranchIdStr == null || bankBranchIdStr.trim().isEmpty()) {
            response.sendRedirect("accounts.jsp?error=all_fields_required");
            return;
        }

        try {
            BigDecimal balance = new BigDecimal(balanceStr);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                response.sendRedirect("accounts.jsp?error=negative_balance");
                return;
            }

            AccountType accountType = accountTypeService.getAccountTypeByName(accountTypeStr);
            Long userId = userIdStr != null && !userIdStr.trim().isEmpty() ? Long.parseLong(userIdStr) : null;
            Long bankBranchId = Long.parseLong(bankBranchIdStr);
            
            // Parse optional fields
            LocalDate birthday = null;
            if (birthdayStr != null && !birthdayStr.trim().isEmpty()) {
                birthday = LocalDate.parse(birthdayStr);
            }
            
            BigDecimal monthlyIncome = null;
            if (monthlyIncomeStr != null && !monthlyIncomeStr.trim().isEmpty()) {
                monthlyIncome = new BigDecimal(monthlyIncomeStr);
            }

            // Read daily and monthly transaction limits
            String dailyLimitStr = request.getParameter("dailyTransactionLimit");
            String monthlyLimitStr = request.getParameter("monthlyTransactionLimit");
            BigDecimal dailyLimit = (dailyLimitStr != null && !dailyLimitStr.isEmpty()) ? new BigDecimal(dailyLimitStr) : new BigDecimal("200000");
            BigDecimal monthlyLimit = (monthlyLimitStr != null && !monthlyLimitStr.isEmpty()) ? new BigDecimal(monthlyLimitStr) : new BigDecimal("5000000");

            // Validate bank branch
            BankBranch bankBranch = bankBranchService.getBankBranchById(bankBranchId);
            if (bankBranch == null || !bankBranch.getIsActive()) {
                response.sendRedirect("accounts.jsp?error=invalid_bank_branch");
                return;
            }

            // Check for duplicate account number
            Account existingAccount = accountService.getAccountByAccountNumber(generatedAccountNumber.trim());
            if (existingAccount != null) {
                response.sendRedirect("accounts.jsp?error=account_exists");
                return;
            }
            
            // Create account with customer information
            Account account = new Account();
            account.setAccountNumber(generatedAccountNumber);
            account.setBalance(balance);
            account.setAccountType(accountType);
            User user = null;
            if (userId != null) {
                user = userService.getUserById(userId);
            }
            account.setUser(user);
            account.setCustomerName(customerName.trim());
            account.setNic(nic.trim());
            account.setEmail(email.trim());
            account.setContactNumber(contactNumber.trim());
            account.setBirthday(birthday);
            account.setAddress(address.trim());
            account.setOccupation(occupation != null ? occupation.trim() : null);
            account.setMonthlyIncome(monthlyIncome);
            account.setBankBranch(bankBranch);
            
            // Set status based on initial balance
            if (balance.compareTo(new BigDecimal("3000")) >= 0) {
                account.setStatus(AccountStatus.ACTIVE);
            } else {
                account.setStatus(AccountStatus.INACTIVE);
            }
            account.setOpeningDate(LocalDate.now());
            
            // Set account-specific defaults using AccountPolicy
            AccountPolicy policy = accountPolicyService.getPolicyByAccountType(accountType);
            if (policy != null) {
                account.setInterestRate(policy.getInterestRate());
            } else {
                account.setInterestRate(BigDecimal.ZERO);
            }
                    account.setMinimumBalance(new BigDecimal("100"));
                    account.setDailyTransactionLimit(new BigDecimal("50000"));
                    account.setMonthlyTransactionLimit(new BigDecimal("500000"));

            account.setDailyTransactionLimit(dailyLimit);
            account.setMonthlyTransactionLimit(monthlyLimit);
            
            accountService.createAccount(account);
            response.sendRedirect("accounts.jsp?success=account_added");
        } catch (NumberFormatException e) {
            response.sendRedirect("accounts.jsp?error=invalid_data");
        } catch (IllegalArgumentException e) {
            response.sendRedirect("accounts.jsp?error=invalid_account_type");
        } catch (Exception e) {
            response.sendRedirect("accounts.jsp?error=add_failed");
        }
    }
    
    private void updateAccount(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String idStr = request.getParameter("id");
        String accountNumber = request.getParameter("accountNumber");
        String balanceStr = request.getParameter("balance");
        String accountTypeStr = request.getParameter("accountType");
        String statusStr = request.getParameter("status");
        
        // Customer information
        String customerName = request.getParameter("customerName");
        String nic = request.getParameter("nic");
        String email = request.getParameter("email");
        String contactNumber = request.getParameter("contactNumber");
        String birthdayStr = request.getParameter("birthday");
        String address = request.getParameter("address");
        String occupation = request.getParameter("occupation");
        String monthlyIncomeStr = request.getParameter("monthlyIncome");
        String bankBranchIdStr = request.getParameter("bankBranchId");
        String minimumBalanceStr = request.getParameter("minimumBalance");
        String dailyTransactionLimitStr = request.getParameter("dailyTransactionLimit");
        String monthlyTransactionLimitStr = request.getParameter("monthlyTransactionLimit");

        // Validation
        if (idStr == null || idStr.trim().isEmpty() ||
            accountNumber == null || accountNumber.trim().isEmpty() ||
            balanceStr == null || balanceStr.trim().isEmpty() ||
            accountTypeStr == null || accountTypeStr.trim().isEmpty() ||
            customerName == null || customerName.trim().isEmpty() ||
            nic == null || nic.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            contactNumber == null || contactNumber.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            bankBranchIdStr == null || bankBranchIdStr.trim().isEmpty()) {
            response.sendRedirect("accounts.jsp?error=all_fields_required");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            BigDecimal balance = new BigDecimal(balanceStr);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                response.sendRedirect("accounts.jsp?error=negative_balance");
                return;
            }

            AccountType accountType = accountTypeService.getAccountTypeByName(accountTypeStr);
            AccountStatus status = statusStr != null ? AccountStatus.valueOf(statusStr) : AccountStatus.ACTIVE;
            Long bankBranchId = Long.parseLong(bankBranchIdStr);
            
            // Parse optional fields
            LocalDate birthday = null;
            if (birthdayStr != null && !birthdayStr.trim().isEmpty()) {
                birthday = LocalDate.parse(birthdayStr);
            }
            
            BigDecimal monthlyIncome = null;
            if (monthlyIncomeStr != null && !monthlyIncomeStr.trim().isEmpty()) {
                monthlyIncome = new BigDecimal(monthlyIncomeStr);
            }

            BigDecimal minimumBalance = null;
            if (minimumBalanceStr != null && !minimumBalanceStr.trim().isEmpty()) {
                minimumBalance = new BigDecimal(minimumBalanceStr);
            }
            BigDecimal dailyTransactionLimit = null;
            if (dailyTransactionLimitStr != null && !dailyTransactionLimitStr.trim().isEmpty()) {
                dailyTransactionLimit = new BigDecimal(dailyTransactionLimitStr);
            }
            BigDecimal monthlyTransactionLimit = null;
            if (monthlyTransactionLimitStr != null && !monthlyTransactionLimitStr.trim().isEmpty()) {
                monthlyTransactionLimit = new BigDecimal(monthlyTransactionLimitStr);
            }

            // Validate bank branch
            BankBranch bankBranch = bankBranchService.getBankBranchById(bankBranchId);
            if (bankBranch == null || !bankBranch.getIsActive()) {
                response.sendRedirect("accounts.jsp?error=invalid_bank_branch");
                return;
            }

            Account account = accountService.getAccountById(id);
            if (account == null) {
                response.sendRedirect("accounts.jsp?error=account_not_found");
                return;
            }
            
            // Check if account number is being changed and if it already exists
            if (!accountNumber.trim().equals(account.getAccountNumber())) {
                Account existing = accountService.getAccountByAccountNumber(accountNumber.trim());
                if (existing != null) {
                    response.sendRedirect("accounts.jsp?error=account_exists");
                    return;
                }
            }
            
            // Check if NIC is being changed and if it already exists
            if (!nic.trim().equals(account.getNic())) {
                Account existingNic = accountService.getAccountByNic(nic.trim());
                if (existingNic != null) {
                    response.sendRedirect("accounts.jsp?error=nic_exists");
                    return;
                }
            }
            
            // Check if email is being changed and if it already exists
            if (!email.trim().equals(account.getEmail())) {
                Account existingEmail = accountService.getAccountByEmail(email.trim());
                if (existingEmail != null) {
                    response.sendRedirect("accounts.jsp?error=email_exists");
                    return;
                }
            }

            // Update account information
            account.setAccountNumber(accountNumber.trim());
            account.setBalance(balance);
            AccountType oldType = account.getAccountType();
            account.setAccountType(accountType);
            if (!oldType.equals(accountType)) {
                AccountPolicy policy = accountPolicyService.getPolicyByAccountType(accountType);
                if (policy != null) {
                    account.setInterestRate(policy.getInterestRate());
                } else {
                    account.setInterestRate(BigDecimal.ZERO);
                }
            }
            account.setStatus(status);
            account.setCustomerName(customerName.trim());
            account.setNic(nic.trim());
            account.setEmail(email.trim());
            account.setContactNumber(contactNumber.trim());
            account.setBirthday(birthday);
            account.setAddress(address.trim());
            account.setOccupation(occupation != null ? occupation.trim() : null);
            account.setMonthlyIncome(monthlyIncome);
            account.setBankBranch(bankBranch);
            if (minimumBalance != null) account.setMinimumBalance(minimumBalance);
            if (dailyTransactionLimit != null) account.setDailyTransactionLimit(dailyTransactionLimit);
            if (monthlyTransactionLimit != null) account.setMonthlyTransactionLimit(monthlyTransactionLimit);
            accountService.updateAccount(account);
            response.sendRedirect("accounts.jsp?success=account_updated");
        } catch (NumberFormatException e) {
            response.sendRedirect("accounts.jsp?error=invalid_data");
        } catch (IllegalArgumentException e) {
            response.sendRedirect("accounts.jsp?error=invalid_account_type");
        } catch (Exception e) {
            response.sendRedirect("accounts.jsp?error=update_failed");
        }
    }
    
    private void deleteAccount(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect("accounts.jsp?error=invalid_account_id");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            Account account = accountService.getAccountById(id);
            
            if (account == null) {
                response.sendRedirect("accounts.jsp?error=account_not_found");
                return;
            }
            
            // Check if account has balance
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                response.sendRedirect("accounts.jsp?error=cannot_delete_with_balance");
                return;
            }

            accountService.deleteAccount(id);
            response.sendRedirect("accounts.jsp?success=account_deleted");
        } catch (NumberFormatException e) {
            response.sendRedirect("accounts.jsp?error=invalid_account_id");
        } catch (Exception e) {
            response.sendRedirect("accounts.jsp?error=delete_failed");
        }
    }

    private String generateAccountNumber() {
        //  ACC + currentTimeMillis
        return "ACC" + System.currentTimeMillis();
    }
} 