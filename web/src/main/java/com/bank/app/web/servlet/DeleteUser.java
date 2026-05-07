package com.bank.app.web.servlet;

import com.bank.app.core.model.User;
import com.bank.app.core.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/deleteUser")
@RolesAllowed({"ADMIN", "SUPER_ADMIN"})
public class DeleteUser extends HttpServlet {
    @EJB
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userIdStr = request.getParameter("id");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect("users.jsp?error=invalid_user");
            return;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            User user = userService.getUserById(userId);
            
            if (user == null) {
                response.sendRedirect("users.jsp?error=user_not_found");
                return;
            }

            // Prevent deletion of super admin
            if ("SUPER_ADMIN".equals(user.getUserType().toString())) {
                response.sendRedirect("users.jsp?error=cannot_delete_super_admin");
                return;
            }

            // After finding the user to delete, add this check:
            String currentUserRole = request.getUserPrincipal() != null ? 
                request.getUserPrincipal().getName() : null;
            User currentUser = userService.getUserByEmail(currentUserRole);
            if (user.getUserType().name().equals("ADMIN") || user.getUserType().name().equals("SUPER_ADMIN")) {
                if (currentUser == null || !currentUser.getUserType().name().equals("SUPER_ADMIN")) {
                    response.sendRedirect("users.jsp?error=unauthorized_delete_admin");
                    return;
                }
            }

            userService.deleteUser(user);
            response.sendRedirect("users.jsp?success=user_deleted");
        } catch (NumberFormatException e) {
            response.sendRedirect("users.jsp?error=invalid_user_id");
        } catch (Exception e) {
            response.sendRedirect("users.jsp?error=delete_failed");
        }
    }
}
