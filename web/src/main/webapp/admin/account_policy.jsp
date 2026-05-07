<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bank.app.core.model.AccountType" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.AccountPolicyService" %>
<%@ page import="com.bank.app.core.model.AccountPolicy" %>
<%@ page import="java.util.List" %>
<%
    List<AccountPolicy> policies = (List<AccountPolicy>) request.getAttribute("policies");
    if (policies == null) {
        try {
            InitialContext context = new InitialContext();
            AccountPolicyService policyService = (AccountPolicyService) context.lookup("java:global/banking-system-ear/account-module/AccountPolicySessionBean!com.bank.app.core.service.AccountPolicyService");
            policies = policyService.getAllPolicies();
            request.setAttribute("policies", policies);
        } catch (Exception e) {
            policies = new java.util.ArrayList<>();
            request.setAttribute("policies", policies);
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Account Policy Management</title>

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
            <h2>Account Policy Management (Interest Rates)</h2>
            <c:if test="${param.success != null}">
                <div class="alert alert-success">Policy updated successfully!</div>
            </c:if>
            <c:if test="${param.error != null}">
                <div class="alert alert-danger">Error: ${param.error}</div>
            </c:if>
            <table class="table table-bordered mt-4">
                <thead>
                <tr>
                    <th>Account Type</th>
                    <th>Interest Rate (%)</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="policy" items="${policies}">
                    <tr>
                        <td>${policy.accountType.name}</td>
                        <td>${policy.interestRate}</td>
                        <td>
                            <form method="post" action="accountPolicy" class="d-inline">
                                <input type="hidden" name="accountType" value="${policy.accountType.name}" />
                                <input type="number" step="0.01" name="interestRate" value="${policy.interestRate}" required style="width: 100px;" />
                                <button type="submit" class="btn btn-primary btn-sm">Update</button>
                            </form>
                            <form method="post" action="accountPolicy" class="d-inline ms-2">
                                <input type="hidden" name="accountType" value="${policy.accountType.name}" />
                                <input type="hidden" name="interestRate" value="${policy.interestRate}" />
                                <input type="hidden" name="action" value="applyToAll" />
                                <button type="submit" class="btn btn-warning btn-sm" onclick="return confirm('Update interest rate for ALL accounts of this type?')">Apply to All Accounts</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <h4 class="mt-5">Add/Update Policy for Account Type</h4>
            <form method="post" action="accountPolicy" class="row g-3">
                <div class="col-auto">
                    <select name="accountType" class="form-select" required>
                        <option value="">Select Account Type</option>
                        <c:forEach var="type" items="${accountTypes}">
                            <option value="${type.name}">${type.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-auto">
                    <input type="number" step="0.01" name="interestRate" class="form-control" placeholder="Interest Rate (%)" required />
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-success">Add/Update</button>
                </div>
            </form>
            <h4 class="mt-5">Add New Account Type</h4>
            <form method="post" action="accountTypeManagement" class="row g-3">
                <div class="col-auto">
                    <input type="text" name="name" class="form-control" placeholder="Account Type Name" required />
                </div>
                <div class="col-auto">
                    <input type="text" name="description" class="form-control" placeholder="Description (optional)" />
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-info">Add Account Type</button>
                </div>
            </form>
            <h4 class="mt-5">Available Account Types</h4>
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="type" items="${accountTypes}">
                    <tr>
                        <td>${type.name}</td>
                        <td>${type.description}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${empty accountTypes}">
                <div class="alert alert-danger">No account types found! Please check your EJB/service and servlet logic.</div>
            </c:if>
        </div>
    </div>
</div>
</body>
</html> 