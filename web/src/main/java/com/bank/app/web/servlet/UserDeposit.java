package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.User;
import com.bank.app.core.model.Deposit;
import com.bank.app.core.model.DepositStatus;
import com.bank.app.core.model.AccountStatus;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.service.UserService;
import com.bank.app.core.service.DepositService;
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

@WebServlet("/user/deposit")
public class UserDeposit extends HttpServlet {
    private AccountService accountService;
    private UserService userService;
    private DepositService depositService;

    private void initServices() throws NamingException {
        if (accountService == null) {
            InitialContext context = new InitialContext();
            accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        }
        if (userService == null) {
            InitialContext context = new InitialContext();
            userService = (UserService) context.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        }
        if (depositService == null) {
            InitialContext context = new InitialContext();
            depositService = (DepositService) context.lookup("java:global/banking-system-ear/account-module/DepositSessionBean!com.bank.app.core.service.DepositService");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            initServices();
            String action = req.getParameter("action");
            Principal principal = req.getUserPrincipal();
            User user = null;
            if (principal != null) {
                String email = principal.getName();
                user = userService.getUserByEmail(email);
            }
            if (user == null) {
                req.setAttribute("error", "User not found. Please log in again.");
                req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
                return;
            }
            if ("verify".equals(action)) {
                // Verify account number
                String accountNumber = req.getParameter("accountNumber");
                Account account = null;
                if (accountNumber != null) {
                    account = accountService.getAccountByAccountNumber(accountNumber);
                    if (account != null) {
                        req.setAttribute("account", account);
                    } else {
                        req.setAttribute("error", "Invalid account number. Please try again.");
                    }
                } else {
                    req.setAttribute("error", "Please enter an account number.");
                }
                req.setAttribute("accountNumber", accountNumber);
                req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
                return;
            } else if ("deposit".equals(action)) {
                // Perform deposit
                String accountIdStr = req.getParameter("accountId");
                String amountStr = req.getParameter("amount");
                if (accountIdStr == null || amountStr == null) {
                    req.setAttribute("error", "All fields are required.");
                    req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
                    return;
                }
                Long accountId = Long.valueOf(accountIdStr);
                BigDecimal amount = new BigDecimal(amountStr);
                Account account = accountService.getAccountById(accountId);
                if (account == null) {
                    req.setAttribute("error", "Invalid account.");
                    req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
                    return;
                }
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    req.setAttribute("error", "Amount must be positive.");
                    req.setAttribute("account", account);
                    req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
                    return;
                }
                if (account.getStatus() != AccountStatus.ACTIVE && account.getStatus() != AccountStatus.INACTIVE) {
                    req.setAttribute("error", "Account is not eligible for deposit.");
                    req.setAttribute("account", account);
                    req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
                    return;
                }
                accountService.deposit(accountId, amount, user.getId());
                // Create Deposit record for audit
                Deposit deposit = new Deposit(accountId, amount, user);
                deposit.setStatus(DepositStatus.COMPLETED);
                depositService.createDeposit(deposit);
                req.setAttribute("success", "Deposit successful.");
                req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
                return;
            } else {
                req.setAttribute("error", "Invalid action.");
                req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Deposit failed: " + e.getMessage());
            req.getRequestDispatcher("/user/user_deposit.jsp").forward(req, resp);
        }
    }
} 