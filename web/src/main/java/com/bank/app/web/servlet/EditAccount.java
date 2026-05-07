package com.bank.app.web.servlet;

import com.bank.app.core.model.Account;
import com.bank.app.core.model.BankBranch;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.service.BankBranchService;
import com.bank.app.core.model.AccountType;
import com.bank.app.core.service.AccountTypeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import javax.naming.InitialContext;

@WebServlet("/admin/editAccount")
@RolesAllowed({"ADMIN", "SUPER_ADMIN"})
public class EditAccount extends HttpServlet {
    
    private AccountService accountService;
    private BankBranchService bankBranchService;
    private AccountTypeService accountTypeService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {

            if (accountService == null) {
                InitialContext context = new InitialContext();
                accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
            }
            
            if (bankBranchService == null) {
                InitialContext context = new InitialContext();
                bankBranchService = (BankBranchService) context.lookup("java:global/banking-system-ear/auth-module/BankBranchSessionBean!com.bank.app.core.service.BankBranchService");
            }

            if (accountTypeService == null) {
                InitialContext context = new InitialContext();
                accountTypeService = (AccountTypeService) context.lookup("java:global/banking-system-ear/account-module/AccountTypeSessionBean!com.bank.app.core.service.AccountTypeService");
            }
            
            String accountIdStr = request.getParameter("id");
            
            if (accountIdStr == null || accountIdStr.trim().isEmpty()) {
                response.sendRedirect("accounts.jsp?error=invalid_account_id");
                return;
            }
            
            Long accountId = Long.parseLong(accountIdStr);
            Account account = accountService.getAccountById(accountId);
            
            if (account != null) {
                List<BankBranch> bankBranches = bankBranchService.getActiveBankBranches();
                List<AccountType> accountTypes = accountTypeService.getAllAccountTypes();
                
                request.setAttribute("account", account);
                request.setAttribute("bankBranches", bankBranches);
                request.setAttribute("accountTypes", accountTypes);
                request.getRequestDispatcher("edit_account.jsp").forward(request, response);
            } else {
                response.sendRedirect("accounts.jsp?error=account_not_found");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("accounts.jsp?error=invalid_account_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("accounts.jsp?error=edit_failed");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String accountTypeName = request.getParameter("accountType");
            AccountType accountType = accountTypeService.getAccountTypeByName(accountTypeName);

            String accountIdStr = request.getParameter("id");
            Long accountId = Long.parseLong(accountIdStr);

            Account account = accountService.getAccountById(accountId);
            if (account != null) {
                account.setAccountType(accountType);
                accountService.updateAccount(account);
                response.sendRedirect("accounts.jsp?success=account_updated");
            } else {
                response.sendRedirect("accounts.jsp?error=account_not_found");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("accounts.jsp?error=invalid_account_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("accounts.jsp?error=update_failed");
        }
    }
} 