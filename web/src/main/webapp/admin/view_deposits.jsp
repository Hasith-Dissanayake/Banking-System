<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bank.app.core.model.Deposit" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Deposit History - Banking System</title>
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
                    <a href="deposit.jsp" class="btn btn-banking-primary">
                        <i class="fas fa-plus me-2"></i>Deposit

                    </a>
                </div>
                <div class="col-md-6">
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-search"></i>
                            </span>
                        <input type="text" class="form-control form-control-banking"
                               placeholder="Deposit..." id="searchInput">
                    </div>
                </div>
            </div>

            <div class="card card-banking">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">
                        <i class="fas fa-list me-2"></i>Deposit History
                    </h5>
                </div>
                <div class="card-body p-0">
                    <c:if test="${empty deposits}">
                        <div class="alert alert-warning m-3">No deposits found.</div>
                    </c:if>
                    <div class="table-responsive">
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
                                <c:forEach var="deposit" items="${deposits}">
                                    <tr>
                                        <td>${deposit.id}</td>
                                        <td><c:out value="${accountNumberMap[deposit.accountId] != null ? accountNumberMap[deposit.accountId] : 'N/A'}"/></td>
                                        <td>Rs.${deposit.amount}</td>
                                        <td><c:out value="${deposit.createdBy != null ? deposit.createdBy.name : '-'}"/></td>
                                        <td>${deposit.createdDate}</td>
                                        <td>${deposit.status}</td>
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