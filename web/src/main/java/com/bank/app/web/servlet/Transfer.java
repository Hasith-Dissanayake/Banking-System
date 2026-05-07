package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.model.User;
import com.bank.app.core.service.UserService;
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

@WebServlet("/admin/transfer")
public class Transfer extends HttpServlet {
    private AccountService accountService;
    private UserService userService;

    private void initServices() throws NamingException {
        if (accountService == null) {
            InitialContext context = new InitialContext();
            accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        }
        if (userService == null) {
            InitialContext context = new InitialContext();
            userService = (UserService) context.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            initServices();
            String action = req.getParameter("action");
            Principal principal = req.getUserPrincipal();
            boolean isAdmin = false;
            User user = null;
            if (principal != null) {
                String email = principal.getName();
                user = userService.getUserByEmail(email);
                if (user != null) {
                    isAdmin = user.getUserType().name().contains("ADMIN");
                }
            }
            if ("verify".equals(action)) {
                // Verify account numbers
                String fromAccountNumber = req.getParameter("fromAccountNumber");
                String toAccountNumber = req.getParameter("toAccountNumber");
                Account fromAccount = null;
                Account toAccount = null;
                if (fromAccountNumber != null && toAccountNumber != null) {
                    fromAccount = accountService.getAccountByAccountNumber(fromAccountNumber);
                    toAccount = accountService.getAccountByAccountNumber(toAccountNumber);
                    if (fromAccount != null && toAccount != null) {
                        // Ownership check (unless admin)
                        if (!isAdmin && (fromAccount.getUser() == null || user == null || !fromAccount.getUser().getId().equals(user.getId()))) {
                            req.setAttribute("error", "You do not own the source account.");
                        } else {
                            req.setAttribute("fromAccount", fromAccount);
                            req.setAttribute("toAccount", toAccount);
                        }
                    } else {
                        req.setAttribute("error", "Invalid account number(s). Please try again.");
                    }
                } else {
                    req.setAttribute("error", "Please enter both account numbers.");
                }
                req.setAttribute("fromAccountNumber", fromAccountNumber);
                req.setAttribute("toAccountNumber", toAccountNumber);
                req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                return;
            } else if ("transfer".equals(action)) {
                // Perform transfer
                String fromAccountIdStr = req.getParameter("fromAccountId");
                String toAccountIdStr = req.getParameter("toAccountId");
                String amountStr = req.getParameter("amount");
                if (fromAccountIdStr == null || toAccountIdStr == null || amountStr == null) {
                    req.setAttribute("error", "All fields are required.");
                    req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                    return;
                }
                if (user == null) {
                    req.setAttribute("error", "User not found. Please log in again.");
                    req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                    return;
                }
                Long fromAccountId = Long.valueOf(fromAccountIdStr);
                Long toAccountId = Long.valueOf(toAccountIdStr);
                BigDecimal amount = new BigDecimal(amountStr);
                Account fromAccount = accountService.getAccountById(fromAccountId);
                Account toAccount = accountService.getAccountById(toAccountId);
                if (fromAccount == null || toAccount == null) {
                    req.setAttribute("error", "Invalid account(s).");
                    req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                    return;
                }
                // Ownership check (unless admin)
                if (!isAdmin && (fromAccount.getUser() == null || user == null || !fromAccount.getUser().getId().equals(user.getId()))) {
                    req.setAttribute("error", "You do not own the source account.");
                    req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                    return;
                }
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    req.setAttribute("error", "Amount must be positive.");
                    req.setAttribute("fromAccount", fromAccount);
                    req.setAttribute("toAccount", toAccount);
                    req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                    return;
                }
                if (!accountService.isAccountActive(fromAccountId) || !accountService.isAccountActive(toAccountId)) {
                    req.setAttribute("error", "Both accounts must be active.");
                    req.setAttribute("fromAccount", fromAccount);
                    req.setAttribute("toAccount", toAccount);
                    req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                    return;
                }
                if (!accountService.hasSufficientBalance(fromAccountId, amount)) {
                    req.setAttribute("error", "Insufficient balance.");
                    req.setAttribute("fromAccount", fromAccount);
                    req.setAttribute("toAccount", toAccount);
                    req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                    return;
                }
                accountService.transfer(fromAccountId, toAccountId, amount, user);
                req.setAttribute("success", "Transfer successful.");
                req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
                return;
            } else {
                req.setAttribute("error", "Invalid action.");
                req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Transfer failed: " + e.getMessage());
            req.getRequestDispatcher("/admin/transfer.jsp").forward(req, resp);
        }
    }
} 