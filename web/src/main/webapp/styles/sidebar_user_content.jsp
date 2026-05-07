<h4 class="text-center mb-4">
    <i class="fas fa-university me-2"></i>Banking System
</h4>
<hr>
<ul class="nav nav-pills flex-column mb-auto">
    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/user/index.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/user/index.jsp' ? 'active' : ''}">
            <i class="fas fa-exchange-alt me-2"></i>Dashboard
        </a>
    </li>
    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/user/user_transfer.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/user/user_transfer.jsp' ? 'active' : ''}">
            <i class="fas fa-exchange-alt me-2"></i>Transfer
        </a>
    </li>
    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/user/user_withdraw.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/user/user_withdraw.jsp' ? 'active' : ''}">
            <i class="fas fa-money-bill-wave me-2"></i>Withdraw Funds
        </a>
    </li>
    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/user/user_deposit.jsp" class="nav-link text-white ${pageContext.request.servletPath == '/user/user_deposit.jsp' ? 'active' : ''}">
            <i class="fas fa-coins me-2"></i>Deposit Funds
        </a>
    </li>

    <li class="nav-item">
        <a href="${pageContext.request.contextPath}/user/my_transaction_history" class="nav-link text-white ${pageContext.request.servletPath == '/user/my_transaction_history.jsp' ? 'active' : ''}">
            <i class="fas fa-coins me-2"></i>My Transaction History
        </a>
    </li>
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