package com.bank.app.web.servlet;

import com.bank.app.core.model.User;
import com.bank.app.core.model.UserType;
import com.bank.app.core.model.BankBranch;
import com.bank.app.core.service.UserService;
import com.bank.app.core.service.BankBranchService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/editUser")
@RolesAllowed({"ADMIN", "SUPER_ADMIN"})
public class EditUser extends HttpServlet {
    @EJB
    private UserService userService;
    
    @EJB
    private BankBranchService bankBranchService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userIdStr = request.getParameter("id");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect("users.jsp?error=user_id_required");
            return;
        }
        
        try {
            Long userId = Long.parseLong(userIdStr);
            User user = userService.getUserById(userId);
            
            if (user == null) {
                response.sendRedirect("users.jsp?error=user_not_found");
                return;
            }
            
            // Check authorization
            String currentUserRole = request.getUserPrincipal() != null ? 
                request.getUserPrincipal().getName() : null;
            User currentUser = userService.getUserByEmail(currentUserRole);
            
            if (currentUser == null) {
                response.sendRedirect("users.jsp?error=unauthorized");
                return;
            }
            
            // ADMIN can only edit USER accounts, SUPER_ADMIN can edit all
            if (currentUser.getUserType() == UserType.ADMIN && 
                user.getUserType() != UserType.USER) {
                response.sendRedirect("users.jsp?error=unauthorized_edit_admin");
                return;
            }
            
            request.setAttribute("user", user);
            request.getRequestDispatcher("edit_user.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("users.jsp?error=invalid_user_id");
        } catch (Exception e) {
            response.sendRedirect("users.jsp?error=load_failed");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String contact = request.getParameter("contact");
        String nic = request.getParameter("nic");
        String address = request.getParameter("address");
        String bankBranchIdStr = request.getParameter("bankBranchId");
        String userTypeStr = request.getParameter("userType");

        // Validation
        if (userIdStr == null || userIdStr.trim().isEmpty() ||
            name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() ||
            contact == null || contact.trim().isEmpty() ||
            nic == null || nic.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            bankBranchIdStr == null || bankBranchIdStr.trim().isEmpty()) {
            response.sendRedirect("users.jsp?error=all_fields_required");
            return;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            User user = userService.getUserById(userId);
            
            if (user == null) {
                response.sendRedirect("users.jsp?error=user_not_found");
                return;
            }

            // Validate bank branch
            Long bankBranchId = Long.parseLong(bankBranchIdStr);
            BankBranch bankBranch = bankBranchService.getBankBranchById(bankBranchId);
            
            if (bankBranch == null || !bankBranch.getIsActive()) {
                response.sendRedirect("users.jsp?error=invalid_bank_branch");
                return;
            }

            // Check if email is being changed and if it already exists
            if (!email.equals(user.getEmail())) {
                User existingUser = userService.getUserByEmail(email);
                if (existingUser != null) {
                    response.sendRedirect("users.jsp?error=email_exists");
                    return;
                }
            }

            // Check if NIC is being changed and if it already exists
            if (!nic.equals(user.getNic())) {
                User existingUserByNic = userService.getUserByNic(nic);
                if (existingUserByNic != null) {
                    response.sendRedirect("users.jsp?error=nic_exists");
                    return;
                }
            }

            // Update user details
            user.setName(name);
            user.setEmail(email);
            user.setContact(contact);
            user.setNic(nic);
            user.setAddress(address);
            user.setBankBranch(bankBranch);
            
            // Handle user type changes based on current user's role
            String currentUserRole = request.getUserPrincipal() != null ? 
                request.getUserPrincipal().getName() : null;
            User currentUser = userService.getUserByEmail(currentUserRole);
            
            if (currentUser != null && currentUser.getUserType().name().equals("SUPER_ADMIN")) {
                // SUPER_ADMIN can change any user type
                if ("ADMIN".equals(userTypeStr)) {
                    user.setUserType(UserType.ADMIN);
                } else if ("SUPER_ADMIN".equals(userTypeStr)) {
                    user.setUserType(UserType.SUPER_ADMIN);
                } else {
                    user.setUserType(UserType.USER);
                }
            } else if (currentUser != null && currentUser.getUserType().name().equals("ADMIN")) {
                // ADMIN can only manage USER accounts
                user.setUserType(UserType.USER);
            }
            
            userService.updateUser(user);
            response.sendRedirect("users.jsp?success=user_updated");
        } catch (NumberFormatException e) {
            response.sendRedirect("users.jsp?error=invalid_data");
        } catch (Exception e) {
            response.sendRedirect("users.jsp?error=update_failed");
        }
    }
}
