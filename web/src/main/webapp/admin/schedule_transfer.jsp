<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bank.app.core.model.Account" %>
<!DOCTYPE html>
<html>
<head>
    <title>Schedule Transfer - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2  p-0">
            <%@ include file="/styles/sidebar.jsp" %>
        </div>
        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 main-content p-4">
            <!-- Actions Bar -->
            <div class="row mb-4">
                <div class="col-md-6">
                    <a href="scheduled_transfers.jsp" class="btn btn-banking-primary">
                        <i class="fas fa-plus me-2"></i>View Scheduled Transfers

                    </a>
                </div>

            </div>
            <!-- Place all previous content here -->
            <div class="card card-banking">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">
                        <i class="fas fa-clock me-2"></i>Schedule a Transfer
                    </h5>
                </div>
                <div class="card-body">
                    <!-- Success/Error Feedback -->
                    <c:if test="${param.success != null}">
                        <div class="alert alert-success">Scheduled transfer created successfully!</div>
                    </c:if>
                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger">Error: ${param.error}</div>
                    </c:if>
                    <c:choose>
                        <c:when test="${empty requestScope.fromAccount || empty requestScope.toAccount}">
                            <!-- Enter account numbers and verify -->
                            <form action="${pageContext.request.contextPath}/admin/ScheduleTransfer" method="post" class="row g-3 needs-validation" novalidate>
                                <input type="hidden" name="action" value="verify">
                                <div class="col-md-6">
                                    <label for="fromAccountNumber" class="form-label">From Account Number</label>
                                    <input type="text" class="form-control" id="fromAccountNumber" name="fromAccountNumber" required value="${param.fromAccountNumber}">
                                    <div class="invalid-feedback">Please enter a valid account number.</div>
                                </div>
                                <div class="col-md-6">
                                    <label for="toAccountNumber" class="form-label">To Account Number</label>
                                    <input type="text" class="form-control" id="toAccountNumber" name="toAccountNumber" required value="${param.toAccountNumber}">
                                    <div class="invalid-feedback">Please enter a valid account number.</div>
                                </div>
                                <div class="col-12 text-end">
                                    <button type="submit" class="btn btn-banking-primary">
                                        <i class="fas fa-search me-2"></i>Verify Accounts
                                    </button>
                                </div>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <!-- Show customer names and schedule transfer -->
                            <form action="${pageContext.request.contextPath}/admin/ScheduleTransfer" method="post" class="row g-3 needs-validation" novalidate>
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="fromAccountId" value="${fromAccount.id}">
                                <input type="hidden" name="toAccountId" value="${toAccount.id}">
                                <div class="col-md-6">
                                    <label class="form-label">From Account</label>
                                    <div class="form-control-plaintext">
                                        <strong>${fromAccount.accountNumber}</strong> - ${fromAccount.customerName}
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">To Account</label>
                                    <div class="form-control-plaintext">
                                        <strong>${toAccount.accountNumber}</strong> - ${toAccount.customerName}
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="amount" class="form-label">Amount</label>
                                    <input type="number" class="form-control" id="amount" name="amount" step="0.01" min="0.01" required>
                                    <div class="invalid-feedback">Please enter a valid amount.</div>
                                </div>
                                <div class="col-md-6">
                                    <label for="scheduledTime" class="form-label">Scheduled Time</label>
                                    <input type="datetime-local" class="form-control" id="scheduledTime" name="scheduledTime" required>
                                    <div class="invalid-feedback">Please select a scheduled date and time.</div>
                                </div>
                                <div class="col-12 text-end">
                                    <button type="submit" class="btn btn-banking-primary">
                                        <i class="fas fa-clock me-2"></i>Schedule Transfer
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