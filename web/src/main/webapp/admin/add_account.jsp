<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.UserService" %>
<%@ page import="com.bank.app.core.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<%@ page import="com.bank.app.core.service.BankBranchService" %>
<%@ page import="com.bank.app.core.model.BankBranch" %>
<%@ page import="com.bank.app.core.service.AccountPolicyService" %>
<%@ page import="com.bank.app.core.model.AccountPolicy" %>
<%
    java.security.Principal principal = ((jakarta.servlet.http.HttpServletRequest)request).getUserPrincipal();
    String loggedInEmail = principal != null ? principal.getName() : null;
    User loggedInUser = null;
    try {
        InitialContext context = new InitialContext();
        UserService userService = (UserService) context.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        if (loggedInEmail != null) {
            loggedInUser = userService.getUserByEmail(loggedInEmail);
        }
    } catch (Exception e) {

    }

    // Instead, use the first available account type from accountTypes if needed
    List<com.bank.app.core.model.AccountType> accountTypes = (List<com.bank.app.core.model.AccountType>) request.getAttribute("accountTypes");
    com.bank.app.core.model.AccountType defaultType = (accountTypes != null && !accountTypes.isEmpty()) ? accountTypes.get(0) : null;
    com.bank.app.core.service.AccountPolicyService policyService = null;
    com.bank.app.core.model.AccountPolicy policy = null;
    String backendError = null;
    try {
        InitialContext context = new InitialContext();
        policyService = (AccountPolicyService) context.lookup("java:global/banking-system-ear/account-module/AccountPolicySessionBean!com.bank.app.core.service.AccountPolicyService");
        if (defaultType != null) {
            policy = policyService.getPolicyByAccountType(defaultType);
        }
    } catch (Exception e) { backendError = "Error loading account policy: " + e.getMessage(); }
    String defaultInterest = (policy != null) ? policy.getInterestRate().toString() : "0.00";
