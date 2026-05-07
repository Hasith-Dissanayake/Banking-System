package com.bank.app.web.servlet;

import com.bank.app.core.mail.VerificationMail;
import com.bank.app.core.model.Status;
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
import java.util.UUID;

@WebServlet("/resendVerification")
public class ResendVerification extends HttpServlet {

    @EJB
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect("verify_email.jsp?error=email_required");
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

            // Generate new verification code
            String newVerificationCode = UUID.randomUUID().toString();
            user.setVerificationCode(newVerificationCode);
            userService.updateUser(user);

            // Send new verification email
            VerificationMail mail = new VerificationMail(email, newVerificationCode);
            MailServiceProvider.getInstance().sendMail(mail);

            response.sendRedirect("verify_email.jsp?success=email_sent&email=" + email);
            
        } catch (Exception e) {
            response.sendRedirect("verify_email.jsp?error=resend_failed&email=" + email);
        }
    }
} 