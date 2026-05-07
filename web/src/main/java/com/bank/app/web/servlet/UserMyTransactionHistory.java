package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.Transaction;
import com.bank.app.core.model.User;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.service.TransactionService;
import com.bank.app.core.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@WebServlet("/user/my_transaction_history")
public class UserMyTransactionHistory extends HttpServlet {
    private TransactionService transactionService;
    private AccountService accountService;
    private UserService userService;

    private void initServices() throws NamingException {
        InitialContext ctx = new InitialContext();
        if (transactionService == null) {
            transactionService = (TransactionService) ctx.lookup("java:global/banking-system-ear/account-module/TransactionSessionBean!com.bank.app.core.service.TransactionService");
        }
        if (accountService == null) {
            accountService = (AccountService) ctx.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        }
        if (userService == null) {
            userService = (UserService) ctx.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        }
    }

    public static class TransactionView {
        private Transaction tx;
        private String status;
        public TransactionView(Transaction tx, String status) {
            this.tx = tx;
            this.status = status;
        }
        public Transaction getTx() { return tx; }
        public String getStatus() { return status; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            initServices();
            Principal principal = req.getUserPrincipal();
            User user = null;
            if (principal != null) {
                String email = principal.getName();
                user = userService.getUserByEmail(email);
            }
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            List<Account> allAccounts = accountService.getAccountsByUser(user.getId());
            List<Long> userAccountIds = allAccounts.stream().map(Account::getId).collect(Collectors.toList());
            final User userFinal = user;
            List<Transaction> transactions = transactionService.getAllTransactions().stream()
                .filter(t -> t.getUserId() != null && t.getUserId().equals(userFinal.getId()))
                .collect(Collectors.toList());

            // Filtering
            String typeParam = req.getParameter("type");
            String startDateParam = req.getParameter("startDate");
            String endDateParam = req.getParameter("endDate");
            String accountNumberParam = req.getParameter("accountNumber");

            if (typeParam != null && !typeParam.isEmpty()) {
                transactions = transactions.stream()
                    .filter(t -> t.getType().toString().equalsIgnoreCase(typeParam))
                    .collect(Collectors.toList());
            }
            if (startDateParam != null && !startDateParam.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startDateParam);
                transactions = transactions.stream()
                    .filter(t -> !t.getDate().toLocalDate().isBefore(startDate))
                    .collect(Collectors.toList());
            }
            if (endDateParam != null && !endDateParam.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endDateParam);
                transactions = transactions.stream()
                    .filter(t -> !t.getDate().toLocalDate().isAfter(endDate))
                    .collect(Collectors.toList());
            }
            if (accountNumberParam != null && !accountNumberParam.isEmpty()) {
                List<Account> filteredAccounts = allAccounts.stream()
                    .filter(a -> a.getAccountNumber().equals(accountNumberParam))
                    .collect(Collectors.toList());
                List<Long> filteredAccountIds = filteredAccounts.stream().map(Account::getId).collect(Collectors.toList());
                transactions = transactions.stream()
                    .filter(t -> filteredAccountIds.contains(t.getAccountId()))
                    .collect(Collectors.toList());
            }

            // Build TransactionView list (status is always COMPLETED for user)
            List<TransactionView> txViews = transactions.stream()
                .map(tx -> new TransactionView(tx, "COMPLETED"))
                .collect(Collectors.toList());

            req.setAttribute("transactions", txViews);
            req.setAttribute("allAccounts", allAccounts);
            req.getRequestDispatcher("/user/my_transaction_history.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Failed to load transaction history", e);
        }
    }
} 