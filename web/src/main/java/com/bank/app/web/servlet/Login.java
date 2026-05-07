package com.bank.app.web.servlet;


import com.bank.app.core.exception.LoginFailedException;
import com.bank.app.core.model.User;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



import java.io.IOException;

@WebServlet("/login")
public class Login extends HttpServlet {

    @Inject
    private SecurityContext securityContext;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println(email + " " + password);



        AuthenticationParameters parameters = AuthenticationParameters.withParams()
                .credential(new UsernamePasswordCredential(email, password));

        System.out.println("parameters :" + parameters);
        AuthenticationStatus status = securityContext.authenticate(request, response, parameters);
        System.out.println("status:" + status);

        if (status == AuthenticationStatus.SUCCESS) {
            // Fetch user and check verification status
            User user = null;
            try {
                user = ((com.bank.app.core.service.UserService) new javax.naming.InitialContext().lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService")).getUserByEmail(email);
            } catch (javax.naming.NamingException e) {
                throw new ServletException("Failed to lookup UserService", e);
            }
            
            // Check if user is verified (all users including super admin need ACTIVE status)
            if (user.getStatus().name().equals("ACTIVE")) {
                // Set user and userType in session
                request.getSession().setAttribute("user", user);
                request.getSession().setAttribute("userType", user.getUserType().name());
                String role = user.getUserType().name();
                if (role.equals("ADMIN") || role.equals("SUPER_ADMIN")) {
                    response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
                } else {
                    response.sendRedirect(request.getContextPath() + "/user/index.jsp");
                }
            } else {
                // User is not verified
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=not_verified");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_failed");
        }

    }
}
