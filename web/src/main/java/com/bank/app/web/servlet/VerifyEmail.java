package com.bank.app.web.servlet;

import com.bank.app.core.model.Status;
import com.bank.app.core.model.User;
import com.bank.app.core.service.UserService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet({"/verifyEmail", "/verify"})
public class VerifyEmail extends HttpServlet {

    @EJB
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if this is a URL-based verification (old format)
        String id = request.getParameter("id");
        String vc = request.getParameter("vc");
        
        if (id != null && vc != null) {
            // Handle URL-based verification (old format)
            handleUrlVerification(request, response, id, vc);
        } else {
            // Show verification page (new format)
            request.getRequestDispatcher("verify_email.jsp").forward(request, response);
        }
    }
    
    private void handleUrlVerification(HttpServletRequest request, HttpServletResponse response, String id, String vc) throws ServletException, IOException {
        try {
            // Decode the email from base64
            byte[] bytes = java.util.Base64.getDecoder().decode(id);
            String email = new String(bytes);
            
            User user = userService.getUserByEmail(email);
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/verify_email.jsp?error=user_not_found&email=" + email);
                return;
            }
            
            if (user.getStatus() == Status.ACTIVE) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?success=already_verified");
                return;
            }
            
            if (user.getVerificationCode() == null || !user.getVerificationCode().equals(vc)) {
                response.sendRedirect(request.getContextPath() + "/verify_email.jsp?error=invalid_code&email=" + email);
                return;
            }
            
            // Verification successful
            user.setStatus(Status.ACTIVE);
            userService.updateUser(user);
            
            // Check if this was a super admin verification
            if (user.getUserType().name().equals("SUPER_ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?success=super_admin_verified");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?success=verified");
            }
            
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/verify_email.jsp?error=verification_failed");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String verificationCode = request.getParameter("verificationCode");

        if (email == null || email.trim().isEmpty() || 
            verificationCode == null || verificationCode.trim().isEmpty()) {
            response.sendRedirect("verify_email.jsp?error=missing_fields");
            return;
        }

        try {
            User user = userService.getUserByEmail(email);
            
            if (user == null) {
                response.sendRedirect("verify_email.jsp?error=user_not_found&email=" + email);
                return;
            }

            if (user.getStatus() == Status.ACTIVE) {
                response.sendRedirect("login.jsp?success=already_verified");
                return;
            }

            if (user.getVerificationCode() == null || !user.getVerificationCode().equals(verificationCode)) {
                response.sendRedirect("verify_email.jsp?error=invalid_code&email=" + email);
                return;
            }

            // Verification successful
            user.setStatus(Status.ACTIVE);
            userService.updateUser(user);
            
            // Check if this was a setup verification
            String setup = request.getParameter("setup");
            if ("true".equals(setup) || user.getUserType().name().equals("SUPER_ADMIN")) {
                response.sendRedirect("login.jsp?success=super_admin_verified");
            } else {
                response.sendRedirect("login.jsp?success=verified");
            }
            
        } catch (Exception e) {
            response.sendRedirect("verify_email.jsp?error=verification_failed&email=" + email);
        }
    }
}
