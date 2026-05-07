<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.bank.app.core.service.BankBranchService" %>
<%@ page import="com.bank.app.core.model.BankBranch" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.naming.NamingException" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bank Branch Management - Banking System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="../styles/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../styles/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
    <%
        // Load bank branches if not already loaded by servlet
        if (request.getAttribute("bankBranches") == null) {
            try {
                InitialContext context = new InitialContext();
                BankBranchService bankBranchService = (BankBranchService) context.lookup("java:global/banking-system-ear/auth-module/BankBranchSessionBean!com.bank.app.core.service.BankBranchService");
                List<BankBranch> bankBranches = bankBranchService.getAllBankBranches();
                request.setAttribute("bankBranches", bankBranches);
            } catch (NamingException e) {
                request.setAttribute("bankBranches", new java.util.ArrayList<>());
                request.setAttribute("bankBranchError", "NamingException: " + e.getMessage());
            } catch (Exception e) {
                request.setAttribute("bankBranches", new java.util.ArrayList<>());
                request.setAttribute("bankBranchError", "Exception: " + e.getMessage());
            }
        }
    %>
    <!-- Debug/Info Block for bankBranches -->
    <c:if test="${not empty bankBranchError}">
        <div class="alert alert-danger">
            <strong>Error loading bank branches:</strong> ${bankBranchError}
        </div>
    </c:if>
    <c:if test="${empty bankBranches}">
        <div class="alert alert-warning">
            <strong>No bank branches found or error loading data.</strong>
        </div>
    </c:if>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2  p-0">
                <%@ include file="/styles/sidebar.jsp" %>
            </div>
            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content p-4">
                <!-- Success Messages -->
                <c:if test="${param.success != null}">
                    <div class="alert alert-banking alert-banking-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>
                        <c:choose>
                            <c:when test="${param.success == 'branch_added'}">
                                Bank branch added successfully!
                            </c:when>
                            <c:when test="${param.success == 'branch_updated'}">
                                Bank branch updated successfully!
                            </c:when>
                            <c:when test="${param.success == 'branch_deleted'}">
                                Bank branch deleted successfully!
                            </c:when>
                            <c:when test="${param.success == 'branch_activated'}">
                                Bank branch activated successfully!
                            </c:when>
                            <c:when test="${param.success == 'branch_deactivated'}">
                                Bank branch deactivated successfully!
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
                            <c:when test="${param.error == 'name_required'}">
                                Branch name is required.
                            </c:when>
                            <c:when test="${param.error == 'branch_exists'}">
                                A branch with this name already exists.
                            </c:when>
                            <c:when test="${param.error == 'invalid_data'}">
                                Invalid data provided.
                            </c:when>
                            <c:when test="${param.error == 'branch_not_found'}">
                                Bank branch not found.
                            </c:when>
                            <c:when test="${param.error == 'invalid_id'}">
                                Invalid branch ID.
                            </c:when>
                            <c:when test="${param.error == 'add_failed'}">
                                Failed to add bank branch.
                            </c:when>
                            <c:when test="${param.error == 'update_failed'}">
                                Failed to update bank branch.
                            </c:when>
                            <c:when test="${param.error == 'delete_failed'}">
                                Failed to delete bank branch.
                            </c:when>
                            <c:when test="${param.error == 'load_failed'}">
                                Failed to load bank branches.
                            </c:when>
                        </c:choose>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <!-- Add New Branch Form -->
                <div class="card card-banking mb-4">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-plus me-2"></i>Add New Bank Branch
                        </h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="bankBranches" class="row g-3">
                            <input type="hidden" name="action" value="add">
                            <div class="col-md-8">
                                <label for="branchName" class="form-label">Branch Name</label>
                                <input type="text" class="form-control form-control-banking" 
                                       id="branchName" name="branchName" required 
                                       placeholder="e.g., Colombo Main Branch">
                            </div>
                            <div class="col-md-4 d-flex align-items-end">
                                <button type="submit" class="btn btn-banking-primary">
                                    <i class="fas fa-plus me-2"></i>Add Branch
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                
                <!-- Bank Branches Table -->
                <div class="card card-banking">
                    <div class="card-header bg-white">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-list me-2"></i>Bank Branches
                        </h5>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <div class="table-vertical-scroll">
                                <table class="table table-hover mb-0">
                                    <thead class="table-sticky-header">
                                        <tr>
                                            <th scope="col">#</th>
                                            <th scope="col">Branch Name</th>
                                            <th scope="col">Status</th>
                                            <th scope="col">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="branch" items="${bankBranches}">
                                            <tr>
                                                <td>${branch.id}</td>
                                                <td>
                                                    <strong>${branch.branchName}</strong>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${branch.isActive}">
                                                            <span class="badge bg-success">
                                                                <i class="fas fa-check-circle me-1"></i>Active
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary">
                                                                <i class="fas fa-times-circle me-1"></i>Inactive
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="btn-group btn-group-sm" role="group">
                                                        <button type="button" class="btn btn-outline-warning" 
                                                                data-branch-id="${branch.id}"
                                                                data-branch-name="${fn:replace(branch.branchName, "'", "\\'")}"
                                                                onclick="editBranchFromButton(this)"
                                                                title="Edit Branch">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <c:choose>
                                                            <c:when test="${branch.isActive}">
                                                                <form method="post" action="bankBranches" style="display: inline;">
                                                                    <input type="hidden" name="action" value="deactivate">
                                                                    <input type="hidden" name="id" value="${branch.id}">
                                                                    <button type="submit" class="btn btn-outline-warning" 
                                                                            onclick="return confirm('Deactivate this branch?')"
                                                                            title="Deactivate Branch">
                                                                        <i class="fas fa-pause"></i>
                                                                    </button>
                                                                </form>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form method="post" action="bankBranches" style="display: inline;">
                                                                    <input type="hidden" name="action" value="activate">
                                                                    <input type="hidden" name="id" value="${branch.id}">
                                                                    <button type="submit" class="btn btn-outline-success" 
                                                                            title="Activate Branch">
                                                                        <i class="fas fa-play"></i>
                                                                    </button>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <form method="post" action="bankBranches" style="display: inline;">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="id" value="${branch.id}">
                                                            <button type="submit" class="btn btn-outline-danger" 
                                                                    onclick="return confirm('Are you sure you want to delete this branch? This action cannot be undone.')"
                                                                    title="Delete Branch">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </form>
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
            </div>
        </div>
    </div>
    <!-- Edit Branch Modal and Scripts  -->
    <div class="modal fade" id="editBranchModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-edit me-2"></i>Edit Bank Branch
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form method="post" action="bankBranches">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" id="editBranchId">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="editBranchName" class="form-label">Branch Name</label>
                            <input type="text" class="form-control form-control-banking" 
                                   id="editBranchName" name="branchName" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-banking-primary">
                            <i class="fas fa-save me-2"></i>Update Branch
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="../styles/bootstrap/js/bootstrap.bundle.min.js"></script>
    <script>
        function editBranchFromButton(btn) {
            var id = btn.getAttribute('data-branch-id');
            var name = btn.getAttribute('data-branch-name');
            document.getElementById('editBranchId').value = id;
            document.getElementById('editBranchName').value = name;
            new bootstrap.Modal(document.getElementById('editBranchModal')).show();
        }
        
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