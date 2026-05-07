package com.bank.app.web.servlet;

import com.bank.app.core.model.Deposit;
import com.bank.app.core.model.Account;
import com.bank.app.core.service.DepositService;
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

@WebServlet("/admin/view_deposits")
public class ViewDeposits extends HttpServlet {
    private DepositService depositService;
    private AccountService accountService;

    private void initService() throws NamingException {
        if (depositService == null) {
            InitialContext context = new InitialContext();
            depositService = (DepositService) context.lookup("java:global/banking-system-ear/account-module/DepositSessionBean!com.bank.app.core.service.DepositService");
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
            List<Deposit> deposits = depositService.getAllDeposits();
            req.setAttribute("deposits", deposits);
            // Build accountId -> accountNumber map
            Map<Long, String> accountNumberMap = new HashMap<>();
            if (deposits != null) {
                for (Deposit d : deposits) {
                    if (!accountNumberMap.containsKey(d.getAccountId())) {
                        Account acc = accountService.getAccountById(d.getAccountId());
                        accountNumberMap.put(d.getAccountId(), acc != null ? acc.getAccountNumber() : "N/A");
                    }
                }
            }
            req.setAttribute("accountNumberMap", accountNumberMap);
        } catch (Exception e) {
            req.setAttribute("deposits", new java.util.ArrayList<Deposit>());
            req.setAttribute("accountNumberMap", new HashMap<Long, String>());
        }
        req.getRequestDispatcher("/admin/view_deposits.jsp").forward(req, resp);
    }
} 