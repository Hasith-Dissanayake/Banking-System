package com.bank.app.web.servlet;

import com.bank.app.core.model.User;
import com.bank.app.core.service.UserService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/debug")
public class DebugSuperAdmin extends HttpServlet {
    @EJB
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            // Check if super admin exists
            boolean superAdminExists = userService.isSuperAdminExists();
            response.getWriter().println("<h2>Super Admin Debug Info</h2>");
            response.getWriter().println("<p>Super Admin Exists: " + superAdminExists + "</p>");
            
            if (superAdminExists) {
                // Get all users to find super admin
                for (User user : userService.getAllUsers()) {
                    if (user.getUserType().name().equals("SUPER_ADMIN")) {
                        response.getWriter().println("<h3>Super Admin Details:</h3>");
                        response.getWriter().println("<p>ID: " + user.getId() + "</p>");
                        response.getWriter().println("<p>Name: " + user.getName() + "</p>");
                        response.getWriter().println("<p>Email: " + user.getEmail() + "</p>");
                        response.getWriter().println("<p>Status: " + user.getStatus() + "</p>");
                        response.getWriter().println("<p>User Type: " + user.getUserType() + "</p>");
                        response.getWriter().println("<p>Verification Code: " + (user.getVerificationCode() != null ? "Set" : "Not Set") + "</p>");
                        response.getWriter().println("<p>Password Length: " + (user.getPassword() != null ? user.getPassword().length() : "null") + "</p>");
                        break;
                    }
                }
            }
            
            response.getWriter().println("<h3>All Users:</h3>");
            for (User user : userService.getAllUsers()) {
                response.getWriter().println("<p>" + user.getEmail() + " - " + user.getUserType() + " - " + user.getStatus() + "</p>");
            }
            
        } catch (Exception e) {
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(response.getWriter());
        }
    }
} 