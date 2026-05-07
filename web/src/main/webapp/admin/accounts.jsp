<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.AccountService" %>
<%@ page import="com.bank.app.core.service.UserService" %>
<%@ page import="com.bank.app.core.model.Account" %>
<%@ page import="com.bank.app.core.model.User" %>
<%@ page import="com.bank.app.core.model.AccountType" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.bank.app.core.model.AccountStatus" %>
<%@ page import="com.bank.app.web.util.AccountUtils" %>
<%@ page import="com.bank.app.core.service.BankBranchService" %>
<%@ page import="com.bank.app.core.model.BankBranch" %>
<!DOCTYPE html>
<html>
<head>
    <title>Account Management - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">

</head>
<body class="bg-light">
<%

    List<Account> accounts = null;
    if (request.getAttribute("accounts") == null) {
        try {
            InitialContext context = new InitialContext();
            AccountService accountService = (AccountService) context.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
            accounts = accountService.getAllAccounts();
            request.setAttribute("accounts", accounts);
        } catch (NamingException e) {
            accounts = new java.util.ArrayList<>();
            request.setAttribute("accounts", accounts);
        } catch (Exception e) {
            accounts = new java.util.ArrayList<>();
            request.setAttribute("accounts", accounts);
        }
    } else {
        accounts = (List<Account>) request.getAttribute("accounts");
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
                    <!-- Success Messages -->
                    <c:if test="${param.success != null}">
                        <div class="alert alert-banking alert-banking-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            <c:choose>
                                <c:when test="${param.success == 'account_added'}">
                                    Account added successfully!
                                </c:when>
                                <c:when test="${param.success == 'account_updated'}">
                                    Account updated successfully!
                                </c:when>
                                <c:when test="${param.success == 'account_deleted'}">
                                    Account deleted successfully!
                                </c:when>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Error Messages -->
                    <c:if test="${param.error != null}">
                        <div class="alert alert-banking alert-banking-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <c:choose>
                                <c:when test="${param.error == 'load_failed'}">
                                    Failed to load accounts. Please try again.
                                </c:when>
                                <c:when test="${param.error == 'all_fields_required'}">
                                    All fields are required.
                                </c:when>
                                <c:when test="${param.error == 'negative_balance'}">
                                    Account balance cannot be negative.
                                </c:when>
                                <c:when test="${param.error == 'account_exists'}">
                                    An account with this account number already exists.
                                </c:when>
                                <c:when test="${param.error == 'user_not_found'}">
                                    Selected user not found.
                                </c:when>
                                <c:when test="${param.error == 'account_not_found'}">
                                    Account not found.
                                </c:when>
                                <c:when test="${param.error == 'cannot_delete_with_balance'}">
                                    Cannot delete account with positive balance.
                                </c:when>
                                <c:when test="${param.error == 'invalid_data'}">
                                    Invalid data provided.
                                </c:when>
                                <c:when test="${param.error == 'invalid_account_type'}">
                                    Invalid account type selected.
                                </c:when>
                                <c:when test="${param.error == 'invalid_action'}">
                                    Invalid action requested.
                                </c:when>
                                <c:when test="${param.error == 'add_failed'}">
                                    Failed to add account. Please try again.
                                </c:when>
                                <c:when test="${param.error == 'update_failed'}">
                                    Failed to update account. Please try again.
                                </c:when>
                                <c:when test="${param.error == 'delete_failed'}">
                                    Failed to delete account. Please try again.
                                </c:when>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Actions Bar -->
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <a href="AddAccount" class="btn btn-banking-primary">
                                <i class="fas fa-plus me-2"></i>Add New Account
                            </a>
                        </div>
                        <div class="col-md-6">
                            <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-search"></i>
                            </span>
                                <input type="text" class="form-control form-control-banking"
                                       placeholder="Search accounts..." id="searchInput">
                            </div>
                        </div>
                    </div>


                    <!-- Accounts Table -->
                    <div class="card card-banking">
                        <div class="card-header bg-white">
                            <h5 class="card-title mb-0">
                                <i class="fas fa-list me-2"></i>Accounts List
                            </h5>
                        </div>
                        <div class="card-body p-0">
                            <!-- Table Info Bar -->
                            <div class="table-info-bar">
                                <div class="account-count">
                                    <i class="fas fa-credit-card me-1"></i>
                                    Total Accounts: <strong>${fn:length(accounts)}</strong>
                                </div>
                                <c:if test="${fn:length(accounts) > 8}">
                                    <div class="scroll-hint">
                                        <i class="fas fa-mouse me-1"></i>
                                        Scroll to see more accounts
                                    </div>
                                </c:if>
                            </div>

                            <!-- Scrollable Table Container -->
                            <div class="table-responsive">
                                <div class="table-vertical-scroll">
                                    <table class="table table-hover mb-0" id="accountsTable">
                                        <thead class="table-dark">
                                        <tr>
                                            <th>ID</th>
                                            <th>Account #</th>
                                            <th>Customer Name</th>
                                            <th>NIC</th>
                                            <th>Type</th>
                                            <th>Balance</th>
                                            <th>Status</th>
                                            <th>Branch</th>
                                            <th>Contact</th>
                                            <th>Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        <% if (accounts != null && !accounts.isEmpty()) { %>
                                        <% for (Account account : accounts) { %>
                                        <tr data-account-id="<%= account.getId() %>">
                                            <td><%= account.getId() %>
                                            </td>
                                            <td>
                                                <strong><%= account.getAccountNumber() %>
                                                </strong>
                                            </td>
                                            <td>
                                                <div>
                                                    <strong><%= account.getCustomerName() != null ? account.getCustomerName() : "N/A" %>
                                                    </strong>
                                                    <% if (account.getEmail() != null) { %>
                                                    <br><small class="text-muted"><%= account.getEmail() %>
                                                </small>
                                                    <% } %>
                                                </div>
                                            </td>
                                            <td>
                                                <code><%= account.getNic() != null ? account.getNic() : "N/A" %>
                                                </code>
                                            </td>
                                            <td>
                                                    <span class="badge bg-<%= AccountUtils.getAccountTypeColor(account.getAccountType()) %>">
                                                                <%= account.getAccountType() != null ? account.getAccountType().getName() : "" %>
                                                    </span>
                                            </td>
                                            <td>
                                                <strong class="text-<%= account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) >= 0 ? "success" : "danger" %>">
                                                    Rs.<%= account.getBalance() != null ? String.format("%.2f", account.getBalance()) : "0.00" %>
                                                </strong>
                                            </td>
                                            <td>
                                                    <span class="badge bg-<%= AccountUtils.getStatusColor(account.getStatus()) %>">
                                                        <%= account.getStatus() %>
                                                    </span>
                                            </td>
                                            <td>
                                                <% if (account.getBankBranch() != null) { %>
                                                <small><%= account.getBankBranch().getBranchName() %>
                                                </small>
                                                <% } else { %>
                                                <span class="text-muted">N/A</span>
                                                <% } %>
                                            </td>
                                            <td>
                                                <% if (account.getContactNumber() != null) { %>
                                                <small><%= account.getContactNumber() %>
                                                </small>
                                                <% } else { %>
                                                <span class="text-muted">N/A</span>
                                                <% } %>
                                            </td>
                                            <td>
                                                <div class="btn-group" role="group">
                                                    <a href="viewAccount?id=<%= account.getId() %>"
                                                       class="btn btn-sm btn-outline-primary"
                                                       title="View Details">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <a href="editAccount?id=<%= account.getId() %>"
                                                       class="btn btn-sm btn-outline-warning"
                                                       title="Edit Account">
                                                        <i class="fas fa-edit"></i>
                                                    </a>

                                                </div>
                                            </td>
                                        </tr>
                                        <% } %>
                                        <% } else { %>
                                        <tr>
                                            <td colspan="10" class="text-center text-muted py-4">
                                                <i class="fas fa-inbox fa-2x mb-3"></i>
                                                <p>No accounts found</p>
                                            </td>
                                        </tr>
                                        <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- View Account Modal -->
    <div class="modal fade" id="viewAccountModal" tabindex="-1" aria-labelledby="viewAccountModalLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="viewAccountModalLabel">
                        <i class="fas fa-eye me-2"></i>Account Details
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h6 class="text-primary mb-3">
                                <i class="fas fa-credit-card me-2"></i>Account Information
                            </h6>
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong>Account Number:</strong></td>
                                    <td id="viewAccountNumber"></td>
                                </tr>
                                <tr>
                                    <td><strong>Account Type:</strong></td>
                                    <td id="viewAccountType"></td>
                                </tr>
                                <tr>
                                    <td><strong>Balance:</strong></td>
                                    <td id="viewBalance"></td>
                                </tr>
                                <tr>
                                    <td><strong>Status:</strong></td>
                                    <td id="viewStatus"></td>
                                </tr>
                                <tr>
                                    <td><strong>Opening Date:</strong></td>
                                    <td id="viewOpeningDate"></td>
                                </tr>
                                <tr>
                                    <td><strong>Last Transaction:</strong></td>
                                    <td id="viewLastTransaction"></td>
                                </tr>
                                <tr>
                                    <td><strong>Bank Branch:</strong></td>
                                    <td id="viewBankBranch"></td>
                                </tr>
                            </table>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-primary mb-3">
                                <i class="fas fa-user me-2"></i>Customer Information
                            </h6>
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong>Customer Name:</strong></td>
                                    <td id="viewCustomerName"></td>
                                </tr>
                                <tr>
                                    <td><strong>NIC Number:</strong></td>
                                    <td id="viewNic"></td>
                                </tr>
                                <tr>
                                    <td><strong>Email:</strong></td>
                                    <td id="viewEmail"></td>
                                </tr>
                                <tr>
                                    <td><strong>Contact Number:</strong></td>
                                    <td id="viewContactNumber"></td>
                                </tr>
                                <tr>
                                    <td><strong>Date of Birth:</strong></td>
                                    <td id="viewBirthday"></td>
                                </tr>
                                <tr>
                                    <td><strong>Address:</strong></td>
                                    <td id="viewAddress"></td>
                                </tr>
                                <tr>
                                    <td><strong>Occupation:</strong></td>
                                    <td id="viewOccupation"></td>
                                </tr>
                                <tr>
                                    <td><strong>Monthly Income:</strong></td>
                                    <td id="viewMonthlyIncome"></td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div class="row mt-4">
                        <div class="col-12">
                            <h6 class="text-primary mb-3">
                                <i class="fas fa-cog me-2"></i>Account Settings
                            </h6>
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong>Interest Rate:</strong></td>
                                    <td id="viewInterestRate"></td>
                                    <td><strong>Minimum Balance:</strong></td>
                                    <td id="viewMinimumBalance"></td>
                                </tr>
                                <tr>
                                    <td><strong>Daily Transaction Limit:</strong></td>
                                    <td id="viewDailyLimit"></td>
                                    <td><strong>Monthly Transaction Limit:</strong></td>
                                    <td id="viewMonthlyLimit"></td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-banking-primary" onclick="editFromView()">
                        <i class="fas fa-edit me-2"></i>Edit Account
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Account Modal -->
    <div class="modal fade" id="editAccountModal" tabindex="-1" aria-labelledby="editAccountModalLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editAccountModalLabel">
                        <i class="fas fa-edit me-2"></i>Edit Account
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post" action="accounts">
                    <input type="hidden" name="action" value="update">
                    <div class="modal-body">
                        <input type="hidden" id="editAccountId" name="id">

                        <!-- Account Information -->
                        <h6 class="text-primary mb-3">
                            <i class="fas fa-credit-card me-2"></i>Account Information
                        </h6>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editAccountNumber" class="form-label">Account Number</label>
                                <input type="text" class="form-control form-control-banking"
                                       id="editAccountNumber" name="accountNumber" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editBalance" class="form-label">Balance</label>
                                <input type="number" class="form-control form-control-banking"
                                       id="editBalance" name="balance" step="0.01" required>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editAccountType" class="form-label">Account Type</label>
                                <select class="form-select form-control-banking"
                                        id="editAccountType" name="accountType" required>
                                    <option value="SAVINGS">Savings</option>
                                    <option value="CHECKING">Checking</option>
                                    <option value="FIXED_DEPOSIT">Fixed Deposit</option>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editStatus" class="form-label">Status</label>
                                <select class="form-select form-control-banking"
                                        id="editStatus" name="status" required>
                                    <option value="ACTIVE">Active</option>
                                    <option value="INACTIVE">Inactive</option>
                                    <option value="SUSPENDED">Suspended</option>
                                    <option value="CLOSED">Closed</option>
                                    <option value="DORMANT">Dormant</option>
                                </select>
                            </div>
                        </div>

                        <!-- Customer Information -->
                        <h6 class="text-primary mb-3 mt-4">
                            <i class="fas fa-user me-2"></i>Customer Information
                        </h6>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editCustomerName" class="form-label">Customer Name</label>
                                <input type="text" class="form-control form-control-banking"
                                       id="editCustomerName" name="customerName" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editNic" class="form-label">NIC Number</label>
                                <input type="text" class="form-control form-control-banking"
                                       id="editNic" name="nic" required>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editEmail" class="form-label">Email</label>
                                <input type="email" class="form-control form-control-banking"
                                       id="editEmail" name="email" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editContactNumber" class="form-label">Contact Number</label>
                                <input type="tel" class="form-control form-control-banking"
                                       id="editContactNumber" name="contactNumber" required>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editBirthday" class="form-label">Date of Birth</label>
                                <input type="date" class="form-control form-control-banking"
                                       id="editBirthday" name="birthday">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editOccupation" class="form-label">Occupation</label>
                                <input type="text" class="form-control form-control-banking"
                                       id="editOccupation" name="occupation">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editMonthlyIncome" class="form-label">Monthly Income</label>
                                <input type="number" class="form-control form-control-banking"
                                       id="editMonthlyIncome" name="monthlyIncome" step="0.01">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editBankBranchId" class="form-label">Bank Branch</label>
                                <select class="form-select form-control-banking"
                                        id="editBankBranchId" name="bankBranchId" required>
                                    <option value="">Select a bank branch</option>
                                    <%
                                        try {
                                            InitialContext context = new InitialContext();
                                            BankBranchService bankBranchService = (BankBranchService) context.lookup("java:global/banking-system-ear/auth-module/BankBranchSessionBean!com.bank.app.core.service.BankBranchService");
                                            List<BankBranch> bankBranches = bankBranchService.getActiveBankBranches();
                                            if (bankBranches != null && !bankBranches.isEmpty()) {
                                                for (BankBranch branch : bankBranches) {
                                    %>
                                    <option value="<%= branch.getId() %>"><%= branch.getBranchName() %>
                                    </option>
                                    <%
                                                }
                                            }
                                        } catch (Exception e) {
                                            // Handle error silently
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="editAddress" class="form-label">Address</label>
                            <textarea class="form-control form-control-banking"
                                      id="editAddress" name="address" rows="3" required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-banking-primary">
                            <i class="fas fa-save me-2"></i>Update Account
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

</div>
<script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>

<script>
    // Search functionality
    document.getElementById('searchInput').addEventListener('keyup', function () {
        const searchTerm = this.value.toLowerCase();
        const table = document.getElementById('accountsTable');
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