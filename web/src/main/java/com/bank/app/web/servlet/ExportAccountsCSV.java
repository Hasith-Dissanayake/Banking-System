package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.service.AccountService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/ExportAccountsCSV")
public class ExportAccountsCSV extends HttpServlet {
    @EJB
    private AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=accounts.csv");
        PrintWriter out = resp.getWriter();
        out.println("ID,Account Number,Balance,Type,User ID");
        List<Account> accounts = accountService.getAllAccounts();
        for (Account account : accounts) {
            out.printf("%d,%s,%s,%s,%d\n",
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType() != null ? account.getAccountType().getName() : "",
                account.getUser() != null ? account.getUser().getId() : null
            );
        }
        out.flush();
    }
} 