%>
<!DOCTYPE html>
<html>
<head>
    <title>Add Account - Banking System</title>
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
                <%@ include file="../styles/sidebar.jsp" %>
            </div>
            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content p-4">
                <div class="card card-banking">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-plus-circle me-2"></i>Add New Account
                        </h5>
                    </div>
                    <div class="card-body">
                        <!-- Error Messages -->
                        <c:if test="${param.error != null}">
                            <div class="alert alert-banking alert-banking-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                <c:choose>
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
                                    <c:when test="${param.error == 'invalid_data'}">
                                        Invalid data provided.
                                    </c:when>
                                    <c:when test="${param.error == 'invalid_account_type'}">
                                        Invalid account type selected.
                                    </c:when>
                                    <c:when test="${param.error == 'creation_failed'}">
                                        Failed to create account. Please try again.
                                    </c:when>
                                </c:choose>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        <c:if test="${not empty backendError}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>${backendError}
                            </div>
                        </c:if>

                        
                        <form method="post" action="accounts" class="needs-validation" novalidate>
                            <input type="hidden" name="action" value="add">
                            
                            <!-- Account Information -->
                            <div class="row">
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
                                                <input type="text" class="form-control" id="accountNumber" name="accountNumber" value="(Will be auto-generated)" readonly>
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
                                                <input type="number" class="form-control" id="balance" name="balance" step="0.01" value="0" required>
                                            </div>
                                            <div class="mb-3">
                                                <label for="dailyTransactionLimit" class="form-label">Daily Transaction Limit</label>
                                                <input type="number" class="form-control" id="dailyTransactionLimit" name="dailyTransactionLimit" value="200000" required>
                                            </div>
                                            <div class="mb-3">
                                                <label for="monthlyTransactionLimit" class="form-label">Monthly Transaction Limit</label>
                                                <input type="number" class="form-control" id="monthlyTransactionLimit" name="monthlyTransactionLimit" value="5000000" required>
                                            </div>
                                            <div class="mb-3">
                                                <label for="interestRate" class="form-label">Interest Rate (%)</label>
                                                <input type="text" class="form-control" id="interestRate" name="interestRate" value="<%= defaultInterest %>" readonly>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="bankBranchId" class="form-label">Bank Branch</label>
                                    <select class="form-select form-control-banking" 
                                            id="bankBranchId" name="bankBranchId" required>
                                        <option value="">Select a bank branch</option>
                                        <%
                                            try {
                                                InitialContext context = new InitialContext();
                                                BankBranchService bankBranchService = (BankBranchService) context.lookup("java:global/banking-system-ear/auth-module/BankBranchSessionBean!com.bank.app.core.service.BankBranchService");
                                                List<BankBranch> bankBranches = bankBranchService.getActiveBankBranches();
                                                if (bankBranches != null && !bankBranches.isEmpty()) {
                                                    for (BankBranch branch : bankBranches) {
                                        %>
                                                        <option value="<%= branch.getId() %>"><%= branch.getBranchName() %></option>
                                        <%
                                                    }
                                                } else {
                                        %>
                                                        <option value="" disabled>No bank branches available</option>
                                        <%
                                                }
                                            } catch (NamingException e) {
                                        %>
                                                <option value="" disabled>Error loading bank branches: <%= e.getMessage() %></option>
                                        <%
                                            } catch (Exception e) {
                                        %>
                                                <option value="" disabled>Error: <%= e.getMessage() %></option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    <div class="invalid-feedback">
                                        Please select a bank branch.
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Customer Information -->
                            <hr class="my-4">
                            <h6 class="mb-3">
                                <i class="fas fa-user me-2"></i>Customer Information
                            </h6>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="customerName" class="form-label">Full Name</label>
                                    <input type="text" class="form-control form-control-banking" 
                                           id="customerName" name="customerName" required 
                                           placeholder="Enter customer's full name">
                                    <div class="invalid-feedback">
                                        Please provide the customer's full name.
                                    </div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="nic" class="form-label">NIC Number</label>
                                    <input type="text" class="form-control form-control-banking" 
                                           id="nic" name="nic" required 
                                           placeholder="e.g., 123456789V">
                                    <div class="invalid-feedback">
                                        Please provide a valid NIC number.
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label">Email Address</label>
                                    <input type="email" class="form-control form-control-banking" 
                                           id="email" name="email" required 
                                           placeholder="customer@example.com">
                                    <div class="invalid-feedback">
                                        Please provide a valid email address.
                                    </div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="contactNumber" class="form-label">Contact Number</label>
                                    <input type="tel" class="form-control form-control-banking" 
                                           id="contactNumber" name="contactNumber" required 
                                           placeholder="e.g., 0771234567">
                                    <div class="invalid-feedback">
                                        Please provide a valid contact number.
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="birthday" class="form-label">Date of Birth</label>
                                    <input type="date" class="form-control form-control-banking" id="birthday" name="birthday" required>
                                    <div class="invalid-feedback">
                                        Please provide the customer's date of birth.
                                    </div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="occupation" class="form-label">Occupation</label>
                                    <input type="text" class="form-control form-control-banking" 
                                           id="occupation" name="occupation" 
                                           placeholder="e.g., Software Engineer">
                                    <div class="form-text">Optional - for risk assessment</div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="monthlyIncome" class="form-label">Monthly Income</label>
                                    <div class="input-group">
                                        <span class="input-group-text">Rs.</span>
                                        <input type="number" class="form-control form-control-banking" 
                                               id="monthlyIncome" name="monthlyIncome" step="0.01" min="0" 
                                               placeholder="0.00">
                                    </div>
                                    <div class="form-text">Optional - for credit assessment</div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <input type="hidden" name="userId" value="<%= loggedInUser != null ? loggedInUser.getId() : "" %>" />
                                    <span class="form-text">This account will be linked to your user account: <%= loggedInUser != null ? (loggedInUser.getName() + " (" + loggedInUser.getEmail() + ")") : "Unknown user" %></span>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="address" class="form-label">Address</label>
                                <textarea class="form-control form-control-banking" 
                                          id="address" name="address" rows="3" required 
                                          placeholder="Enter customer's complete address"></textarea>
                                <div class="invalid-feedback">
                                    Please provide the customer's address.
                                </div>
                            </div>
                            
                            <!-- Account Type Information -->
                            <div class="alert alert-info" id="accountTypeInfo" style="display: none;">
                                <i class="fas fa-info-circle me-2"></i>
                                <span id="accountTypeDescription"></span>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-banking-primary">
                                    <i class="fas fa-plus-circle me-2"></i>Create Account
                                </button>
                                <a href="accounts.jsp" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>Back to Accounts
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    

    <script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>
    <script>
        // Form validation
        (function() {
            'use strict';
            window.addEventListener('load', function() {
                var forms = document.getElementsByClassName('needs-validation');
                var validation = Array.prototype.filter.call(forms, function(form) {
                    form.addEventListener('submit', function(event) {
                        if (form.checkValidity() === false) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                        form.classList.add('was-validated');
                    }, false);
                });
            }, false);
        })();
        
        // Account type information display
        document.getElementById('accountType').addEventListener('change', function() {
            const accountType = this.value;
            const infoDiv = document.getElementById('accountTypeInfo');
            const descriptionSpan = document.getElementById('accountTypeDescription');
            
            if (accountType) {
                let description = '';
                switch(accountType) {
                    case 'SAVINGS':
                        description = 'Savings accounts earn interest and are designed for long-term saving.';
                        break;
                    case 'CHECKING':
                        description = 'Checking accounts are for daily transactions and bill payments.';
                        break;
                    case 'FIXED_DEPOSIT':
                        description = 'Fixed deposit accounts offer higher interest rates for a fixed period.';
                        break;
                }
                descriptionSpan.textContent = description;
                infoDiv.style.display = 'block';
            } else {
                infoDiv.style.display = 'none';
            }
        });

        // Auto-populate interest rate on account type change
        document.getElementById('accountType').addEventListener('change', function() {
            var type = this.value;
            fetch('accountPolicy/getInterestRate?type=' + type)
                .then(response => response.text())
                .then(rate => {
                    document.getElementById('interestRate').value = rate;
                });
        });
        
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
</body>
</html> 