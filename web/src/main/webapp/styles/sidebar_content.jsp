<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h4 class="text-center mb-4">
    <i class="fas fa-university me-2"></i>Banking System
</h4>
<hr>
<ul class="nav nav-pills flex-column mb-auto">
    <li class="nav-item">
        <a href="index.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/admin/index.jsp' ? 'active' : ''}">
            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
        </a>
    </li>
    <li class="nav-item">
        <a href="accounts.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/admin/accounts.jsp' ? 'active' : ''}">
            <i class="fas fa-credit-card me-2"></i>Account Management
        </a>
    </li>
    <li class="nav-item">
        <a href="users.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/admin/users.jsp' ? 'active' : ''}">
            <i class="fas fa-users me-2"></i>User Management
        </a>
    </li>
    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/admin/transfer.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/admin/transfer.jsp' ? 'active' : ''}">
            <i class="fas fa-exchange-alt me-2"></i>Transfer
        </a>
    </li>
    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/admin/schedule_transfer.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/admin/schedule_transfer.jsp' ? 'active' : ''}">
            <i class="fas fa-clock me-2"></i>Schedule Transfer
        </a>
    </li>
    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/admin/withdraw.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/admin/' ? 'active' : ''}">
            <i class="fas fa-money-bill-wave me-2"></i>Withdraw Funds
        </a>
    </li>
    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/admin/deposit.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/admin/deposit.jsp' ? 'active' : ''}">
            <i class="fas fa-money-bill-wave me-2"></i>Deposit Funds
        </a>
    </li>

    

    <c:if test="${sessionScope.userType == 'ADMIN' || sessionScope.userType == 'SUPER_ADMIN'}">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/TransactionHistory" class="nav-link text-white ${pageContext.request.servletPath == '/admin/transaction_history.jsp' ? 'active' : ''}">
                <i class="fas fa-list-alt me-2"></i>Transaction History
            </a>
        </li>
    </c:if>
    <!-- Only show to SUPER_ADMIN -->
    <c:if test="${sessionScope.userType == 'SUPER_ADMIN'}">
        <li class="nav-item">
            <a href="bankBranches" class="nav-link text-white ${pageContext.request.servletPath == '/admin/bankBranches' ? 'active' : ''}">
                <i class="fas fa-building me-2"></i>Bank Branch Management
            </a>
        </li>
        <li class="nav-item">
            <a href="accountPolicy" class="nav-link text-white ${pageContext.request.servletPath == '/admin/accountPolicy' ? 'active' : ''}">
                <i class="fas fa-percent me-2"></i>Account Policy
            </a>
        </li>
    </c:if>
</ul>
<hr>
<div class="dropdown">
    <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" id="dropdownUser1" data-bs-toggle="dropdown" aria-expanded="false">
        <i class="fas fa-user-circle me-2"></i>
        <strong>${sessionScope.username}</strong>
    </a>
    <ul class="dropdown-menu dropdown-menu-dark text-small shadow" aria-labelledby="dropdownUser1">
        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Sign out</a></li>
    </ul>
</div> 