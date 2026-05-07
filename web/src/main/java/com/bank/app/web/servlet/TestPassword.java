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

@WebServlet("/testpassword")
public class TestPassword extends HttpServlet {
    @EJB
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (email == null || password == null) {
            response.getWriter().println("<h2>Password Test</h2>");
            response.getWriter().println("<p>Usage: /testpassword?email=your_email&password=your_password</p>");
            return;
        }
        
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                response.getWriter().println("<p>User not found: " + email + "</p>");
                return;
            }
            
            response.getWriter().println("<h2>Password Test Results</h2>");
            response.getWriter().println("<p>Email: " + email + "</p>");
            response.getWriter().println("<p>User Type: " + user.getUserType() + "</p>");
            response.getWriter().println("<p>Status: " + user.getStatus() + "</p>");
            response.getWriter().println("<p>Stored Password: " + user.getPassword() + "</p>");
            
            String encryptedPassword = Encryption.encrypt(password);
            response.getWriter().println("<p>Input Password: " + password + "</p>");
            response.getWriter().println("<p>Encrypted Password: " + encryptedPassword + "</p>");
            response.getWriter().println("<p>Passwords Match: " + user.getPassword().equals(encryptedPassword) + "</p>");
            
            boolean validationResult = userService.validate(email, password);
            response.getWriter().println("<p>Validation Result: " + validationResult + "</p>");
            
        } catch (Exception e) {
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(response.getWriter());
        }
    }
} 