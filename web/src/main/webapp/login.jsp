<%--
  Created by IntelliJ IDEA.
  User: Hasith Disanayaka
  Date: 7/6/2025
  Time: 12:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f4f6f8; }
        .login-container {
            width: 350px; margin: 80px auto; background: #fff; border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1); padding: 32px 24px;
        }
        h2 { text-align: center; color: #333; margin-bottom: 24px; }
        label { display: block; margin-bottom: 8px; color: #555; }
        input[type="text"], input[type="password"] {
            width: 100%; padding: 10px; margin-bottom: 18px; border: 1px solid #ccc;
            border-radius: 4px; font-size: 16px;
        }
        .btn {
            width: 100%; background: #1976d2; color: #fff; border: none;
            padding: 12px; border-radius: 4px; font-size: 16px; cursor: pointer;
            transition: background 0.2s;
        }
        .btn:hover { background: #1565c0; }
        .register-link { text-align: center; margin-top: 16px; }
        .register-link a { color: #1976d2; text-decoration: none; }
        .register-link a:hover { text-decoration: underline; }
        .error { color: #d32f2f; text-align: center; margin-bottom: 12px; }
        .success { color: #388e3c; text-align: center; margin-bottom: 12px; }
        .info { color: #1976d2; text-align: center; margin-bottom: 12px; }
    </style>
</head>
<body>
<div class="login-container">
    <h2>Banking System Login</h2>
    <form action="login" method="post">
        <label for="email">Email</label>
        <input type="text" id="email" name="email" required>
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>
        
        <% if (request.getParameter("error") != null) { %>
            <div class="error">
                <% if ("setup_complete".equals(request.getParameter("error"))) { %>
                    Setup already completed. Please login with your credentials.
                <% } else if ("login_failed".equals(request.getParameter("error"))) { %>
                    Invalid email or password. Please try again.
                <% } else if ("not_verified".equals(request.getParameter("error"))) { %>
                    Please verify your email address before logging in.
                <% } else { %>
                    <%= request.getParameter("error") %>
                <% } %>
            </div>
        <% } %>
        
        <% if (request.getParameter("success") != null) { %>
            <div class="success">
                <% if ("verified".equals(request.getParameter("success"))) { %>
                    Email verified successfully! You can now login.
                <% } else if ("super_admin_verified".equals(request.getParameter("success"))) { %>
                    Super admin account verified successfully! You can now login to your account.
                <% } else if ("already_verified".equals(request.getParameter("success"))) { %>
                    Your email is already verified. Please login.
                <% } else if ("setup_complete".equals(request.getParameter("success"))) { %>
                    Super admin created successfully! Please login.
                <% } else { %>
                    <%= request.getParameter("success") %>
                <% } %>
            </div>
        <% } %>
        
        <button class="btn" type="submit">Login</button>
    </form>
    <div class="register-link">
        Setup SUPER ADMIN <a href="setup.jsp">Register here</a>
        <br><br>
        <a href="verify_email.jsp">Need to verify your email?</a>
    </div>
</div>
</body>
</html>
