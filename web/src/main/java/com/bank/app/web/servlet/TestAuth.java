package com.bank.app.web.servlet;

import com.bank.app.core.model.User;
import com.bank.app.core.service.UserService;
import com.bank.app.core.util.Encryption;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/test/auth")
public class TestAuth extends HttpServlet {
    
    @EJB
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Test Authentication</title></head><body>");
        out.println("<h1>Test Authentication Process</h1>");
        
        try {
            out.println("<h2>Testing User Service...</h2>");
            
            // Test getting all users
            List<User> users = userService.getAllUsers();
            out.println("<p>Total users found: " + users.size() + "</p>");
            
            for (User user : users) {
                out.println("<h3>User: " + user.getName() + " (" + user.getEmail() + ")</h3>");
                out.println("<p>Type: " + user.getUserType() + "</p>");
                out.println("<p>Status: " + user.getStatus() + "</p>");
                out.println("<p>Password length: " + (user.getPassword() != null ? user.getPassword().length() : "null") + "</p>");
                out.println("<p>Verification Code: " + (user.getVerificationCode() != null ? "Present" : "None") + "</p>");
                
                // Test validation for each user
                if (user.getPassword() != null) {
                    boolean isValid = userService.validate(user.getEmail(), "admin123"); // Test with common password
                    out.println("<p>Validation with 'admin123': " + isValid + "</p>");
                }
            }
            
            // Test specific validation scenarios
            out.println("<h2>Testing Specific Validation Scenarios...</h2>");
            
            // Test with a specific email if available
            if (!users.isEmpty()) {
                User testUser = users.get(0);
                out.println("<h3>Testing validation for: " + testUser.getEmail() + "</h3>");
                
                // Test with different passwords
                String[] testPasswords = {"admin123", "password", "123456", "admin"};
                for (String testPassword : testPasswords) {
                    boolean isValid = userService.validate(testUser.getEmail(), testPassword);
                    out.println("<p>Password '" + testPassword + "': " + isValid + "</p>");
                }
                
                // Test encryption
                out.println("<h3>Testing Encryption...</h3>");
                String testPassword = "admin123";
                String encrypted = Encryption.encrypt(testPassword);
                out.println("<p>Original password: " + testPassword + "</p>");
                out.println("<p>Encrypted password: " + encrypted + "</p>");
                out.println("<p>Encrypted length: " + encrypted.length() + "</p>");
                out.println("<p>Stored password: " + testUser.getPassword() + "</p>");
                out.println("<p>Stored password length: " + (testUser.getPassword() != null ? testUser.getPassword().length() : "null") + "</p>");
                out.println("<p>Passwords match: " + (testUser.getPassword() != null && testUser.getPassword().equals(encrypted)) + "</p>");
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