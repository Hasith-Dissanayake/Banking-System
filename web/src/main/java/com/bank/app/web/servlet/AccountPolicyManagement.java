package com.bank.app.web.servlet;

import com.bank.app.core.model.AccountPolicy;
import com.bank.app.core.model.AccountType;
import com.bank.app.core.service.AccountPolicyService;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.service.AccountTypeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet({"/admin/accountPolicy", "/admin/accountPolicy/getInterestRate"})
@RolesAllowed({"SUPER_ADMIN"})
public class AccountPolicyManagement extends HttpServlet {
    @EJB
    private AccountPolicyService accountPolicyService;
    @EJB
    private AccountService accountService;
    @EJB
    private AccountTypeService accountTypeService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/admin/accountPolicy/getInterestRate".equals(path)) {
            String typeStr = request.getParameter("type");
            if (typeStr != null) {
                try {
                    AccountType type = accountTypeService.getAccountTypeByName(typeStr);
                    AccountPolicy policy = accountPolicyService.getPolicyByAccountType(type);
                    String rate = (policy != null) ? policy.getInterestRate().toString() : "0.00";
                    response.setContentType("text/plain");
                    response.getWriter().write(rate);
                } catch (Exception e) {
                    response.setContentType("text/plain");
                    response.getWriter().write("0.00");
                }
            } else {
                response.setContentType("text/plain");
                response.getWriter().write("0.00");
            }
            return;
        }
        List<AccountPolicy> policies = accountPolicyService.getAllPolicies();
        List<AccountType> accountTypes = accountTypeService.getAllAccountTypes();
        request.setAttribute("policies", policies);
        request.setAttribute("accountTypes", accountTypes);
        request.getRequestDispatcher("account_policy.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String accountTypeStr = request.getParameter("accountType");
        String interestRateStr = request.getParameter("interestRate");
        if ("applyToAll".equals(action)) {
            if (accountTypeStr == null || interestRateStr == null) {
                response.sendRedirect("accountPolicy?error=missing_fields");
                return;
            }
            try {
                AccountType accountType = accountTypeService.getAccountTypeByName(accountTypeStr);
                BigDecimal interestRate = new BigDecimal(interestRateStr);
                accountService.updateInterestRateForAllAccounts(accountType, interestRate);
                response.sendRedirect("accountPolicy?success=bulk_updated");
            } catch (Exception e) {
                response.sendRedirect("accountPolicy?error=bulk_update_failed");
            }
            return;
        }
        if (accountTypeStr == null || interestRateStr == null) {
            response.sendRedirect("accountPolicy?error=missing_fields");
            return;
        }
        try {
            AccountType accountType = accountTypeService.getAccountTypeByName(accountTypeStr);
            BigDecimal interestRate = new BigDecimal(interestRateStr);
            AccountPolicy policy = accountPolicyService.getPolicyByAccountType(accountType);
            if (policy == null) {
                policy = new AccountPolicy(accountType, interestRate);
                accountPolicyService.addPolicy(policy);
            } else {
                policy.setInterestRate(interestRate);
                accountPolicyService.updatePolicy(policy);
            }
            response.sendRedirect("accountPolicy?success=updated");
        } catch (Exception e) {
            response.sendRedirect("accountPolicy?error=update_failed");
        }
    }
} 