<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bank.app.core.model.Account" %>
<%@ page import="com.bank.app.core.model.AccountType" %>
<%@ page import="com.bank.app.core.service.AccountPolicyService" %>
<%@ page import="com.bank.app.core.model.AccountPolicy" %>
<%@ page import="javax.naming.InitialContext" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Account - Banking System</title>

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
                    <h2><i class="fas fa-edit me-2"></i>Edit Account</h2>
                    <div>
                        <a href="viewAccount?id=${account.id}" class="btn btn-info me-2">
                            <i class="fas fa-eye me-1"></i>View Account
                        </a>
                    <a href="accounts.jsp" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-1"></i>Back to Accounts
                        </a>
                    </div>
                </div>

                <!-- Error Messages -->
                <c:if test="${param.error != null}">
                    <div class="alert alert-banking alert-banking-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        <c:choose>
                            <c:when test="${param.error == 'all_fields_required'}">
                                All fields are required.
                            </c:when>
                            <c:when test="${param.error == 'invalid_data'}">
                                Invalid data provided.
                            </c:when>
                            <c:when test="${param.error == 'account_not_found'}">
                                Account not found.
                            </c:when>
                            <c:when test="${param.error == 'update_failed'}">
                                Failed to update account. Please try again.
                            </c:when>
                            <c:otherwise>
                                ${param.error}
                            </c:otherwise>
                        </c:choose>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <form action="accounts" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${account.id}">
                    
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
                                    <div class="mb-3">
                                        <label for="accountNumber" class="form-label">Account Number</label>
                                        <input type="text" class="form-control" id="accountNumber" name="accountNumber" 
                                               value="${account.accountNumber}" readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label for="accountType" class="form-label">Account Type</label>
                                        <select class="form-select" id="accountType" name="accountType" required>
                                        <option value="">Select Account Type</option>
                                        <c:forEach var="type" items="${accountTypes}">
                                            <option value="${type.name}" ${account != null && account.accountType != null && account.accountType.name == type.name ? 'selected' : ''}>${type.name}</option>
                                        </c:forEach>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="balance" class="form-label">Balance</label>
                                        <input type="number" class="form-control" id="balance" name="balance" 
                                               value="${account.balance}" step="0.01" required>
                                    </div>
                                    <div class="mb-3">
                                        <label for="status" class="form-label">Status</label>
                                        <select class="form-select" id="status" name="status" required>
                                        <option value="ACTIVE" ${account.status == 'ACTIVE' ? 'selected' : ''}>Active
                                        </option>
                                        <option value="INACTIVE" ${account.status == 'INACTIVE' ? 'selected' : ''}>
                                            Inactive
                                        </option>
                                        <option value="SUSPENDED" ${account.status == 'SUSPENDED' ? 'selected' : ''}>
                                            Suspended
                                        </option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="bankBranchId" class="form-label">Bank Branch</label>
                                        <select class="form-select" id="bankBranchId" name="bankBranchId" required>
                                            <option value="">Select Bank Branch</option>
                                            <c:forEach var="branch" items="${bankBranches}">
                                                <option value="${branch.id}" ${account.bankBranch.id == branch.id ? 'selected' : ''}>
                                                    ${branch.branchName}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                <div class="mb-3">
                                    <input type="hidden" name="userId"
                                           value="${account.user != null ? account.user.id : ''}"/>
                                    <span class="form-text">
                                            This account is linked to user: 
                                            <c:choose>
                                                <c:when test="${account.user != null}">
                                                    ${account.user.name} (${account.user.email})
                                                </c:when>
                                                <c:otherwise>
                                                    No user linked
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                </div>
                                <%
                                    Account account = (Account) request.getAttribute("account");
                                    AccountType currentType = account != null ? account.getAccountType() : null;
                                    AccountPolicyService policyService = null;
                                    AccountPolicy policy = null;
                                    try {
                                        InitialContext context = new InitialContext();
                                        policyService = (AccountPolicyService) context.lookup("java:global/banking-system-ear/account-module/AccountPolicySessionBean!com.bank.app.core.service.AccountPolicyService");
                                        policy = policyService.getPolicyByAccountType(currentType);
                                    } catch (Exception e) { /* ignore */ }
                                    String currentInterest = (policy != null) ? policy.getInterestRate().toString() : "0.00";
                                %>
                                <div class="mb-3">
                                    <label for="interestRate" class="form-label">Interest Rate (%)</label>
                                    <input type="text" class="form-control" id="interestRate" name="interestRate"
                                           value="<%= currentInterest %>" readonly>
                                </div>
                                <div class="mb-3">
                                    <label for="minimumBalance" class="form-label">Minimum Balance</label>
                                    <input type="number" class="form-control" id="minimumBalance" name="minimumBalance"
                                           step="0.01"
                                           value="${account.minimumBalance != null ? account.minimumBalance : ''}">
                                </div>
                                <div class="mb-3">
                                    <label for="dailyTransactionLimit" class="form-label">Daily Transaction
                                        Limit</label>
                                    <input type="number" class="form-control" id="dailyTransactionLimit"
                                           name="dailyTransactionLimit"
                                           value="${account.dailyTransactionLimit != null ? account.dailyTransactionLimit : ''}">
                                </div>
                                <div class="mb-3">
                                    <label for="monthlyTransactionLimit" class="form-label">Monthly Transaction
                                        Limit</label>
                                    <input type="number" class="form-control" id="monthlyTransactionLimit"
                                           name="monthlyTransactionLimit"
                                           value="${account.monthlyTransactionLimit != null ? account.monthlyTransactionLimit : ''}">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Opening Date</label>
                                    <input type="text" class="form-control" value="${account.openingDate}" readonly>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Last Transaction Date</label>
                                    <input type="text" class="form-control" value="${account.lastTransactionDate}"
                                           readonly>
                                </div>
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
                                    <div class="mb-3">
                                        <label for="customerName" class="form-label">Customer Name</label>
                                        <input type="text" class="form-control" id="customerName" name="customerName" 
                                               value="${account.customerName}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label for="nic" class="form-label">NIC Number</label>
                                        <input type="text" class="form-control" id="nic" name="nic" 
                                               value="${account.nic}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label for="email" class="form-label">Email</label>
                                        <input type="email" class="form-control" id="email" name="email" 
                                               value="${account.email}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label for="contactNumber" class="form-label">Contact Number</label>
                                        <input type="text" class="form-control" id="contactNumber" name="contactNumber" 
                                               value="${account.contactNumber}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label for="birthday" class="form-label">Date of Birth</label>
                                        <input type="date" class="form-control" id="birthday" name="birthday" 
                                               value="${account.birthday}">
                                    </div>
                                    <div class="mb-3">
                                        <label for="address" class="form-label">Address</label>
                                    <textarea class="form-control" id="address" name="address" rows="3"
                                              required>${account.address}</textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="occupation" class="form-label">Occupation</label>
                                        <input type="text" class="form-control" id="occupation" name="occupation" 
                                               value="${account.occupation}">
                                    </div>
                                    <div class="mb-3">
                                        <label for="monthlyIncome" class="form-label">Monthly Income</label>
                                        <input type="number" class="form-control" id="monthlyIncome" name="monthlyIncome" 
                                               value="${account.monthlyIncome}" step="0.01">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Submit Buttons -->
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body text-center">
                                    <button type="submit" class="btn btn-primary btn-lg me-3">
                                        <i class="fas fa-save me-2"></i>Update Account
                                    </button>
                                    <a href="accounts" class="btn btn-secondary btn-lg">
                                        <i class="fas fa-times me-2"></i>Cancel
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

<script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>

<script>
    document.getElementById('accountType').addEventListener('change', function () {
        var type = this.value;
        fetch('accountPolicy/getInterestRate?type=' + type)
            .then(response => response.text())
            .then(rate => {
                document.getElementById('interestRate').value = rate;
            });
    });
</script>
</body>
</html> 