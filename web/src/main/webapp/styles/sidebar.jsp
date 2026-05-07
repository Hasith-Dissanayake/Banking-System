<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Responsive Sidebar using Bootstrap Offcanvas --%>
<!-- Hamburger button for small screens -->
<button class="btn btn-primary d-md-none m-2" type="button" data-bs-toggle="offcanvas" data-bs-target="#sidebarOffcanvas" aria-controls="sidebarOffcanvas">
    <i class="fas fa-bars"></i>
</button>

<!-- Offcanvas Sidebar for mobile/tablet -->
<div class="offcanvas offcanvas-start d-md-none bg-dark text-white" tabindex="-1" id="sidebarOffcanvas" aria-labelledby="sidebarOffcanvasLabel">
    <div class="offcanvas-header">
        <h5 class="offcanvas-title" id="sidebarOffcanvasLabel">
            <i class="fas fa-university me-2"></i>Banking System
        </h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    <div class="offcanvas-body p-0">
        <jsp:include page="/styles/sidebar_content.jsp" />
    </div>
</div>

<!-- Sidebar for desktop -->
<div class="sidebar d-none d-md-flex flex-column p-3 text-white bg-dark min-vh-100">
    <jsp:include page="/styles/sidebar_content.jsp" />
</div>

<%-- Sidebar content extracted for reuse --%>
<!-- Create a new file: sidebar_content.jsp with the original sidebar content (except the outer div) --> 