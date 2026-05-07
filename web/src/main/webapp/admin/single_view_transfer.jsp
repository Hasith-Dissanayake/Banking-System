<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bank.app.core.model.Transfer" %>
<%@ page import="com.bank.app.core.model.User" %>
<%@ page import="com.bank.app.core.service.AccountService" %>
<%@ page import="com.bank.app.core.model.Account" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Transfer - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
<%
    String fromAccountNumber = null;
    String toAccountNumber = null;
    Transfer transfer = (Transfer) request.getAttribute("transfer");
    if (transfer != null) {
        try {
            javax.naming.InitialContext context = new javax.naming.InitialContext();
            AccountService accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
            Account fromAcc = accountService.getAccountById(transfer.getFromAccountId());
            Account toAcc = accountService.getAccountById(transfer.getToAccountId());
            fromAccountNumber = fromAcc != null ? fromAcc.getAccountNumber() : String.valueOf(transfer.getFromAccountId());
            toAccountNumber = toAcc != null ? toAcc.getAccountNumber() : String.valueOf(transfer.getToAccountId());
        } catch (Exception e) {
            fromAccountNumber = String.valueOf(transfer.getFromAccountId());
            toAccountNumber = String.valueOf(transfer.getToAccountId());
        }
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
            <div class="card card-banking">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">
                        <i class="fas fa-eye me-2"></i>Transfer Details
                    </h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty transfer}">
                            <table class="table table-bordered">
                                <tr><th>ID</th><td>${transfer.id}</td></tr>
                                <tr><th>From Account</th><td><%= fromAccountNumber %></td></tr>
                                <tr><th>To Account</th><td><%= toAccountNumber %></td></tr>
                                <tr><th>Amount</th><td>$${transfer.amount}</td></tr>
                                <tr><th>Created Date</th><td>${transfer.createdDate}</td></tr>
                                <tr><th>Status</th><td>${transfer.status}</td></tr>
                                <tr><th>Created By</th><td><c:out value="${transfer.createdBy != null ? transfer.createdBy.name : '-'}"/></td></tr>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-danger mt-4">
                                <strong>Transfer not found.</strong><br>
                                The transfer you are looking for does not exist, or you do not have permission to view it.<br>
                                <span class="text-muted small">(If you believe this is an error, please contact support or your system administrator.)</span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <a href="view_transfers" class="btn btn-secondary mt-3"><i class="fas fa-arrow-left me-2"></i>Back to List</a>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html> 