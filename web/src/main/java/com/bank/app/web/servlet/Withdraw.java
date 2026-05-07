package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.User;
import com.bank.app.core.model.Withdrawal;
import com.bank.app.core.model.WithdrawalStatus;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.service.UserService;
import com.bank.app.core.service.WithdrawalService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

@WebServlet("/admin/withdraw")
public class Withdraw extends HttpServlet {
    private AccountService accountService;
    private UserService userService;
    private WithdrawalService withdrawalService;

    private void initServices() throws NamingException {
        if (accountService == null) {
            InitialContext context = new InitialContext();
            accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        }
        if (userService == null) {
            InitialContext context = new InitialContext();
            userService = (UserService) context.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        }
        if (withdrawalService == null) {
            InitialContext context = new InitialContext();
            withdrawalService = (WithdrawalService) context.lookup("java:global/banking-system-ear/account-module/WithdrawalSessionBean!com.bank.app.core.service.WithdrawalService");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            initServices();
            List<Account> accounts = accountService.getAllAccounts();
            req.setAttribute("accounts", accounts);
        } catch (Exception e) {
            req.setAttribute("accounts", new java.util.ArrayList<Account>());
        }
        req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            initServices();
            String action = req.getParameter("action");
            if ("verify".equals(action)) {
                // Verify account number
                String accountNumber = req.getParameter("accountNumber");
                if (accountNumber == null || accountNumber.trim().isEmpty()) {
                    req.setAttribute("error", "Please enter an account number.");
                    req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                    return;
                }
                Account account = accountService.getAccountByAccountNumber(accountNumber.trim());
                if (account == null) {
                    req.setAttribute("error", "Account not found.");
                    req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                    return;
                }
                if (!account.isActive()) {
                    req.setAttribute("error", "Account is not active.");
                    req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                    return;
                }
                req.setAttribute("account", account);
                req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                return;
            } else if ("withdraw".equals(action)) {
                // Perform withdrawal
                String accountIdStr = req.getParameter("accountId");
                String amountStr = req.getParameter("amount");
                if (accountIdStr == null || amountStr == null) {
                    req.setAttribute("error", "All fields are required.");
                    req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                    return;
                }
                Long accountId = Long.valueOf(accountIdStr);
                Account account = accountService.getAccountById(accountId);
                if (account == null) {
                    req.setAttribute("error", "Account not found.");
                    req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                    return;
                }
                if (!account.isActive()) {
                    req.setAttribute("error", "Account is not active.");
                    req.setAttribute("account", account);
                    req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                    return;
                }
                BigDecimal amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    req.setAttribute("error", "Amount must be positive.");
                    req.setAttribute("account", account);
                    req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                    return;
                }
                if (!account.hasSufficientBalance(amount)) {
                    req.setAttribute("error", "Insufficient balance.");
                    req.setAttribute("account", account);
                    req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                    return;
                }
                // Get current admin user for audit
                java.security.Principal principal = req.getUserPrincipal();
                User user = null;
                if (principal != null) {
                    String email = principal.getName();
                    user = userService.getUserByEmail(email);
                }
                accountService.withdraw(accountId, amount, user != null ? user.getId() : null);
                // Create Withdrawal record for audit
                Withdrawal withdrawal = new Withdrawal(accountId, amount, user);
                withdrawal.setStatus(WithdrawalStatus.COMPLETED);
                withdrawalService.createWithdrawal(withdrawal);
                req.setAttribute("success", "Withdrawal successful.");
                req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
                return;
            } else {
                req.setAttribute("error", "Invalid action.");
                req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Withdrawal failed: " + e.getMessage());
            req.getRequestDispatcher("/admin/withdraw.jsp").forward(req, resp);
        }
    }
} 