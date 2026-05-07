package com.bank.app.web.servlet;

import com.bank.app.core.model.Transfer;
import com.bank.app.core.model.Account;
import com.bank.app.core.service.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/view_transfers")
public class ViewTransfers extends HttpServlet {
    @PersistenceContext
    private EntityManager em;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Transfer> transfers = em.createQuery("SELECT t FROM Transfer t ORDER BY t.createdDate DESC", Transfer.class).getResultList();
            req.setAttribute("transfers", transfers);

            // Build accountId -> accountNumber map
            Map<Long, String> accountNumberMap = new HashMap<>();
            if (!transfers.isEmpty()) {
                InitialContext context = new InitialContext();
                AccountService accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
                for (Transfer t : transfers) {
                    if (!accountNumberMap.containsKey(t.getFromAccountId())) {
                        Account fromAcc = accountService.getAccountById(t.getFromAccountId());
                        accountNumberMap.put(t.getFromAccountId(), fromAcc != null ? fromAcc.getAccountNumber() : "N/A");
                    }
                    if (!accountNumberMap.containsKey(t.getToAccountId())) {
                        Account toAcc = accountService.getAccountById(t.getToAccountId());
                        accountNumberMap.put(t.getToAccountId(), toAcc != null ? toAcc.getAccountNumber() : "N/A");
                    }
                }
            }
            req.setAttribute("accountNumberMap", accountNumberMap);
        } catch (Exception e) {
            req.setAttribute("transfers", new java.util.ArrayList<Transfer>());
            req.setAttribute("accountNumberMap", new HashMap<Long, String>());
        }
        req.getRequestDispatcher("/admin/view_transfers.jsp").forward(req, resp);
    }
} 