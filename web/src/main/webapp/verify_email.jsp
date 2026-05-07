<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Email Verification - Banking System</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .verify-container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 500px;
            text-align: center;
        }
        .verify-header {
            margin-bottom: 30px;
        }
        .verify-header h1 {
            color: #333;
            margin: 0 0 10px 0;
            font-size: 28px;
        }
        .verify-header p {
            color: #666;
            margin: 0;
            font-size: 16px;
            line-height: 1.5;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }
        .form-group input {
            width: 100%;
            padding: 12px;
            border: 2px solid #e1e5e9;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s ease;
            box-sizing: border-box;
            text-align: center;
            letter-spacing: 2px;
            font-weight: bold;
        }
        .form-group input:focus {
            outline: none;
            border-color: #667eea;
        }
        .verify-btn {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s ease;
        }
        .verify-btn:hover {
            transform: translateY(-2px);
        }
        .message {
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .message.error {
            background: #fee;
            color: #c33;
            border-left: 4px solid #c33;
        }
        .message.success {
            background: #efe;
            color: #3c3;
            border-left: 4px solid #3c3;
        }
        .info-box {
            background: #e3f2fd;
            color: #1976d2;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #1976d2;
            text-align: left;
        }
        .resend-link {
            margin-top: 20px;
        }
        .resend-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }
        .resend-link a:hover {
            text-decoration: underline;
        }
        .login-link {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
        .login-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }
        .login-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="verify-container">
        <div class="verify-header">
            <h1>Email Verification</h1>
            <p>Please enter the verification code sent to your email address</p>
            <% if ("true".equals(request.getParameter("setup"))) { %>
                <p style="color: #1976d2; font-weight: 600; margin-top: 10px;">
                    🔐 Super Admin Setup - Email verification required
                </p>
            <% } %>
        </div>
        
        <div class="info-box">
            <strong>Important:</strong> Check your email inbox (and spam folder) for the verification code. 
            The code is required to activate your account.
        </div>
        
        <% if (request.getParameter("error") != null) { %>
            <div class="message error">
                <% if ("invalid_code".equals(request.getParameter("error"))) { %>
                    Invalid verification code. Please check your email and try again.
                <% } else if ("expired_code".equals(request.getParameter("error"))) { %>
                    Verification code has expired. Please request a new one.
                <% } else if ("user_not_found".equals(request.getParameter("error"))) { %>
                    User not found. Please check your email address.
                <% } else { %>
                    <%= request.getParameter("error") %>
                <% } %>
            </div>
        <% } %>
        
        <% if (request.getParameter("success") != null) { %>
            <div class="message success">
                <% if ("email_sent".equals(request.getParameter("success"))) { %>
                    <% if ("true".equals(request.getParameter("setup"))) { %>
                        Super admin account created! Verification code has been sent to your email address.
                        <br><strong>Please verify your email to activate the super admin account.</strong>
                    <% } else { %>
                        Verification code has been sent to your email address.
                    <% } %>
                <% } else if ("verified".equals(request.getParameter("success"))) { %>
                    <% if ("true".equals(request.getParameter("setup"))) { %>
                        Super admin account verified successfully! You can now login to your account.
                    <% } else { %>
                        Email verified successfully! You can now login to your account.
                    <% } %>
                <% } %>
            </div>
        <% } %>
        
        <form method="post" action="verifyEmail">
            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" required 
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>"
                       placeholder="Enter your email address">
            </div>
            
            <div class="form-group">
                <label for="verificationCode">Verification Code</label>
                <input type="text" id="verificationCode" name="verificationCode" required 
                       maxlength="36" placeholder="Enter verification code"
                       pattern="[a-fA-F0-9-]{36}" title="Please enter a valid verification code">
            </div>
            
            <button type="submit" class="verify-btn">Verify Email</button>
        </form>
        
        <div class="resend-link">
            <a href="resendVerification?email=<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
                Resend verification code
            </a>
        </div>
        
        <div class="login-link">
            Already verified? <a href="login.jsp">Login here</a>
        </div>
    </div>
    
    <script>
        // Autofocus on verification code field
        document.getElementById('verificationCode').focus();
        
        // Auto-format verification code (remove spaces and dashes)
        document.getElementById('verificationCode').addEventListener('input', function() {
            this.value = this.value.replace(/[^a-fA-F0-9-]/g, '');
        });
    </script>
</body>
</html> 