package com.bank.app.web.servlet;

import com.bank.app.core.model.User;
import com.bank.app.core.model.UserType;
import com.bank.app.core.model.Status;
import com.bank.app.core.model.BankBranch;
import com.bank.app.core.service.UserService;
import com.bank.app.core.service.BankBranchService;
import com.bank.app.core.mail.VerificationMail;
import com.bank.app.core.provider.MailServiceProvider;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/admin/addUser")
@RolesAllowed({"ADMIN", "SUPER_ADMIN"})
public class AddUser extends HttpServlet {
    @EJB
    private UserService userService;
    
    @EJB
    private BankBranchService bankBranchService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String contact = request.getParameter("contact");
        String nic = request.getParameter("nic");
        String address = request.getParameter("address");
        String bankBranchIdStr = request.getParameter("bankBranchId");
        String password = request.getParameter("password");
        String userTypeStr = request.getParameter("userType");

        // Validation
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() ||
            contact == null || contact.trim().isEmpty() ||
            nic == null || nic.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            bankBranchIdStr == null || bankBranchIdStr.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            response.sendRedirect("add_user.jsp?error=all_fields_required");
            return;
        }

        if (password.length() < 6) {
            response.sendRedirect("add_user.jsp?error=password_too_short");
            return;
        }

        try {
            // Validate bank branch
            Long bankBranchId = Long.parseLong(bankBranchIdStr);
            BankBranch bankBranch = bankBranchService.getBankBranchById(bankBranchId);
            
            if (bankBranch == null || !bankBranch.getIsActive()) {
                response.sendRedirect("add_user.jsp?error=invalid_bank_branch");
                return;
            }

            // Check if user already exists
            User existingUser = userService.getUserByEmail(email);
            if (existingUser != null) {
                response.sendRedirect("add_user.jsp?error=email_exists");
                return;
            }

            // Check if NIC already exists
            User existingUserByNic = userService.getUserByNic(nic);
            if (existingUserByNic != null) {
                response.sendRedirect("add_user.jsp?error=nic_exists");
                return;
            }

            // Create new user
            User user = new User(name, email, contact, password, nic, address, bankBranch);
            
            // Set user type based on parameter and current user's role
            String currentUserRole = request.getUserPrincipal() != null ? 
                request.getUserPrincipal().getName() : null;
            User currentUser = userService.getUserByEmail(currentUserRole);
            if ("ADMIN".equals(userTypeStr) && currentUser != null && currentUser.getUserType().name().equals("SUPER_ADMIN")) {
                user.setUserType(UserType.ADMIN);
            } else {
                user.setUserType(UserType.USER);
            }
            
            // Set status to INACTIVE and add verification code
            user.setStatus(Status.INACTIVE);
            String verificationCode = UUID.randomUUID().toString();
            user.setVerificationCode(verificationCode);
            
            userService.addUser(user);
            
            // Send verification email
            VerificationMail mail = new VerificationMail(email, verificationCode);
            MailServiceProvider.getInstance().sendMail(mail);
            
            response.sendRedirect("users.jsp?success=user_added_verification_sent");
        } catch (NumberFormatException e) {
            response.sendRedirect("add_user.jsp?error=invalid_bank_branch");
        } catch (Exception e) {
            response.sendRedirect("add_user.jsp?error=creation_failed");
        }
    }
}
