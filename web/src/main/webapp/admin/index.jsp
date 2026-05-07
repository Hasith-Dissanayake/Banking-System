<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.AccountService" %>
<%@ page import="com.bank.app.core.model.Account" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<%@ page import="com.bank.app.core.service.UserService" %>
<%@ page import="com.bank.app.core.model.User" %>
<%@ page import="com.bank.app.core.model.UserType" %>
<%@ page import="com.bank.app.core.service.BankBranchService" %>
<%@ page import="com.bank.app.core.model.BankBranch" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Local Bootstrap CSS -->
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Custom Banking CSS -->
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
<%
    // Check if current user is SUPER_ADMIN
    boolean isSuperAdmin = false;
    String currentUserEmail = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
    if (currentUserEmail != null) {
        try {
            InitialContext ic = new InitialContext();
            UserService userService = (UserService) ic.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
            User currentUser = userService.getUserByEmail(currentUserEmail);
            isSuperAdmin = currentUser != null && currentUser.getUserType() == UserType.SUPER_ADMIN;
            if (currentUser != null) {
                session.setAttribute("userType", currentUser.getUserType().name());
            }
        } catch (Exception e) {
            isSuperAdmin = false;
        }
    }

    // Load data for quick stats
    try {
        InitialContext ic = new InitialContext();
        UserService userService = (UserService) ic.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        AccountService accountService = (AccountService) ic.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        BankBranchService bankBranchService = (BankBranchService) ic.lookup("java:global/banking-system-ear/auth-module/BankBranchSessionBean!com.bank.app.core.service.BankBranchService");

        List<User> users = userService.getAllUsers();
        List<Account> accounts = accountService.getAllAccounts();
        List<BankBranch> bankBranches = bankBranchService.getAllBankBranches();

        pageContext.setAttribute("users", users);
        pageContext.setAttribute("accounts", accounts);
        pageContext.setAttribute("bankBranches", bankBranches);
    } catch (Exception e) {
        pageContext.setAttribute("users", new java.util.ArrayList<>());
        pageContext.setAttribute("accounts", new java.util.ArrayList<>());
        pageContext.setAttribute("bankBranches", new java.util.ArrayList<>());
    }
%>
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2  p-0">
            <%@ include file="/styles/sidebar.jsp" %>
        </div>
        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 main-content p-4">
            <div class="row">
                <div class="col-12">
                    <h1 class="mb-4">
                        <i class="fas fa-tachometer-alt me-2"></i>Admin Dashboard
                    </h1>
                    <!-- Quick Stats -->
                    <div class="row mb-4">
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking text-center">
                                <div class="card-body">
                                    <i class="fas fa-users fa-2x text-primary mb-2"></i>
                                    <h4 class="card-title">${fn:length(users)}</h4>
                                    <p class="card-text">Total Users</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking text-center">
                                <div class="card-body">
                                    <i class="fas fa-credit-card fa-2x text-info mb-2"></i>
                                    <h4 class="card-title">${fn:length(accounts)}</h4>
                                    <p class="card-text">Total Accounts</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking text-center">
                                <div class="card-body">
                                    <i class="fas fa-university fa-2x text-success mb-2"></i>
                                    <h4 class="card-title">${fn:length(bankBranches)}</h4>
                                    <p class="card-text">Bank Branches</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking text-center">
                                <div class="card-body">
                                    <i class="fas fa-chart-line fa-2x text-warning mb-2"></i>
                                    <h4 class="card-title">Rs.0</h4>
                                    <p class="card-text">Total Balance</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Quick Actions -->
                    <div class="row mb-4">
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking h-100">
                                <div class="card-body text-center">
                                    <i class="fas fa-user-plus fa-2x text-primary mb-3"></i>
                                    <h5 class="card-title">User Management</h5>
                                    <p class="card-text">Manage users and their accounts</p>
                                    <a href="users.jsp" class="btn btn-banking-primary">
                                        <i class="fas fa-users me-2"></i>Manage Users
                                    </a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking h-100">
                                <div class="card-body text-center">
                                    <i class="fas fa-credit-card fa-2x text-info mb-3"></i>
                                    <h5 class="card-title">Account Management</h5>
                                    <p class="card-text">Manage bank accounts and transactions</p>
                                    <a href="accounts.jsp" class="btn btn-banking-info">
                                        <i class="fas fa-credit-card me-2"></i>Manage Accounts
                                    </a>
                                </div>
                            </div>
                        </div>
                        <% if (isSuperAdmin) { %>
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking h-100">
                                <div class="card-body text-center">
                                    <i class="fas fa-university fa-2x text-success mb-3"></i>
                                    <h5 class="card-title">Bank Branches</h5>
                                    <p class="card-text">Manage bank branch locations</p>
                                    <a href="bankBranches" class="btn btn-banking-success">
                                        <i class="fas fa-university me-2"></i>Manage Branches
                                    </a>
                                </div>
                            </div>
                        </div>
                        <% } %>
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking h-100">
                                <div class="card-body text-center">
                                    <i class="fas fa-exchange-alt fa-2x text-warning mb-3"></i>
                                    <h5 class="card-title">Schedule Transfer</h5>
                                    <p class="card-text">Schedule money transfers</p>
                                    <a href="schedule_transfer.jsp" class="btn btn-banking-warning">
                                        <i class="fas fa-clock me-2"></i>Schedule Transfer
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
    // Auto-hide alerts after 5 seconds
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
</script>
</body>
</html>
