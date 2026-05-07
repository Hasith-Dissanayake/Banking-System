package com.bank.app.web.servlet;

import com.bank.app.core.model.BankBranch;
import com.bank.app.core.service.BankBranchService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/bankBranches")
@RolesAllowed({"SUPER_ADMIN"})
public class BankBranchManagement extends HttpServlet {
    
    @EJB
    private BankBranchService bankBranchService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<BankBranch> bankBranches = bankBranchService.getAllBankBranches();
            request.setAttribute("bankBranches", bankBranches);
            request.getRequestDispatcher("bank_branches.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("bank_branches.jsp?error=load_failed");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            addBankBranch(request, response);
        } else if ("update".equals(action)) {
            updateBankBranch(request, response);
        } else if ("delete".equals(action)) {
            deleteBankBranch(request, response);
        } else if ("activate".equals(action)) {
            activateBankBranch(request, response);
        } else if ("deactivate".equals(action)) {
            deactivateBankBranch(request, response);
        } else {
            response.sendRedirect("bank_branches.jsp?error=invalid_action");
        }
    }
    
    private void addBankBranch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String branchName = request.getParameter("branchName");
        
        if (branchName == null || branchName.trim().isEmpty()) {
            response.sendRedirect("bank_branches.jsp?error=name_required");
            return;
        }
        
        try {
            if (bankBranchService.isBankBranchExists(branchName.trim())) {
                response.sendRedirect("bank_branches.jsp?error=branch_exists");
                return;
            }
            
            BankBranch bankBranch = new BankBranch(branchName.trim());
            bankBranchService.addBankBranch(bankBranch);
            response.sendRedirect("bank_branches.jsp?success=branch_added");
        } catch (Exception e) {
            response.sendRedirect("bank_branches.jsp?error=add_failed");
        }
    }
    
    private void updateBankBranch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String idStr = request.getParameter("id");
        String branchName = request.getParameter("branchName");
        
        if (idStr == null || branchName == null || branchName.trim().isEmpty()) {
            response.sendRedirect("bank_branches.jsp?error=invalid_data");
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            BankBranch bankBranch = bankBranchService.getBankBranchById(id);
            
            if (bankBranch == null) {
                response.sendRedirect("bank_branches.jsp?error=branch_not_found");
                return;
            }
            
            // Check if the new name already exists
            if (!branchName.trim().equals(bankBranch.getBranchName()) && 
                bankBranchService.isBankBranchExists(branchName.trim())) {
                response.sendRedirect("bank_branches.jsp?error=branch_exists");
                return;
            }
            
            bankBranch.setBranchName(branchName.trim());
            bankBranchService.updateBankBranch(bankBranch);
            response.sendRedirect("bank_branches.jsp?success=branch_updated");
        } catch (NumberFormatException e) {
            response.sendRedirect("bank_branches.jsp?error=invalid_id");
        } catch (Exception e) {
            response.sendRedirect("bank_branches.jsp?error=update_failed");
        }
    }
    
    private void deleteBankBranch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null) {
            response.sendRedirect("bank_branches.jsp?error=invalid_id");
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            bankBranchService.deleteBankBranch(id);
            response.sendRedirect("bank_branches.jsp?success=branch_deleted");
        } catch (NumberFormatException e) {
            response.sendRedirect("bank_branches.jsp?error=invalid_id");
        } catch (Exception e) {
            response.sendRedirect("bank_branches.jsp?error=delete_failed");
        }
    }
    
    private void activateBankBranch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null) {
            response.sendRedirect("bank_branches.jsp?error=invalid_id");
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            bankBranchService.activateBankBranch(id);
            response.sendRedirect("bank_branches.jsp?success=branch_activated");
        } catch (NumberFormatException e) {
            response.sendRedirect("bank_branches.jsp?error=invalid_id");
        } catch (Exception e) {
            response.sendRedirect("bank_branches.jsp?error=activation_failed");
        }
    }
    
    private void deactivateBankBranch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null) {
            response.sendRedirect("bank_branches.jsp?error=invalid_id");
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            bankBranchService.deactivateBankBranch(id);
            response.sendRedirect("bank_branches.jsp?success=branch_deactivated");
        } catch (NumberFormatException e) {
            response.sendRedirect("bank_branches.jsp?error=invalid_id");
        } catch (Exception e) {
            response.sendRedirect("bank_branches.jsp?error=deactivation_failed");
        }
    }
} 