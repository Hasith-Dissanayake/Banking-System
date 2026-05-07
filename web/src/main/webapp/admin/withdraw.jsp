<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bank.app.core.model.Account" %>
<!DOCTYPE html>
<html>
<head>
    <title>Withdraw Funds - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 p-0">
            <%@ include file="/styles/sidebar.jsp" %>
        </div>
        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 main-content p-4">

            <!-- Actions Bar -->
            <div class="row mb-4">
                <div class="col-md-6">
                    <a href="view_withdrawals" class="btn btn-banking-primary">
                        <i class="fas fa-plus me-2"></i>View withdrawals

                    </a>
                </div>

            </div>

            <div class="card card-banking">
                <div class="card-header bg-white d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">
                        <i class="fas fa-money-bill-wave me-2"></i>Withdraw Funds
                    </h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="alert alert-success">${success}</div>
                    </c:if>
                    <c:choose>
                        <c:when test="${empty account}">
                            <!-- Enter account number and verify -->
                            <form action="${pageContext.request.contextPath}/admin/withdraw" method="post" class="row g-3 needs-validation" novalidate>
                                <input type="hidden" name="action" value="verify">
                                <div class="col-md-6">
                                    <label for="accountNumber" class="form-label">Account Number</label>
                                    <input type="text" class="form-control" id="accountNumber" name="accountNumber" required value="${param.accountNumber}">
                                    <div class="invalid-feedback">Please enter a valid account number.</div>
                                </div>
                                <div class="col-12 text-end">
                                    <button type="submit" class="btn btn-banking-primary">
                                        <i class="fas fa-search me-2"></i>Verify Account
                                    </button>
                                </div>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <!-- Show account details and allow withdrawal -->
                            <form action="${pageContext.request.contextPath}/admin/withdraw" method="post" class="row g-3 needs-validation" novalidate>
                                <input type="hidden" name="action" value="withdraw">
                                <input type="hidden" name="accountId" value="${account.id}">
                                <div class="col-md-6">
                                    <label class="form-label">Account</label>
                                    <div class="form-control-plaintext">
                                        <strong>${account.accountNumber}</strong> - ${account.customerName}
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Balance</label>
                                    <div class="form-control-plaintext">
                                        $${account.balance}
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="amount" class="form-label">Amount</label>
                                    <input type="number" class="form-control" id="amount" name="amount" step="0.01" min="0.01" required>
                                    <div class="invalid-feedback">Please enter a valid amount.</div>
                                </div>
                                <div class="col-12 text-end">
                                    <button type="submit" class="btn btn-banking-primary">
                                        <i class="fas fa-money-bill-wave me-2"></i>Withdraw
                                    </button>
                                </div>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
    // Bootstrap validation
    (function () {
        'use strict';
        var forms = document.querySelectorAll('.needs-validation');
        Array.prototype.slice.call(forms).forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>
</body>
</html> 