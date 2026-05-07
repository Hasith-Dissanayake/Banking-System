package com.bank.app.web.servlet;

import com.bank.app.core.mail.VerificationMail;
import com.bank.app.core.model.User;
import com.bank.app.core.provider.MailServiceProvider;
import com.bank.app.core.service.UserService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/test/verification")
public class TestVerification extends HttpServlet {
    
    @EJB
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Test Verification</title></head><body>");
        out.println("<h1>Test Email Verification Process</h1>");
        
        try {
            out.println("<h2>Testing Mail Service...</h2>");
            
            // Test mail service
            try {
                MailServiceProvider mailProvider = MailServiceProvider.getInstance();
                mailProvider.start();
                out.println("<p>Mail service started successfully</p>");
                
                // Test sending a verification email
                String testEmail = "test@example.com";
                String testCode = "test-verification-code-123";
                VerificationMail testMail = new VerificationMail(testEmail, testCode);
                mailProvider.sendMail(testMail);
                out.println("<p>Test verification email sent successfully</p>");
                
            } catch (Exception e) {
                out.println("<p>Mail service error: " + e.getMessage() + "</p>");
                e.printStackTrace(out);
            }
            
            out.println("<h2>Testing User Service...</h2>");
            
            // Test getting all users
            List<User> users = userService.getAllUsers();
            out.println("<p>Total users found: " + users.size() + "</p>");
            
            for (User user : users) {
                out.println("<p>User: " + user.getName() + " (" + user.getEmail() + ") - " + 
                           "Type: " + user.getUserType() + ", Status: " + user.getStatus() + 
                           ", Verified: " + (user.getVerificationCode() == null ? "Yes" : "No") + "</p>");
            }
            
            // Test getting inactive users
            out.println("<h3>Inactive Users (Need Verification):</h3>");
            for (User user : users) {
                if (user.getStatus().name().equals("INACTIVE")) {
                    out.println("<p>Inactive User: " + user.getName() + " (" + user.getEmail() + ") - " + 
                               "Verification Code: " + user.getVerificationCode() + "</p>");
                }
            }
            
            // Test JNDI lookup
            out.println("<h2>Testing JNDI Lookup...</h2>");
            try {
                javax.naming.InitialContext context = new javax.naming.InitialContext();
                UserService service = (UserService) context.lookup(
                    "java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
                out.println("<p>JNDI lookup successful!</p>");
                
                List<User> jndiUsers = service.getAllUsers();
                out.println("<p>JNDI users found: " + jndiUsers.size() + "</p>");
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