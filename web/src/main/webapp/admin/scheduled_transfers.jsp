<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.ScheduledTransferService" %>
<%@ page import="com.bank.app.core.model.ScheduledTransfer" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<%@ page import="com.bank.app.core.service.AccountService" %>
<%@ page import="com.bank.app.core.model.Account" %>
<!DOCTYPE html>
<html>
<head>
    <title>Scheduled Transfers - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
<%
    List<ScheduledTransfer> scheduledTransfers = null;
    java.util.Map<Long, String> accountNumberMap = new java.util.HashMap<>();
    if (request.getAttribute("scheduledTransfers") == null) {
        try {
            InitialContext context = new InitialContext();
            ScheduledTransferService scheduledTransferService = (ScheduledTransferService) context.lookup("java:global/banking-system-ear/account-module/ScheduledTransferSessionBean!com.bank.app.core.service.ScheduledTransferService");
            scheduledTransfers = scheduledTransferService.getAllScheduledTransfers();
            request.setAttribute("scheduledTransfers", scheduledTransfers);
            // Fetch account numbers for all involved accounts
            AccountService accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
            for (ScheduledTransfer st : scheduledTransfers) {
                if (!accountNumberMap.containsKey(st.getFromAccountId())) {
                    Account fromAcc = accountService.getAccountById(st.getFromAccountId());
                    accountNumberMap.put(st.getFromAccountId(), fromAcc != null ? fromAcc.getAccountNumber() : "N/A");
                }
                if (!accountNumberMap.containsKey(st.getToAccountId())) {
                    Account toAcc = accountService.getAccountById(st.getToAccountId());
                    accountNumberMap.put(st.getToAccountId(), toAcc != null ? toAcc.getAccountNumber() : "N/A");
                }
            }
            request.setAttribute("accountNumberMap", accountNumberMap);
        } catch (NamingException e) {
            scheduledTransfers = new java.util.ArrayList<>();
            request.setAttribute("scheduledTransfers", scheduledTransfers);
        } catch (Exception e) {
            scheduledTransfers = new java.util.ArrayList<>();
            request.setAttribute("scheduledTransfers", scheduledTransfers);
        }
    } else {
        scheduledTransfers = (List<ScheduledTransfer>) request.getAttribute("scheduledTransfers");
        accountNumberMap = (java.util.Map<Long, String>) request.getAttribute("accountNumberMap");
        if (accountNumberMap == null) accountNumberMap = new java.util.HashMap<>();
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
            <!-- Actions Bar -->
            <div class="row mb-4">
                <div class="col-md-6">
                    <a href="schedule_transfer.jsp" class="btn btn-banking-primary">
                        <i class="fas fa-plus me-2"></i>Add Schedule a Transfer

                    </a>
                </div>
                <div class="col-md-6">
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-search"></i>
                            </span>
                        <input type="text" class="form-control form-control-banking"
                               placeholder="Scheduled Transfers..." id="searchInput">
                    </div>
                </div>
            </div>

            <div class="card card-banking">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">
                        <i class="fas fa-list me-2"></i>Scheduled Transfers
                    </h5>
                </div>
                <div class="card-body p-0">
                    <c:if test="${empty scheduledTransfers}">
                        <div class="alert alert-warning m-3">No scheduled transfers found.</div>
                    </c:if>
                    <div class="table-responsive">
                        <div class="table-vertical-scroll">
                            <table class="table table-hover mb-0">
                            <thead class="table-dark">
                                <tr>
                                    <th>ID</th>
                                    <th>From Account</th>
                                    <th>To Account</th>
                                    <th>Amount</th>
                                    <th>Scheduled Date</th>
                                    <th>Executed Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="transfer" items="${scheduledTransfers}">
                                    <tr>
                                        <td>${transfer.id}</td>
                                        <td>${accountNumberMap[transfer.fromAccountId]}</td>
                                        <td>${accountNumberMap[transfer.toAccountId]}</td>
                                        <td>$${transfer.amount}</td>
                                        <td>${transfer.scheduledDate}</td>
                                        <td><c:out value="${transfer.executedDate != null ? transfer.executedDate : '-'}"/></td>
                                        <td>
                                            <span class="badge bg-${transfer.status == 'PENDING' ? 'warning' : transfer.status == 'COMPLETED' ? 'success' : transfer.status == 'FAILED' ? 'danger' : 'secondary'}">
                                                ${transfer.status}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="btn-group btn-group-sm" role="group">
                                                <!-- View Button as link -->
                                                <a href="view_scheduled_transfer?id=${transfer.id}" class="btn btn-outline-primary" title="View">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <!-- Cancel Button (only if pending) -->
                                                <c:if test="${transfer.status == 'PENDING'}">
                                                    <form action="ScheduleTransfer" method="post" style="display:inline;">
                                                        <input type="hidden" name="action" value="cancel">
                                                        <input type="hidden" name="id" value="${transfer.id}">
                                                        <button type="submit" class="btn btn-outline-danger" title="Cancel" onclick="return confirm('Cancel this scheduled transfer?');">
                                                            <i class="fas fa-times"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </div>
                                        </td>
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