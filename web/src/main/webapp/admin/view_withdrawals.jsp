<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bank.app.core.model.Withdrawal" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Withdrawal History - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
<%
    List<Withdrawal> withdrawals = (List<Withdrawal>) request.getAttribute("withdrawals");
    java.util.Map<Long, String> accountNumberMap = new java.util.HashMap<>();
    if (withdrawals != null) {

        for (Withdrawal w : withdrawals) {
            if (!accountNumberMap.containsKey(w.getAccountId())) {
                accountNumberMap.put(w.getAccountId(), "N/A"); // Default to "N/A" if not found
            }
        }
    }
%>
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
                    <a href="withdraw.jsp" class="btn btn-banking-primary">
                        <i class="fas fa-plus me-2"></i>Withdraw

                    </a>
                </div>
                <div class="col-md-6">
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-search"></i>
                            </span>
                        <input type="text" class="form-control form-control-banking"
                               placeholder="Withdraws..." id="searchInput">
                    </div>
                </div>
            </div>

            <div class="card card-banking">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">
                        <i class="fas fa-list me-2"></i>Withdrawal History
                    </h5>
                </div>
                <div class="card-body p-0">
                    <c:if test="${empty withdrawals}">
                        <div class="alert alert-warning m-3">No withdrawals found.</div>
                    </c:if>
                    <div class="table-responsive">
                        <div class="table-vertical-scroll" style="max-height: 400px; overflow-y: auto;">
                            <table class="table table-hover mb-0">
                                <thead class="table-dark">
                                    <tr>
                                        <th>ID</th>
                                        <th>Account</th>
                                        <th>Amount</th>
                                        <th>Created By</th>
                                        <th>Created Date</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="withdrawal" items="${withdrawals}">
                                        <tr>
                                            <td>${withdrawal.id}</td>
                                            <td><c:out value="${accountNumberMap[withdrawal.accountId] != null ? accountNumberMap[withdrawal.accountId] : 'N/A'}"/></td>
                                            <td>Rs.${withdrawal.amount}</td>
                                            <td><c:out value="${withdrawal.createdBy != null ? withdrawal.createdBy.name : '-'}"/></td>
                                            <td>${withdrawal.createdDate}</td>
                                            <td>${withdrawal.status}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
    // Search functionality for scheduled transfers
    document.getElementById('searchInput').addEventListener('keyup', function () {
        const searchTerm = this.value.toLowerCase();
        const table = document.querySelector('table');
        const rows = table.getElementsByTagName('tr');
        for (let i = 1; i < rows.length; i++) {
            const row = rows[i];
            const cells = row.getElementsByTagName('td');
            let found = false;
            for (let j = 0; j < cells.length; j++) {
                const cellText = cells[j].textContent.toLowerCase();
                if (cellText.includes(searchTerm)) {
                    found = true;
                    break;
                }
            }
            row.style.display = found ? '' : 'none';
        }
    });
</script>
</body>
</html> 