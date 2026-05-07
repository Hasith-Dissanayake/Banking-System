<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.UserService" %>
<%@ page import="com.bank.app.core.model.User" %>
<%@ page import="com.bank.app.core.model.UserType" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
<%
    try {
        InitialContext ic = new InitialContext();
        UserService userService = (UserService) ic.lookup("com.bank.app.core.service.UserService");
        List<User> users = userService.getAllUsers();
        pageContext.setAttribute("users", users);
    } catch (NamingException e) {
        throw new RuntimeException(e);
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
                            <c:when test="${param.success == 'user_added'}">
                                User added successfully!
                            </c:when>
                            <c:when test="${param.success == 'user_added_verification_sent'}">
                                User added successfully! Verification email has been sent.
                            </c:when>
                            <c:when test="${param.success == 'user_updated'}">
                                User updated successfully!
                            </c:when>
                            <c:when test="${param.success == 'user_deleted'}">
                                User deleted successfully!
                            </c:when>
                            <c:when test="${param.success == 'user_activated'}">
                                User activated successfully!
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
                            <c:when test="${param.error == 'invalid_user'}">
                                Invalid user selected.
                            </c:when>
                            <c:when test="${param.error == 'user_not_found'}">
                                User not found.
                            </c:when>
                            <c:when test="${param.error == 'user_id_required'}">
                                User ID is required.
                            </c:when>
                            <c:when test="${param.error == 'invalid_user_id'}">
                                Invalid user ID.
                            </c:when>
                            <c:when test="${param.error == 'load_failed'}">
                                Failed to load user data.
                            </c:when>
                            <c:when test="${param.error == 'unauthorized'}">
                                You are not authorized to perform this action.
                            </c:when>
                            <c:when test="${param.error == 'unauthorized_edit_admin'}">
                                You are not authorized to edit admin users.
                            </c:when>
                            <c:when test="${param.error == 'email_exists'}">
                                A user with this email already exists.
                            </c:when>
                            <c:when test="${param.error == 'nic_exists'}">
                                A user with this NIC number already exists.
                            </c:when>
                            <c:when test="${param.error == 'all_fields_required'}">
                                All required fields must be filled.
                            </c:when>
                            <c:when test="${param.error == 'invalid_bank_branch'}">
                                Invalid bank branch selected.
                            </c:when>
                            <c:when test="${param.error == 'update_failed'}">
                                Failed to update user.
                            </c:when>
                            <c:when test="${param.error == 'delete_failed'}">
                                Failed to delete user.
                            </c:when>
                            <c:when test="${param.error == 'cannot_delete_super_admin'}">
                                Cannot delete super admin user.
                            </c:when>
                            <c:when test="${param.error == 'unauthorized_edit_super_admin'}">
                                Only super admins can edit super admin users.
                            </c:when>
                        </c:choose>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <!-- Actions Bar -->
                <div class="row mb-4">
                    <div class="col-md-6">
                        <a href="add_user.jsp" class="btn btn-banking-primary">
                            <i class="fas fa-plus me-2"></i>Add New User
                        </a>
                    </div>
                    <div class="col-md-6">
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-search"></i>
                            </span>
                            <input type="text" class="form-control form-control-banking" placeholder="Search users..." id="searchInput">
                        </div>
                    </div>
                </div>
                
                <!-- Users Table -->
                <div class="card card-banking">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-list me-2"></i>Users List
                        </h5>
                    </div>
                    <div class="card-body p-0">
                        <!-- Table Info Bar -->
                        <div class="table-info-bar">
                            <div class="user-count">
                                <i class="fas fa-users me-1"></i>
                                Total Users: <strong>${fn:length(users)}</strong>
                            </div>
                            <c:if test="${fn:length(users) > 8}">
                                <div class="scroll-hint">
                                    <i class="fas fa-mouse me-1"></i>
                                    Scroll to see more users
                                </div>
                            </c:if>
                        </div>
                        
                        <!-- Scrollable Table Container -->
                        <div class="table-responsive">
                            <div class="table-vertical-scroll">
                                <table class="table table-hover mb-0">
                                    <thead class="table-sticky-header">
                                        <tr>
                                            <th scope="col">#</th>
                                            <th scope="col">Name</th>
                                            <th scope="col">Email</th>
                                            <th scope="col">Contact</th>
                                            <th scope="col">NIC</th>
                                            <th scope="col">Address</th>
                                            <th scope="col">Bank Branch</th>
                                            <th scope="col">User Type</th>
                                            <th scope="col">Status</th>
                                            <th scope="col">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="user" items="${users}">
                                            <tr>
                                                <td>${user.id}</td>
                                                <td>
                                                    <strong>${user.name}</strong>
                                                </td>
                                                <td>
                                                    <i class="fas fa-envelope me-1 text-muted"></i>
                                                    ${user.email}
                                                </td>
                                                <td>
                                                    <i class="fas fa-phone me-1 text-muted"></i>
                                                    ${user.contact}
                                                </td>
                                                <td>
                                                    <code>${user.nic}</code>
                                                </td>
                                                <td>
                                                    <div class="address-cell" title="${user.address}">
                                                        <c:choose>
                                                            <c:when test="${fn:length(user.address) > 30}">
                                                                ${fn:substring(user.address, 0, 30)}...
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${user.address}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </td>
                                                <td>
                                                    <i class="fas fa-university me-1 text-muted"></i>
                                                    ${user.bankBranch.branchName}
                                                </td>
                                                <td>
                                                    <span class="user-type user-type-${user.userType.toString().toLowerCase()}">
                                                        <c:choose>
                                                            <c:when test="${user.userType == 'SUPER_ADMIN'}">
                                                                <i class="fas fa-crown me-1"></i>
                                                            </c:when>
                                                            <c:when test="${user.userType == 'ADMIN'}">
                                                                <i class="fas fa-user-shield me-1"></i>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="fas fa-user me-1"></i>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        ${user.userType}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="status-${user.status.toString().toLowerCase()}">
                                                        <c:choose>
                                                            <c:when test="${user.status == 'ACTIVE'}">
                                                                <i class="fas fa-check-circle me-1"></i>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="fas fa-clock me-1"></i>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        ${user.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <%-- Only show action buttons if allowed --%>
                                                    <% 
                                                    String currentUserEmail = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
                                                    boolean isSuperAdmin = false;
                                                    if (currentUserEmail != null) {
                                                        try {
                                                            javax.naming.InitialContext ic = new javax.naming.InitialContext();
                                                            com.bank.app.core.service.UserService userService = (com.bank.app.core.service.UserService) ic.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
                                                            com.bank.app.core.model.User currentUser = userService.getUserByEmail(currentUserEmail);
                                                            isSuperAdmin = currentUser != null && currentUser.getUserType().name().equals("SUPER_ADMIN");
                                                        } catch (Exception e) { isSuperAdmin = false; }
                                                    }
                                                    %>
                                                    <div class="btn-group btn-group-sm" role="group">
                                                        <% if (isSuperAdmin || ("USER".equals(pageContext.findAttribute("user") != null ? ((com.bank.app.core.model.User)pageContext.findAttribute("user")).getUserType().name() : null))) { %>
                                                            <a href="editUser?id=${user.id}" class="btn btn-outline-warning" title="Edit User">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                            <c:if test="${user.status == 'INACTIVE'}">
                                                                <form method="post" action="activateUser" style="display: inline;">
                                                                    <input type="hidden" name="id" value="${user.id}">
                                                                    <button type="submit" class="btn btn-outline-success" 
                                                                            onclick="return confirm('Activate this user without email verification?')"
                                                                            title="Quick Activate">
                                                                        <i class="fas fa-check"></i>
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                            <c:if test="${user.userType != 'SUPER_ADMIN'}">
                                                                <form method="post" action="deleteUser" style="display: inline;">
                                                                    <input type="hidden" name="id" value="${user.id}">
                                                                    <button type="submit" class="btn btn-outline-danger" 
                                                                            onclick="return confirm('Are you sure you want to delete this user?')"
                                                                            title="Delete User">
                                                                        <i class="fas fa-trash"></i>
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                        <% } else { %>
                                                            <span class="text-muted small">
                                                                <i class="fas fa-eye me-1"></i>View only
                                                            </span>
                                                        <% } %>
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
                
                <!-- Summary Cards -->
                <div class="row mt-4">
                    <div class="col-md-3">
                        <div class="summary-card">
                            <div class="icon text-primary">
                                <i class="fas fa-users"></i>
                            </div>
                            <div class="count text-primary">${fn:length(users)}</div>
                            <div class="label">Total Users</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="summary-card">
                            <div class="icon text-success">
                                <i class="fas fa-check-circle"></i>
                            </div>
                            <div class="count text-success">
                                <c:set var="activeCount" value="0" />
                                <c:forEach var="user" items="${users}">
                                    <c:if test="${user.status == 'ACTIVE'}">
                                        <c:set var="activeCount" value="${activeCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${activeCount}
                            </div>
                            <div class="label">Active Users</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="summary-card">
                            <div class="icon text-warning">
                                <i class="fas fa-clock"></i>
                            </div>
                            <div class="count text-warning">
                                <c:set var="inactiveCount" value="0" />
                                <c:forEach var="user" items="${users}">
                                    <c:if test="${user.status == 'INACTIVE'}">
                                        <c:set var="inactiveCount" value="${inactiveCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${inactiveCount}
                            </div>
                            <div class="label">Pending Activation</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="summary-card">
                            <div class="icon text-info">
                                <i class="fas fa-user-shield"></i>
                            </div>
                            <div class="count text-info">
                                <c:set var="adminCount" value="0" />
                                <c:forEach var="user" items="${users}">
                                    <c:if test="${user.userType == 'ADMIN' || user.userType == 'SUPER_ADMIN'}">
                                        <c:set var="adminCount" value="${adminCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${adminCount}
                            </div>
                            <div class="label">Admins</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    

    <script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>
    <script>
        // Search functionality with enhanced UX
        document.getElementById('searchInput').addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const table = document.getElementById('usersTable');
            const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
            let visibleCount = 0;
            
            for (let i = 0; i < rows.length; i++) {
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
                
                if (found) {
                    row.style.display = '';
                    visibleCount++;
                } else {
                    row.style.display = 'none';
                }
            }
            
            // Update user count in info bar
            const userCountElement = document.querySelector('.user-count strong');
            if (userCountElement) {
                if (searchTerm === '') {
                    userCountElement.textContent = rows.length;
                } else {
                    userCountElement.textContent = visibleCount + ' of ' + rows.length;
                }
            }
            
            // Show/hide scroll hint based on visible results
            const scrollHint = document.querySelector('.scroll-hint');
            if (scrollHint) {
                if (searchTerm === '' && rows.length > 8) {
                    scrollHint.style.display = 'block';
                } else if (visibleCount > 8) {
                    scrollHint.style.display = 'block';
                    scrollHint.innerHTML = '<i class="fas fa-search me-1"></i>Scroll to see more results';
                } else {
                    scrollHint.style.display = 'none';
                }
            }
        });
        
        // Smooth scrolling for table navigation
        document.addEventListener('DOMContentLoaded', function() {
            const tableContainer = document.querySelector('.table-container');
            const searchInput = document.getElementById('searchInput');
            
            // Add keyboard navigation
            searchInput.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' && this.value.trim() !== '') {
                    // Scroll to first visible result
                    const visibleRows = tableContainer.querySelectorAll('tbody tr:not([style*="display: none"])');
                    if (visibleRows.length > 0) {
                        visibleRows[0].scrollIntoView({ 
                            behavior: 'smooth', 
                            block: 'start' 
                        });
                    }
                }
            });
            
            // Add scroll to top button functionality
            const scrollToTopBtn = document.createElement('button');
            scrollToTopBtn.innerHTML = '<i class="fas fa-arrow-up"></i>';
            scrollToTopBtn.className = 'btn btn-outline-primary btn-sm position-fixed';
            scrollToTopBtn.style.cssText = 'bottom: 20px; right: 20px; z-index: 1000; display: none; border-radius: 50%; width: 40px; height: 40px;';
            document.body.appendChild(scrollToTopBtn);
            
            tableContainer.addEventListener('scroll', function() {
                if (this.scrollTop > 200) {
                    scrollToTopBtn.style.display = 'block';
                } else {
                    scrollToTopBtn.style.display = 'none';
                }
            });
            
            scrollToTopBtn.addEventListener('click', function() {
                tableContainer.scrollTo({
                    top: 0,
                    behavior: 'smooth'
                });
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
        
        // Add loading animation for form submissions
        document.addEventListener('DOMContentLoaded', function() {
            const forms = document.querySelectorAll('form');
            forms.forEach(function(form) {
                form.addEventListener('submit', function() {
                    const submitBtn = form.querySelector('button[type="submit"]');
                    if (submitBtn) {
                        const originalText = submitBtn.innerHTML;
                        submitBtn.innerHTML = '<span class="loading-spinner me-2"></span>Processing...';
                        submitBtn.disabled = true;
                        
                        // Re-enable after 3 seconds if no response
                        setTimeout(function() {
                            submitBtn.innerHTML = originalText;
                            submitBtn.disabled = false;
                        }, 3000);
                    }
                });
            });
        });
    </script>
</body>
</html>
