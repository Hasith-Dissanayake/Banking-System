package com.bank.app.web.servlet;

import com.bank.app.core.model.BankBranch;
import com.bank.app.core.service.BankBranchService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/debug/bankBranches")
public class DebugBankBranches extends HttpServlet {
    
    @EJB
    private BankBranchService bankBranchService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Debug Bank Branches</title></head><body>");
        out.println("<h1>Debug Bank Branches</h1>");
        
        try {
            out.println("<h2>Testing BankBranchService...</h2>");
            
            // Test getAllBankBranches
            out.println("<h3>All Bank Branches:</h3>");
            List<BankBranch> allBranches = bankBranchService.getAllBankBranches();
            out.println("<p>Total branches found: " + allBranches.size() + "</p>");
            for (BankBranch branch : allBranches) {
                out.println("<p>ID: " + branch.getId() + ", Name: " + branch.getBranchName() + 
                           ", Active: " + branch.getIsActive() + "</p>");
            }
            
            // Test getActiveBankBranches
            out.println("<h3>Active Bank Branches:</h3>");
            List<BankBranch> activeBranches = bankBranchService.getActiveBankBranches();
            out.println("<p>Active branches found: " + activeBranches.size() + "</p>");
            for (BankBranch branch : activeBranches) {
                out.println("<p>ID: " + branch.getId() + ", Name: " + branch.getBranchName() + 
                           ", Active: " + branch.getIsActive() + "</p>");
            }
            
            // Test JNDI lookup
            out.println("<h3>Testing JNDI Lookup:</h3>");
            try {
                javax.naming.InitialContext context = new javax.naming.InitialContext();
                BankBranchService service = (BankBranchService) context.lookup(
                    "java:global/banking-system-ear/auth-module/BankBranchSessionBean!com.bank.app.core.service.BankBranchService");
                out.println("<p>JNDI lookup successful!</p>");
                
                List<BankBranch> jndiBranches = service.getActiveBankBranches();
                out.println("<p>JNDI branches found: " + jndiBranches.size() + "</p>");
            } catch (Exception e) {
                out.println("<p>JNDI lookup failed: " + e.getMessage() + "</p>");
                e.printStackTrace(out);
            }
            
        } catch (Exception e) {
            out.println("<h2>Error:</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }
        
        out.println("</body></html>");
    }
} 