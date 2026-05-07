package com.bank.app.web.servlet;

import com.bank.app.core.model.Withdrawal;
import com.bank.app.core.service.WithdrawalService;
import com.bank.app.core.model.Account;
import com.bank.app.core.service.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/view_withdrawals")
public class ViewWithdrawals extends HttpServlet {
    private WithdrawalService withdrawalService;
    private AccountService accountService;

    private void initService() throws NamingException {
        if (withdrawalService == null) {
            InitialContext context = new InitialContext();
            withdrawalService = (WithdrawalService) context.lookup("java:global/banking-system-ear/account-module/WithdrawalSessionBean!com.bank.app.core.service.WithdrawalService");
        }
        if (accountService == null) {
            InitialContext context = new InitialContext();
            accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            initService();
            List<Withdrawal> withdrawals = withdrawalService.getAllWithdrawals();
            req.setAttribute("withdrawals", withdrawals);
            // Build accountId -> accountNumber map
            Map<Long, String> accountNumberMap = new HashMap<>();
            if (withdrawals != null) {
                for (Withdrawal w : withdrawals) {
                    if (!accountNumberMap.containsKey(w.getAccountId())) {
                        Account acc = accountService.getAccountById(w.getAccountId());
                        accountNumberMap.put(w.getAccountId(), acc != null ? acc.getAccountNumber() : "N/A");
                    }
                }
            }
            req.setAttribute("accountNumberMap", accountNumberMap);
        } catch (Exception e) {
            req.setAttribute("withdrawals", new java.util.ArrayList<Withdrawal>());
        }
        req.getRequestDispatcher("/admin/view_withdrawals.jsp").forward(req, resp);
    }
} 