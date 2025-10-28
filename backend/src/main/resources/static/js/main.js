/**
 * Main JavaScript for ADRS
 * Common functionality across all pages
 */

// CSRF Token configuration for AJAX requests
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

// Configure fetch to include CSRF token
const originalFetch = window.fetch;
window.fetch = function(url, options = {}) {
    if (csrfToken && csrfHeader) {
        options.headers = options.headers || {};
        options.headers[csrfHeader] = csrfToken;
    }
    return originalFetch(url, options);
};

// Auto-hide flash messages
document.addEventListener('DOMContentLoaded', function() {
    const alerts = document.querySelectorAll('.alert-success, .alert-info');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = 'opacity 0.5s ease-out';
            alert.style.opacity = '0';
            setTimeout(function() {
                alert.remove();
            }, 500);
        }, 5000);
    });
});

// Confirm delete operations
document.addEventListener('DOMContentLoaded', function() {
    const deleteForms = document.querySelectorAll('form[action*="/delete/"]');
    deleteForms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            const confirmed = confirm('Are you sure you want to delete this item? This action cannot be undone.');
            if (!confirmed) {
                event.preventDefault();
            }
        });
    });
});
