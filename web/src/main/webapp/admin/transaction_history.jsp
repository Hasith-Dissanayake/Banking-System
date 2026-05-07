<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin | Transaction History</title>
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
    <script src="../styles/bootstrap/js/bootstrap.bundle.min.js">

    </script>
    <script>
        function exportTableToCSV(filename) {
            var csv = [];
            var rows = document.querySelectorAll("table tr");
            for (var i = 0; i < rows.length; i++) {
                var row = [], cols = rows[i].querySelectorAll("td, th");
                for (var j = 0; j < cols.length; j++) row.push('"' + cols[j].innerText.replace(/"/g, '""') + '"');
                csv.push(row.join(","));
            }
            var csvFile = new Blob([csv.join("\n")], {type: "text/csv"});
            var downloadLink = document.createElement("a");
            downloadLink.download = filename;
            downloadLink.href = window.URL.createObjectURL(csvFile);
            downloadLink.style.display = "none";
            document.body.appendChild(downloadLink);
            downloadLink.click();
        }
    </script>
</head>
<body>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-3 col-lg-2  p-0">
            <%@ include file="/styles/sidebar.jsp" %>
        </div>
        <div class="col-md-10">
            <!-- Main transaction history content starts here -->
            <h2 class="mt-4 mb-3">Transaction History</h2>
            <form method="get" action="TransactionHistory" class="row g-3 mb-3">
                <div class="col-md-3">
                    <label for="startDate" class="form-label">Start Date</label>
                    <input type="date" class="form-control" id="startDate" name="startDate" value="${param.startDate}">
                </div>
                <div class="col-md-3">
                    <label for="endDate" class="form-label">End Date</label>
                    <input type="date" class="form-control" id="endDate" name="endDate" value="${param.endDate}">
                </div>
                <div class="col-md-2">
                    <label for="type" class="form-label">Type</label>
                    <select class="form-select" id="type" name="type">
                        <option value="">All</option>
                        <option value="DEPOSIT" ${param.type == 'DEPOSIT' ? 'selected' : ''}>Deposit</option>
                        <option value="WITHDRAWAL" ${param.type == 'WITHDRAWAL' ? 'selected' : ''}>Withdrawal</option>
                        <option value="TRANSFER" ${param.type == 'TRANSFER' ? 'selected' : ''}>Transfer</option>
                        <option value="INTEREST" ${param.type == 'INTEREST' ? 'selected' : ''}>Interest</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label for="accountNumber" class="form-label">Account Number</label>
                    <input type="text" class="form-control" id="accountNumber" name="accountNumber" value="${param.accountNumber}" placeholder="Enter account number">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">Filter</button>
                </div>
            </form>
            <div class="mb-3">
                <button class="btn btn-success" onclick="exportTableToCSV('transactions.csv')">Export CSV</button>
                <button class="btn btn-danger ms-2" type="button" onclick="exportPDF()">Export PDF</button>
                <script>
                function exportPDF() {
                    const params = new URLSearchParams(window.location.search);
                    params.set('export', 'pdf');
                    window.location = 'TransactionHistory?' + params.toString();
                }
                </script>
            </div>
            <div class="table-responsive">
                <div class="table-vertical-scroll" style="max-height: 400px; overflow-y: auto;">
                    <table class="table table-striped table-bordered">
                        <thead class="table-dark">
                        <tr>
                            <th>Date/Time</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Account</th>
                            <th>Description</th>
                            <th>Performed By</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="txView" items="${transactions}">
                            <tr>
                                <td>${txView.tx.date}</td>
                                <td>${txView.tx.type}</td>
                                <td>${txView.tx.amount}</td>
                                <td>
                                    <c:forEach var="acc" items="${allAccounts}">
                                        <c:if test="${acc.id == txView.tx.accountId}">
                                            ${acc.accountNumber} - ${acc.customerName}
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>${txView.tx.description}</td>
                                <td>${txView.performedBy}</td>
                                <td>${txView.status}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
</div>
<jsp:include page="../styles/template-footer.jsp"/>
</body>
</html> 