/**
 * User Management JavaScript
 * Handles modal operations and form interactions including cascading province-district dropdowns
 */

// Get CSRF token from meta tag
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

/**
 * Refresh CustomSelect after options are dynamically loaded
 * Uses the global CustomSelect registry
 */
function refreshCustomSelect(selectId) {
    if (typeof CustomSelect !== 'undefined' && CustomSelect.refresh) {
        CustomSelect.refresh(selectId);
    }
}

/**
 * Loads all provinces into a dropdown
 */
async function loadProvinces(selectId) {
    try {
        const response = await fetch('/api/locations/provinces', {
            headers: {
                [csrfHeader]: csrfToken
            }
        });
        
        if (!response.ok) {
            throw new Error('Failed to load provinces');
        }
        
        const provinces = await response.json();
        const select = document.getElementById(selectId);
        
        // Clear existing options except the first one
        select.innerHTML = '<option value="">Select Province</option>';
        
        provinces.forEach(province => {
            const option = document.createElement('option');
            option.value = province.value;
            option.textContent = province.label;
            select.appendChild(option);
        });
        
        // Refresh CustomSelect if initialized
        refreshCustomSelect(selectId);
    } catch (error) {
        console.error('Error loading provinces:', error);
    }
}

/**
 * Loads districts for a specific province
 */
async function loadDistricts(provinceName, selectId) {
    const select = document.getElementById(selectId);
    
    if (!provinceName) {
        select.innerHTML = '<option value="">Select District</option>';
        select.disabled = true;
        refreshCustomSelect(selectId);
        return;
    }
    
    try {
        const response = await fetch(`/api/locations/districts?provinceName=${provinceName}`, {
            headers: {
                [csrfHeader]: csrfToken
            }
        });
        
        if (!response.ok) {
            throw new Error('Failed to load districts');
        }
        
        const districts = await response.json();
        
        // Clear existing options
        select.innerHTML = '<option value="">Select District</option>';
        
        districts.forEach(district => {
            const option = document.createElement('option');
            option.value = district.value;
            option.textContent = district.label;
            select.appendChild(option);
        });
        
        select.disabled = false;
        
        // Refresh CustomSelect if initialized
        refreshCustomSelect(selectId);
    } catch (error) {
        console.error('Error loading districts:', error);
        select.innerHTML = '<option value="">Error loading districts</option>';
        select.disabled = true;
        refreshCustomSelect(selectId);
    }
}

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
    
    // Reset district dropdown
    const districtSelect = document.getElementById('create-district');
    if (districtSelect) {
        districtSelect.innerHTML = '<option value="">Select District</option>';
        districtSelect.disabled = true;
    }
}

// Open Edit Modal
function openEditModal(id, username, email, firstName, lastName, phoneNumber, province, district, role) {
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
    
    // Set province and load corresponding districts
    if (province) {
        document.getElementById('edit-province').value = province;
        loadDistricts(province, 'edit-district').then(() => {
            if (district) {
                document.getElementById('edit-district').value = district;
            }
        });
    } else {
        document.getElementById('edit-province').value = '';
        document.getElementById('edit-district').value = '';
        document.getElementById('edit-district').disabled = true;
    }
    
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
    
    // Reset district dropdown
    const districtSelect = document.getElementById('edit-district');
    if (districtSelect) {
        districtSelect.innerHTML = '<option value="">Select District</option>';
        districtSelect.disabled = true;
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
    
    // Load provinces for both forms (CustomSelects are auto-initialized by custom-select.js)
    loadProvinces('create-province');
    loadProvinces('edit-province');
    
    // Add province change listeners for cascading dropdowns
    const createProvinceSelect = document.getElementById('create-province');
    if (createProvinceSelect) {
        createProvinceSelect.addEventListener('change', function() {
            loadDistricts(this.value, 'create-district');
        });
    }
    
    const editProvinceSelect = document.getElementById('edit-province');
    if (editProvinceSelect) {
        editProvinceSelect.addEventListener('change', function() {
            loadDistricts(this.value, 'edit-district');
        });
    }
    
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
            const province = this.getAttribute('data-province');
            const district = this.getAttribute('data-district');
            const role = this.getAttribute('data-role');
            
            openEditModal(id, username, email, firstName, lastName, phoneNumber, province, district, role);
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
