package com.bank.app.web.servlet;

import com.bank.app.core.service.AccountService;
import com.bank.app.core.exception.InsufficientFundsException;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/AccountOperation")
public class AccountOperation extends HttpServlet {
    @EJB
    private AccountService accountService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        Long accountId = Long.valueOf(req.getParameter("accountId"));
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));
        try {
            switch (action) {
                case "deposit":
                    accountService.deposit(accountId, amount);
                    break;
                case "withdraw":
                    accountService.withdraw(accountId, amount);
                    break;
                case "transfer":
                    Long toAccountId = Long.valueOf(req.getParameter("toAccountId"));
                    accountService.transfer(accountId, toAccountId, amount);
                    break;
            }
            resp.sendRedirect("user/index.jsp");
        } catch (InsufficientFundsException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("user/index.jsp").forward(req, resp);
        }
    }
} 