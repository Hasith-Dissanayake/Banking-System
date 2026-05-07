<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.BankBranchService" %>
<%@ page import="com.bank.app.core.model.BankBranch" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit User - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 p-0">
                <%@ include file="/styles/sidebar.jsp" %>
            </div>
            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content p-4">
                <div class="card card-banking">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-user-edit me-2"></i>Edit User
                        </h5>
                    </div>
                    <div class="card-body">
                        <!-- Error Messages -->
                        <c:if test="${param.error != null}">
                            <div class="alert alert-banking alert-banking-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                <c:choose>
                                    <c:when test="${param.error == 'required_fields_missing'}">
                                        All required fields must be filled.
                                    </c:when>
                                    <c:when test="${param.error == 'password_too_short'}">
                                        Password must be at least 6 characters long.
                                    </c:when>
                                    <c:when test="${param.error == 'email_exists'}">
                                        Email address already exists.
                                    </c:when>
                                    <c:when test="${param.error == 'unauthorized_edit_admin'}">
                                        You are not authorized to edit admin users.
                                    </c:when>
                                    <c:when test="${param.error == 'unauthorized_edit_super_admin'}">
                                        Only super admins can edit super admin accounts.
                                    </c:when>
                                    <c:when test="${param.error == 'update_failed'}">
                                        Failed to update user. Please try again.
                                    </c:when>
                                </c:choose>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        
                        <form method="post" action="editUser" class="needs-validation" novalidate>
                            <input type="hidden" name="userId" value="${user.id}">
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="name" class="form-label">Full Name</label>
                                    <input type="text" class="form-control form-control-banking" 
                                           id="name" name="name" value="${user.name}" required>
                                    <div class="invalid-feedback">
                                        Please provide the full name.
                                    </div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label">Email Address</label>
                                    <input type="email" class="form-control form-control-banking" 
                                           id="email" name="email" value="${user.email}" required>
                                    <div class="invalid-feedback">
                                        Please provide a valid email address.
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="contact" class="form-label">Contact Number</label>
                                    <input type="tel" class="form-control form-control-banking" 
                                           id="contact" name="contact" value="${user.contact}" required>
                                    <div class="invalid-feedback">
                                        Please provide the contact number.
                                    </div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="nic" class="form-label">NIC Number</label>
                                    <input type="text" class="form-control form-control-banking" 
                                           id="nic" name="nic" value="${user.nic}" required>
                                    <div class="invalid-feedback">
                                        Please provide the NIC number.
                                    </div>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="address" class="form-label">Address</label>
                                <textarea class="form-control form-control-banking" 
                                          id="address" name="address" rows="3" required>${user.address}</textarea>
                                <div class="invalid-feedback">
                                    Please provide the address.
                                </div>
                            </div>
                            
                            <div class="mb-3">
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
                                                    String selected = "";
                                                    if (request.getAttribute("user") != null) {
                                                        com.bank.app.core.model.User user = (com.bank.app.core.model.User) request.getAttribute("user");
                                                        if (user.getBankBranch() != null && user.getBankBranch().getId().equals(branch.getId())) {
                                                            selected = "selected";
                                                        }
                                                    }
                                    %>
                                                <option value="<%= branch.getId() %>" <%= selected %>><%= branch.getBranchName() %></option>
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
                            
                            <div class="mb-3">
                                <label for="userType" class="form-label">User Type</label>
                                <c:choose>
                                    <c:when test="${user.userType.name() == 'SUPER_ADMIN'}">
                                        <input type="text" class="form-control form-control-banking" 
                                               value="Super Admin" readonly>
                                        <input type="hidden" name="userType" value="SUPER_ADMIN">
                                        <small class="form-text text-muted">
                                            <i class="fas fa-info-circle me-1"></i>Super Admin user type cannot be changed.
                                        </small>
                                    </c:when>
                                    <c:otherwise>
                                        <select class="form-select form-control-banking" 
                                                id="userType" name="userType" required>
                                            <option value="USER" ${user.userType.name() == 'USER' ? 'selected' : ''}>User</option>
                                            <%
                                                // Only SUPER_ADMIN can change to ADMIN
                                                String currentUserRole = request.getUserPrincipal() != null ? 
                                                    request.getUserPrincipal().getName() : null;
                                                if (currentUserRole != null) {
                                                    try {
                                                        InitialContext context = new InitialContext();
                                                        com.bank.app.core.service.UserService userService = 
                                                            (com.bank.app.core.service.UserService) context.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
                                                        com.bank.app.core.model.User currentUser = userService.getUserByEmail(currentUserRole);
                                                        if (currentUser != null && currentUser.getUserType().name().equals("SUPER_ADMIN")) {
                                            %>
                                                            <option value="ADMIN" ${user.userType.name() == 'ADMIN' ? 'selected' : ''}>Admin</option>
                                            <%
                                                        }
                                                    } catch (Exception e) {
                                                        // Handle error silently
                                                    }
                                                }
                                            %>
                                        </select>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-banking-primary">
                                    <i class="fas fa-save me-2"></i>Update User
                                </button>
                                <a href="users.jsp" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>Back to Users
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
