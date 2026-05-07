package com.bank.app.web.servlet;

import com.bank.app.core.model.AccountType;
import com.bank.app.core.service.AccountTypeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/accountTypeManagement")
@RolesAllowed({"SUPER_ADMIN"})
public class AccountTypeManagement extends HttpServlet {
    @EJB
    private AccountTypeService accountTypeService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        if (name == null || name.trim().isEmpty()) {
            response.sendRedirect("accountPolicy?error=missing_type_name");
            return;
        }
        try {
            AccountType existing = accountTypeService.getAccountTypeByName(name.trim());
            if (existing != null) {
                response.sendRedirect("accountPolicy?error=type_exists");
                return;
            }
            AccountType type = new AccountType(name.trim(), description);
            accountTypeService.addAccountType(type);
            response.sendRedirect("accountPolicy?success=type_added");
        } catch (Exception e) {
            response.sendRedirect("accountPolicy?error=type_add_failed");
        }
    }
} 