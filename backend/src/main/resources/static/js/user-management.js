/**
 * User Management JavaScript
 * Handles modal operations and form interactions
 */

// Open Create Modal
function openCreateModal() {
    const modal = document.getElementById('createModal');
    modal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

// Close Create Modal
function closeCreateModal(event) {
    if (event && event.target !== event.currentTarget) {
        return;
    }
    const modal = document.getElementById('createModal');
    modal.classList.add('hidden');
    document.body.style.overflow = 'auto';
    
    // Reset form
    const form = modal.querySelector('form');
    if (form) {
        form.reset();
    }
}

// Open Edit Modal
function openEditModal(id, username, email, firstName, lastName, phoneNumber, role) {
    const modal = document.getElementById('editModal');
    const form = document.getElementById('editForm');
    
    // Set form action
    form.action = `/users/update/${id}`;
    
    // Populate form fields
    document.getElementById('edit-username').value = username || '';
    document.getElementById('edit-email').value = email || '';
    document.getElementById('edit-firstName').value = firstName || '';
    document.getElementById('edit-lastName').value = lastName || '';
    document.getElementById('edit-phoneNumber').value = phoneNumber || '';
    document.getElementById('edit-role').value = role || '';
    document.getElementById('edit-password').value = '';
    
    modal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

// Close Edit Modal
function closeEditModal(event) {
    if (event && event.target !== event.currentTarget) {
        return;
    }
    const modal = document.getElementById('editModal');
    modal.classList.add('hidden');
    document.body.style.overflow = 'auto';
    
    // Reset form
    const form = document.getElementById('editForm');
    if (form) {
        form.reset();
    }
}

// Close modals on ESC key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeCreateModal();
        closeEditModal();
    }
});

// Auto-hide flash messages after 5 seconds
document.addEventListener('DOMContentLoaded', function() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = 'opacity 0.5s ease-out';
            alert.style.opacity = '0';
            setTimeout(function() {
                alert.remove();
            }, 500);
        }, 5000);
    });
    
    // Add event listeners for edit buttons
    const editButtons = document.querySelectorAll('.btn-edit-user');
    editButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-user-id');
            const username = this.getAttribute('data-username');
            const email = this.getAttribute('data-email');
            const firstName = this.getAttribute('data-first-name');
            const lastName = this.getAttribute('data-last-name');
            const phoneNumber = this.getAttribute('data-phone');
            const role = this.getAttribute('data-role');
            
            openEditModal(id, username, email, firstName, lastName, phoneNumber, role);
        });
    });
    
    // Add event listeners for delete forms
    const deleteForms = document.querySelectorAll('.delete-user-form');
    deleteForms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            const username = this.getAttribute('data-username');
            const message = 'Are you sure you want to delete ' + username + '? This action cannot be undone.';
            
            if (!confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
