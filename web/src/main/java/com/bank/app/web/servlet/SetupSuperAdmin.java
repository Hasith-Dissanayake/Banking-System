package com.bank.app.web.servlet;

import com.bank.app.core.mail.VerificationMail;
import com.bank.app.core.model.User;
import com.bank.app.core.model.UserType;
import com.bank.app.core.model.Status;
import com.bank.app.core.model.BankBranch;
import com.bank.app.core.provider.MailServiceProvider;
import com.bank.app.core.service.UserService;
import com.bank.app.core.service.BankBranchService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/setup")
public class SetupSuperAdmin extends HttpServlet {
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

        // Validation
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() ||
            contact == null || contact.trim().isEmpty() ||
            nic == null || nic.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            bankBranchIdStr == null || bankBranchIdStr.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("setup.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters long");
            request.getRequestDispatcher("setup.jsp").forward(request, response);
            return;
        }

        try {
            // Validate bank branch
            Long bankBranchId = Long.parseLong(bankBranchIdStr);
            BankBranch bankBranch = bankBranchService.getBankBranchById(bankBranchId);
            
            if (bankBranch == null || !bankBranch.getIsActive()) {
                request.setAttribute("error", "Selected bank branch is not available");
                request.getRequestDispatcher("setup.jsp").forward(request, response);
                return;
            }

            // Check if super admin already exists
            if (userService.isSuperAdminExists()) {
                request.setAttribute("error", "Super admin already exists");
                request.getRequestDispatcher("setup.jsp").forward(request, response);
                return;
            }

            // Check if user already exists
            User existingUser = userService.getUserByEmail(email);
            if (existingUser != null) {
                request.setAttribute("error", "User with this email already exists");
                request.getRequestDispatcher("setup.jsp").forward(request, response);
                return;
            }

            // Check if user already exists by NIC
            User existingUserByNic = userService.getUserByNic(nic);
            if (existingUserByNic != null) {
                request.setAttribute("error", "User with this NIC number already exists");
                request.getRequestDispatcher("setup.jsp").forward(request, response);
                return;
            }

            // Create super admin with email verification
            User superAdmin = new User(name, email, contact, password, nic, address, bankBranch);
            superAdmin.setUserType(UserType.SUPER_ADMIN);
            superAdmin.setStatus(Status.INACTIVE); // Super admin needs email verification
            
            // Generate verification code
            String verificationCode = UUID.randomUUID().toString();
            superAdmin.setVerificationCode(verificationCode);
            
            userService.addUser(superAdmin);
            
            // Send verification email
            VerificationMail mail = new VerificationMail(email, verificationCode);
            MailServiceProvider.getInstance().sendMail(mail);
            
            // Redirect to verification page
            response.sendRedirect("verify_email.jsp?success=email_sent&email=" + email + "&setup=true");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid bank branch selected");
            request.getRequestDispatcher("setup.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Setup failed: " + e.getMessage());
            request.getRequestDispatcher("setup.jsp").forward(request, response);
        }
    }
}
