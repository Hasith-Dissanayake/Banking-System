<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Account - Banking System</title>

    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">


</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2  p-0">
                <%@ include file="../styles/sidebar.jsp" %>
            </div>

            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2><i class="fas fa-eye me-2"></i>View Account Details</h2>
                    <div>
                        <a href="editAccount?id=${account.id}" class="btn btn-warning me-2">
                            <i class="fas fa-edit me-1"></i>Edit Account
                        </a>
                        <a href="accounts.jsp" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-1"></i>Back to Accounts
                        </a>
                    </div>
                </div>

                <div class="row">
                    <!-- Account Information -->
                    <div class="col-md-6 mb-4">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-credit-card me-2"></i>Account Information
                                </h5>
                            </div>
                            <div class="card-body">
                                <table class="table table-borderless">
                                    <tr>
                                        <td><strong>Account ID:</strong></td>
                                        <td>${account.id}</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Account Number:</strong></td>
                                        <td><code>${account.accountNumber}</code></td>
                                    </tr>
                                    <tr>
                                        <td><strong>Account Type:</strong></td>
                                        <td>
                                            <span class="badge bg-${account.accountType == 'SAVINGS' ? 'success' : account.accountType == 'CHECKING' ? 'primary' : 'warning'}">
                                                ${account.accountType.name}
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><strong>Balance:</strong></td>
                                        <td>
                                            <strong class="text-${account.balance != null && account.balance >= 0 ? 'success' : 'danger'}">
                                                ${account.balance != null ? account.balance : 'N/A'}
                                            </strong>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><strong>Status:</strong></td>
                                        <td>
                                            <span class="badge bg-${account.status == 'ACTIVE' ? 'success' : 'danger'}">
                                                ${account.status}
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><strong>Opening Date:</strong></td>
                                        <td>${account.openingDate}</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Bank Branch:</strong></td>
                                        <td>${account.bankBranch != null ? account.bankBranch.branchName : 'N/A'}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>

                    <!-- Customer Information -->
                    <div class="col-md-6 mb-4">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-user me-2"></i>Customer Information
                                </h5>
                            </div>
                            <div class="card-body">
                                <table class="table table-borderless">
                                    <tr>
                                        <td><strong>Customer Name:</strong></td>
                                        <td>${account.customerName}</td>
                                    </tr>
                                    <tr>
                                        <td><strong>NIC Number:</strong></td>
                                        <td><code>${account.nic}</code></td>
                                    </tr>
                                    <tr>
                                        <td><strong>Email:</strong></td>
                                        <td>${account.email}</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Contact Number:</strong></td>
                                        <td>${account.contactNumber}</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Date of Birth:</strong></td>
                                        <td>${account.birthday != null ? account.birthday : 'N/A'}</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Address:</strong></td>
                                        <td>${account.address}</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Occupation:</strong></td>
                                        <td>${account.occupation != null ? account.occupation : 'N/A'}</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Monthly Income:</strong></td>
                                        <td>${account.monthlyIncome != null ? account.monthlyIncome : 'N/A'}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Account Settings -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">
                                    <i class="fas fa-cog me-2"></i>Account Settings
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-3">
                                        <div class="text-center">
                                            <h6 class="text-muted">Interest Rate</h6>
                                            <h4 class="text-primary">${account.interestRate != null ? account.interestRate : 'N/A'}</h4>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="text-center">
                                            <h6 class="text-muted">Minimum Balance</h6>
                                            <h4 class="text-info">${account.minimumBalance != null ? account.minimumBalance : 'N/A'}</h4>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="text-center">
                                            <h6 class="text-muted">Daily Limit</h6>
                                            <h4 class="text-warning">${account.dailyTransactionLimit != null ? account.dailyTransactionLimit : 'N/A'}</h4>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="text-center">
                                            <h6 class="text-muted">Monthly Limit</h6>
                                            <h4 class="text-success">${account.monthlyTransactionLimit != null ? account.monthlyTransactionLimit : 'N/A'}</h4>
                                        </div>
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
</body>
</html> 