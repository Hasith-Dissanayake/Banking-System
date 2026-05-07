package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.service.AccountService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import javax.naming.InitialContext;

@WebServlet("/admin/viewAccount")
@RolesAllowed({"ADMIN", "SUPER_ADMIN"})
public class ViewAccount extends HttpServlet {
    
    private AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Initialize service using JNDI lookup
            if (accountService == null) {
                InitialContext context = new InitialContext();
                accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
            }
            
            String accountIdStr = request.getParameter("id");
            
            if (accountIdStr == null || accountIdStr.trim().isEmpty()) {
                response.sendRedirect("accounts.jsp?error=invalid_account_id");
                return;
            }
            
            Long accountId = Long.parseLong(accountIdStr);
            Account account = accountService.getAccountById(accountId);
            
            if (account != null) {
                // Ensure all BigDecimal fields are non-null
                if (account.getBalance() == null) account.setBalance(java.math.BigDecimal.ZERO);
                if (account.getInterestRate() == null) account.setInterestRate(java.math.BigDecimal.ZERO);
                if (account.getMinimumBalance() == null) account.setMinimumBalance(java.math.BigDecimal.ZERO);
                if (account.getDailyTransactionLimit() == null) account.setDailyTransactionLimit(java.math.BigDecimal.ZERO);
                if (account.getMonthlyTransactionLimit() == null) account.setMonthlyTransactionLimit(java.math.BigDecimal.ZERO);
                if (account.getMonthlyIncome() == null) account.setMonthlyIncome(java.math.BigDecimal.ZERO);
                request.setAttribute("account", account);
                request.getRequestDispatcher("view_account.jsp").forward(request, response);
            } else {
                response.sendRedirect("accounts.jsp?error=account_not_found");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("accounts.jsp?error=invalid_account_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("accounts.jsp?error=view_failed");
        }
    }
} 