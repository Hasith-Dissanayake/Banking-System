package com.bank.app.web.servlet;


import com.bank.app.core.mail.VerificationMail;
import com.bank.app.core.model.User;
import com.bank.app.core.model.BankBranch;
import com.bank.app.core.provider.MailServiceProvider;
import com.bank.app.core.service.UserService;
import com.bank.app.core.service.BankBranchService;
import com.bank.app.core.util.Encryption;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;




import java.io.IOException;
import java.util.UUID;

@WebServlet("/register")
public class Register extends HttpServlet {

    @EJB
    private UserService userService;
    
    @EJB
    private BankBranchService bankBranchService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String contact = request.getParameter("contact");
        String nic = request.getParameter("nic");
        String address = request.getParameter("address");
        String bankBranchIdStr = request.getParameter("bankBranchId");
        String password = request.getParameter("password");

        // Validate bank branch
        if (bankBranchIdStr == null || bankBranchIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Please select a bank branch");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        try {
            Long bankBranchId = Long.parseLong(bankBranchIdStr);
            BankBranch bankBranch = bankBranchService.getBankBranchById(bankBranchId);
            
            if (bankBranch == null || !bankBranch.getIsActive()) {
                request.setAttribute("error", "Selected bank branch is not available");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Check if user already exists by email
            User existingUserByEmail = userService.getUserByEmail(email);
            if (existingUserByEmail != null) {
                request.setAttribute("error", "A user with this email already exists");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Check if user already exists by NIC
            User existingUserByNic = userService.getUserByNic(nic);
            if (existingUserByNic != null) {
                request.setAttribute("error", "A user with this NIC number already exists");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            User user = new User(name, email, contact, password, nic, address, bankBranch);
            String verificationCode = UUID.randomUUID().toString();
            user.setVerificationCode(verificationCode);

            System.out.println(name + " " + email + " " + contact + " " + password);
            userService.addUser(user);

            VerificationMail mail = new VerificationMail(email, verificationCode);
            MailServiceProvider.getInstance().sendMail(mail);

            // Redirect to verification page
            response.sendRedirect("verify_email.jsp?success=email_sent&email=" + email);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid bank branch selected");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}
