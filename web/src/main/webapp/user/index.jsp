<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.AccountService" %>
<%@ page import="com.bank.app.core.model.Account" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<%@ page import="com.bank.app.core.service.UserService" %>
<%@ page import="com.bank.app.core.model.User" %>
<%@ page import="com.bank.app.core.service.TransactionService" %>
<%@ page import="com.bank.app.core.model.Transaction" %>
<%@ page import="java.util.Comparator" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Dashboard - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
<%

    String currentUserEmail = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
    User currentUser = null;
    List<Account> accounts = new java.util.ArrayList<>();
    List<Transaction> recentTransactions = new java.util.ArrayList<>();
    try {
        InitialContext ic = new InitialContext();
        UserService userService = (UserService) ic.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        AccountService accountService = (AccountService) ic.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        TransactionService transactionService = (TransactionService) ic.lookup("java:global/banking-system-ear/account-module/TransactionSessionBean!com.bank.app.core.service.TransactionService");
        if (currentUserEmail != null) {
            currentUser = userService.getUserByEmail(currentUserEmail);
            if (currentUser != null) {
                accounts = accountService.getAccountsByUser(currentUser.getId());
                List<Transaction> allTx = transactionService.getAllTransactions();
                for (Transaction tx : allTx) {
                    if (tx.getUserId() != null && tx.getUserId().equals(currentUser.getId())) {
                        recentTransactions.add(tx);
                    }
                }
                recentTransactions.sort(Comparator.comparing(Transaction::getDate).reversed());
                if (recentTransactions.size() > 10) {
                    recentTransactions = recentTransactions.subList(0, 10);
                }
            }
        }
    } catch (Exception e) {
        accounts = new java.util.ArrayList<>();
        recentTransactions = new java.util.ArrayList<>();
    }
%>
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2  p-0">
            <%@ include file="/styles/sidebar_user.jsp" %>
        </div>
        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 main-content p-4">
            <div class="row">
                <div class="col-12">
                    <h1 class="mb-4">
                        <i class="fas fa-cash-register me-2"></i>Cashier Dashboard
                    </h1>
                    <!-- Cashier Quick Actions -->
                    <div class="row mb-4 justify-content-center">
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking h-100 text-center">
                                <div class="card-body d-flex flex-column align-items-center justify-content-center">
                                    <i class="fas fa-money-bill-wave fa-3x text-warning mb-3"></i>
                                    <h5 class="card-title">Process Withdrawal</h5>
                                    <p class="card-text">Withdraw funds for a customer</p>
                                    <a href="withdraw" class="btn btn-lg btn-banking-warning mt-2 w-100">
                                        <i class="fas fa-money-bill-wave me-2"></i>Withdraw
                                    </a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking h-100 text-center">
                                <div class="card-body d-flex flex-column align-items-center justify-content-center">
                                    <i class="fas fa-coins fa-3x text-success mb-3"></i>
                                    <h5 class="card-title">Process Deposit</h5>
                                    <p class="card-text">Deposit funds for a customer</p>
                                    <a href="deposit" class="btn btn-lg btn-banking-success mt-2 w-100">
                                        <i class="fas fa-coins me-2"></i>Deposit
                                    </a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card card-banking h-100 text-center">
                                <div class="card-body d-flex flex-column align-items-center justify-content-center">
                                    <i class="fas fa-exchange-alt fa-3x text-info mb-3"></i>
                                    <h5 class="card-title">Process Transfer</h5>
                                    <p class="card-text">Transfer funds between accounts</p>
                                    <a href="transfer" class="btn btn-lg btn-banking-info mt-2 w-100">
                                        <i class="fas fa-exchange-alt me-2"></i>Transfer
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
