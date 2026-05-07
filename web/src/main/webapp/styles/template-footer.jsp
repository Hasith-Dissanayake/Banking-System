    <!-- Local Bootstrap JS -->
    <script src="${pageContext.request.contextPath}/styles/bootstrap/js/bootstrap.bundle.min.js"></script>
    
    <!-- Common Banking System Scripts -->
    <script>
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
        
        // Common utility functions
        window.BankingUtils = {
            // Show success message
            showSuccess: function(message) {
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-banking alert-banking-success alert-dismissible fade show';
                alertDiv.innerHTML = `
                    <i class="fas fa-check-circle me-2"></i>${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                document.body.insertBefore(alertDiv, document.body.firstChild);
            },
            
            // Show error message
            showError: function(message) {
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-banking alert-banking-danger alert-dismissible fade show';
                alertDiv.innerHTML = `
                    <i class="fas fa-exclamation-triangle me-2"></i>${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                document.body.insertBefore(alertDiv, document.body.firstChild);
            },
            
            // Confirm action
            confirmAction: function(message, callback) {
                if (confirm(message)) {
                    callback();
                }
            }
        };
    </script>
    
    <!-- Additional page-specific scripts -->
    ${param.additionalScripts}
</body>
</html> 