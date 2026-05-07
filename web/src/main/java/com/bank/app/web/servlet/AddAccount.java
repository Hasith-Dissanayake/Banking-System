package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.AccountType;
import com.bank.app.core.model.User;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.service.AccountTypeService;
import com.bank.app.core.service.UserService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/admin/AddAccount")
public class AddAccount extends HttpServlet {
    @EJB
    private AccountService accountService;
    @EJB
    private UserService userService;
    @EJB
    private AccountTypeService accountTypeService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<AccountType> accountTypes = accountTypeService.getAllAccountTypes();
        req.setAttribute("accountTypes", accountTypes);
        req.getRequestDispatcher("add_account.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountNumber = req.getParameter("accountNumber");
        BigDecimal balance = new BigDecimal(req.getParameter("balance"));
        String accountTypeName = req.getParameter("accountType");
        AccountType accountType = accountTypeService.getAccountTypeByName(accountTypeName);
        Long userId = Long.valueOf(req.getParameter("userId"));
        User user = userService.getUserById(userId);

        // Check for duplicate account number
        Account existing = accountService.getAccountByAccountNumber(accountNumber);
        if (existing != null) {
            req.getRequestDispatcher("/admin/duplicate.jsp").forward(req, resp);
            return;
        }

        Account account = new Account(accountNumber, balance, accountType, user);
        accountService.createAccount(account);
        resp.sendRedirect("admin/index.jsp");
    }
} 