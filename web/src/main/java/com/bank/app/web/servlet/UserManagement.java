package com.bank.app.web.servlet;

import com.bank.app.core.service.UserService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class UserManagement extends HttpServlet {
    @EJB
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<com.bank.app.core.model.User> users = userService.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("users.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("index.jsp?error=load_users_failed");
        }
    }
} 