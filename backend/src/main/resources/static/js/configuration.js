/**
 * Configuration Management JavaScript
 * Handles CRUD operations for farm types, animal types, and diseases
 */

// CSRF Token
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

// API Base URLs
const API_BASE = '/api/configuration';
const FARM_TYPES_API = `${API_BASE}/farm-types`;
const ANIMAL_TYPES_API = `${API_BASE}/animal-types`;
const DISEASES_API = `${API_BASE}/diseases`;
const CONFIG_TABS = ['farm-types', 'animal-types', 'diseases'];

// Current state
let currentTab = 'farm-types';
let currentEditId = null;
let currentDeleteId = null;
let currentDeleteType = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initializeTabs();
    initializeModals();
    const initialTab = getInitialTab();
    switchTab(initialTab);

    // Set up form submissions
    document.getElementById('farm-type-form').addEventListener('submit', handleFarmTypeSubmit);
    document.getElementById('animal-type-form').addEventListener('submit', handleAnimalTypeSubmit);
    document.getElementById('disease-form').addEventListener('submit', handleDiseaseSubmit);
    
    // Set up add buttons
    document.getElementById('add-farm-type-btn').addEventListener('click', () => openFarmTypeModal());
    document.getElementById('add-animal-type-btn').addEventListener('click', () => openAnimalTypeModal());
    document.getElementById('add-disease-btn').addEventListener('click', () => openDiseaseModal());
    
    // Set up delete confirmation
    document.getElementById('confirm-delete-btn').addEventListener('click', handleDelete);
});

// ========================================
// TAB MANAGEMENT
// ========================================

function getInitialTab() {
    const params = new URLSearchParams(window.location.search);
    const requestedTab = params.get('tab');
    return getValidTabName(requestedTab);
}

function getValidTabName(tabName) {
    if (!tabName) {
        return CONFIG_TABS[0];
    }
    return CONFIG_TABS.includes(tabName) ? tabName : CONFIG_TABS[0];
}

function initializeTabs() {
    const tabButtons = document.querySelectorAll('.tab-button');
    for (const button of tabButtons) {
        button.addEventListener('click', () => {
            const tabName = button.dataset.tab;
            switchTab(tabName);
        });
    }
}

function switchTab(tabName) {
    const validTab = getValidTabName(tabName);
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');

    for (const btn of tabButtons) {
        btn.classList.remove('active');
    }
    const activeButton = document.querySelector(`[data-tab="${validTab}"]`);
    if (activeButton) {
        activeButton.classList.add('active');
    }
    
    for (const content of tabContents) {
        content.classList.remove('active');
    }
    const targetContent = document.getElementById(`${validTab}-tab`);
    if (targetContent) {
        targetContent.classList.add('active');
    }
    
    // Load data for the tab
    currentTab = validTab;
    switch (validTab) {
        case 'farm-types':
            loadFarmTypes();
            break;
        case 'animal-types':
            loadAnimalTypes();
            break;
        case 'diseases':
            loadDiseases();
            break;
    }
}

// ========================================
// MODAL MANAGEMENT
// ========================================

function initializeModals() {
    // Close buttons
    document.querySelectorAll('.modal-close, .btn-secondary').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const modalId = btn.getAttribute('data-modal');
            if (modalId) {
                closeModal(modalId);
            }
        });
    });
    
    // Click outside to close
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeModal(modal.id);
            }
        });
    });
}

function openModal(modalId) {
    document.getElementById(modalId).classList.add('show');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('show');
}

// ========================================
// FARM TYPE OPERATIONS
// ========================================

async function loadFarmTypes() {
    try {
        const response = await fetch(FARM_TYPES_API);
        if (!response.ok) throw new Error('Failed to load farm types');
        
        const farmTypes = await response.json();
        renderFarmTypes(farmTypes);
    } catch (error) {
        console.error('Error loading farm types:', error);
        showError('Failed to load farm types');
    }
}

function renderFarmTypes(farmTypes) {
    const tbody = document.querySelector('#farm-types-table tbody');
    
    if (farmTypes.length === 0) {
        tbody.innerHTML = `
            <tr class="empty-state">
                <td colspan="7">
                    <div class="icon">🏡</div>
                    <p>No farm types found. Add one to get started!</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = farmTypes.map(ft => `
        <tr>
            <td data-label="Type Name"><strong>${escapeHtml(ft.typeName)}</strong></td>
            <td data-label="Description" class="text-truncate">${escapeHtml(ft.description || '-')}</td>
            <td data-label="Status"><span class="status-badge ${ft.isActive ? 'active' : 'inactive'}">${ft.isActive ? 'Active' : 'Inactive'}</span></td>
            <td data-label="Created By">${escapeHtml(ft.createdByUsername || 'System')}</td>
            <td data-label="Created Date">${formatDate(ft.createdAt)}</td>
            <td data-label="Usage"><strong>${ft.usageCount || 0}</strong> farms</td>
            <td data-label="Actions">
                <div class="action-buttons">
                    <button class="btn-icon edit" onclick="editFarmType('${ft.id}')" title="Edit">✏️</button>
                    <button class="btn-icon toggle" onclick="toggleFarmType('${ft.id}', ${!ft.isActive})" title="${ft.isActive ? 'Deactivate' : 'Activate'}">${ft.isActive ? '🔒' : '🔓'}</button>
                    <button class="btn-icon delete" onclick="deleteFarmType('${ft.id}')" title="Delete">🗑️</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function openFarmTypeModal(farmType = null) {
    currentEditId = farmType ? farmType.id : null;
    const form = document.getElementById('farm-type-form');
    const title = document.getElementById('farm-type-modal-title');
    
    if (farmType) {
        title.textContent = 'Edit Farm Type';
        document.getElementById('farm-type-id').value = farmType.id;
        document.getElementById('farm-type-name').value = farmType.typeName;
        document.getElementById('farm-type-description').value = farmType.description || '';
        document.getElementById('farm-type-active').checked = farmType.isActive;
    } else {
        title.textContent = 'Add Farm Type';
        form.reset();
    }
    
    // Clear errors
    form.querySelectorAll('.error-message').forEach(el => el.textContent = '');
    form.querySelectorAll('.form-control').forEach(el => el.classList.remove('error'));
    
    openModal('farm-type-modal');
}

async function handleFarmTypeSubmit(e) {
    e.preventDefault();
    
    const formData = {
        typeName: document.getElementById('farm-type-name').value.trim(),
        description: document.getElementById('farm-type-description').value.trim(),
        isActive: document.getElementById('farm-type-active').checked
    };
    
    try {
        const url = currentEditId ? `${FARM_TYPES_API}/${currentEditId}` : FARM_TYPES_API;
        const method = currentEditId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(formData)
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to save farm type');
        }
        
        showSuccess(`Farm type ${currentEditId ? 'updated' : 'created'} successfully`);
        closeModal('farm-type-modal');
        loadFarmTypes();
    } catch (error) {
        console.error('Error saving farm type:', error);
        showError(error.message);
    }
}

async function editFarmType(id) {
    try {
        const response = await fetch(`${FARM_TYPES_API}/${id}`);
        if (!response.ok) throw new Error('Failed to load farm type');
        
        const farmType = await response.json();
        openFarmTypeModal(farmType);
    } catch (error) {
        console.error('Error loading farm type:', error);
        showError('Failed to load farm type');
    }
}

async function toggleFarmType(id, isActive) {
    try {
        const response = await fetch(`${FARM_TYPES_API}/${id}/status`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ isActive })
        });
        
        if (!response.ok) throw new Error('Failed to toggle status');
        
        showSuccess(`Farm type ${isActive ? 'activated' : 'deactivated'} successfully`);
        loadFarmTypes();
    } catch (error) {
        console.error('Error toggling farm type:', error);
        showError(error.message);
    }
}

function deleteFarmType(id) {
    currentDeleteId = id;
    currentDeleteType = 'farm-type';
    
    // Check usage before showing delete modal
    fetch(`${FARM_TYPES_API}/${id}/usage`)
        .then(response => response.json())
        .then(data => {
            const usageCount = data.usageCount || 0;
            const message = document.getElementById('delete-message');
            const warning = document.getElementById('delete-warning');
            const deleteBtn = document.getElementById('confirm-delete-btn');
            
            if (usageCount > 0) {
                message.textContent = `This farm type is currently used by ${usageCount} farm(s).`;
                warning.style.display = 'block';
                deleteBtn.disabled = true;
            } else {
                message.textContent = 'Are you sure you want to delete this farm type?';
                warning.style.display = 'none';
                deleteBtn.disabled = false;
            }
            
            openModal('delete-modal');
        })
        .catch(error => {
            console.error('Error checking usage:', error);
            showError('Failed to check usage');
        });
}

// ========================================
// ANIMAL TYPE OPERATIONS
// ========================================

async function loadAnimalTypes() {
    try {
        const response = await fetch(ANIMAL_TYPES_API);
        if (!response.ok) throw new Error('Failed to load animal types');
        
        const animalTypes = await response.json();
        renderAnimalTypes(animalTypes);
    } catch (error) {
        console.error('Error loading animal types:', error);
        showError('Failed to load animal types');
    }
}

function renderAnimalTypes(animalTypes) {
    const tbody = document.querySelector('#animal-types-table tbody');
    
    if (animalTypes.length === 0) {
        tbody.innerHTML = `
            <tr class="empty-state">
                <td colspan="8">
                    <div class="icon">🐄</div>
                    <p>No animal types found. Add one to get started!</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = animalTypes.map(at => `
        <tr>
            <td data-label="Type Name"><strong>${escapeHtml(at.typeName)}</strong></td>
            <td data-label="Species"><em>${escapeHtml(at.species)}</em></td>
            <td data-label="Description" class="text-truncate">${escapeHtml(at.description || '-')}</td>
            <td data-label="Status"><span class="status-badge ${at.isActive ? 'active' : 'inactive'}">${at.isActive ? 'Active' : 'Inactive'}</span></td>
            <td data-label="Created By">${escapeHtml(at.createdByUsername || 'System')}</td>
            <td data-label="Created Date">${formatDate(at.createdAt)}</td>
            <td data-label="Usage"><strong>${at.usageCount || 0}</strong> animals</td>
            <td data-label="Actions">
                <div class="action-buttons">
                    <button class="btn-icon edit" onclick="editAnimalType('${at.id}')" title="Edit">✏️</button>
                    <button class="btn-icon toggle" onclick="toggleAnimalType('${at.id}', ${!at.isActive})" title="${at.isActive ? 'Deactivate' : 'Activate'}">${at.isActive ? '🔒' : '🔓'}</button>
                    <button class="btn-icon delete" onclick="deleteAnimalType('${at.id}')" title="Delete">🗑️</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function openAnimalTypeModal(animalType = null) {
    currentEditId = animalType ? animalType.id : null;
    const form = document.getElementById('animal-type-form');
    const title = document.getElementById('animal-type-modal-title');
    
    if (animalType) {
        title.textContent = 'Edit Animal Type';
        document.getElementById('animal-type-id').value = animalType.id;
        document.getElementById('animal-type-name').value = animalType.typeName;
        document.getElementById('animal-type-species').value = animalType.species;
        document.getElementById('animal-type-description').value = animalType.description || '';
        document.getElementById('animal-type-active').checked = animalType.isActive;
    } else {
        title.textContent = 'Add Animal Type';
        form.reset();
    }
    
    // Clear errors
    form.querySelectorAll('.error-message').forEach(el => el.textContent = '');
    form.querySelectorAll('.form-control').forEach(el => el.classList.remove('error'));
    
    openModal('animal-type-modal');
}

async function handleAnimalTypeSubmit(e) {
    e.preventDefault();
    
    const formData = {
        typeName: document.getElementById('animal-type-name').value.trim(),
        species: document.getElementById('animal-type-species').value.trim(),
        description: document.getElementById('animal-type-description').value.trim(),
        isActive: document.getElementById('animal-type-active').checked
    };
    
    try {
        const url = currentEditId ? `${ANIMAL_TYPES_API}/${currentEditId}` : ANIMAL_TYPES_API;
        const method = currentEditId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(formData)
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to save animal type');
        }
        
        showSuccess(`Animal type ${currentEditId ? 'updated' : 'created'} successfully`);
        closeModal('animal-type-modal');
        loadAnimalTypes();
    } catch (error) {
        console.error('Error saving animal type:', error);
        showError(error.message);
    }
}

async function editAnimalType(id) {
    try {
        const response = await fetch(`${ANIMAL_TYPES_API}/${id}`);
        if (!response.ok) throw new Error('Failed to load animal type');
        
        const animalType = await response.json();
        openAnimalTypeModal(animalType);
    } catch (error) {
        console.error('Error loading animal type:', error);
        showError('Failed to load animal type');
    }
}

async function toggleAnimalType(id, isActive) {
    try {
        const response = await fetch(`${ANIMAL_TYPES_API}/${id}/status`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ isActive })
        });
        
        if (!response.ok) throw new Error('Failed to toggle status');
        
        showSuccess(`Animal type ${isActive ? 'activated' : 'deactivated'} successfully`);
        loadAnimalTypes();
    } catch (error) {
        console.error('Error toggling animal type:', error);
        showError(error.message);
    }
}

function deleteAnimalType(id) {
    currentDeleteId = id;
    currentDeleteType = 'animal-type';
    
    fetch(`${ANIMAL_TYPES_API}/${id}/usage`)
        .then(response => response.json())
        .then(data => {
            const usageCount = data.usageCount || 0;
            const message = document.getElementById('delete-message');
            const warning = document.getElementById('delete-warning');
            const deleteBtn = document.getElementById('confirm-delete-btn');
            
            if (usageCount > 0) {
                message.textContent = `This animal type is currently used by ${usageCount} animal(s).`;
                warning.style.display = 'block';
                deleteBtn.disabled = true;
            } else {
                message.textContent = 'Are you sure you want to delete this animal type?';
                warning.style.display = 'none';
                deleteBtn.disabled = false;
            }
            
            openModal('delete-modal');
        })
        .catch(error => {
            console.error('Error checking usage:', error);
            showError('Failed to check usage');
        });
}

// ========================================
// DISEASE OPERATIONS
// ========================================

async function loadDiseases() {
    try {
        const response = await fetch(DISEASES_API);
        if (!response.ok) throw new Error('Failed to load diseases');
        
        const diseases = await response.json();
        renderDiseases(diseases);
    } catch (error) {
        console.error('Error loading diseases:', error);
        showError('Failed to load diseases');
    }
}

function renderDiseases(diseases) {
    const tbody = document.querySelector('#diseases-table tbody');
    
    if (diseases.length === 0) {
        tbody.innerHTML = `
            <tr class="empty-state">
                <td colspan="9">
                    <div class="icon">🦠</div>
                    <p>No diseases found. Add one to get started!</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = diseases.map(d => `
        <tr>
            <td data-label="Disease Name"><strong>${escapeHtml(d.diseaseName)}</strong></td>
            <td data-label="Code">${escapeHtml(d.diseaseCode || '-')}</td>
            <td data-label="Severity"><span class="severity-badge ${d.severity.toLowerCase()}">${d.severity}</span></td>
            <td data-label="Notifiable">${d.isNotifiable ? '<span class="notifiable-badge">Notifiable</span>' : '-'}</td>
            <td data-label="Status"><span class="status-badge ${d.isActive ? 'active' : 'inactive'}">${d.isActive ? 'Active' : 'Inactive'}</span></td>
            <td data-label="Created By">${escapeHtml(d.createdByUsername || 'System')}</td>
            <td data-label="Created Date">${formatDate(d.createdAt)}</td>
            <td data-label="Usage"><strong>${d.usageCount || 0}</strong> reports</td>
            <td data-label="Actions">
                <div class="action-buttons">
                    <button class="btn-icon edit" onclick="editDisease('${d.id}')" title="Edit">✏️</button>
                    <button class="btn-icon toggle" onclick="toggleDisease('${d.id}', ${!d.isActive})" title="${d.isActive ? 'Deactivate' : 'Activate'}">${d.isActive ? '🔒' : '🔓'}</button>
                    <button class="btn-icon delete" onclick="deleteDisease('${d.id}')" title="Delete">🗑️</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function openDiseaseModal(disease = null) {
    currentEditId = disease ? disease.id : null;
    const form = document.getElementById('disease-form');
    const title = document.getElementById('disease-modal-title');
    
    if (disease) {
        title.textContent = 'Edit Disease';
        document.getElementById('disease-id').value = disease.id;
        document.getElementById('disease-name').value = disease.diseaseName;
        document.getElementById('disease-code').value = disease.diseaseCode || '';
        document.getElementById('disease-severity').value = disease.severity;
        document.getElementById('disease-notifiable').checked = disease.isNotifiable;
        document.getElementById('disease-description').value = disease.description || '';
        document.getElementById('disease-symptoms').value = disease.symptoms || '';
        document.getElementById('disease-treatment').value = disease.treatment || '';
        document.getElementById('disease-active').checked = disease.isActive;
    } else {
        title.textContent = 'Add Disease';
        form.reset();
    }
    
    // Clear errors
    form.querySelectorAll('.error-message').forEach(el => el.textContent = '');
    form.querySelectorAll('.form-control').forEach(el => el.classList.remove('error'));
    
    openModal('disease-modal');
}

async function handleDiseaseSubmit(e) {
    e.preventDefault();
    
    const formData = {
        diseaseName: document.getElementById('disease-name').value.trim(),
        diseaseCode: document.getElementById('disease-code').value.trim(),
        severity: document.getElementById('disease-severity').value,
        isNotifiable: document.getElementById('disease-notifiable').checked,
        description: document.getElementById('disease-description').value.trim(),
        symptoms: document.getElementById('disease-symptoms').value.trim(),
        treatment: document.getElementById('disease-treatment').value.trim(),
        isActive: document.getElementById('disease-active').checked
    };
    
    try {
        const url = currentEditId ? `${DISEASES_API}/${currentEditId}` : DISEASES_API;
        const method = currentEditId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(formData)
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to save disease');
        }
        
        showSuccess(`Disease ${currentEditId ? 'updated' : 'created'} successfully`);
        closeModal('disease-modal');
        loadDiseases();
    } catch (error) {
        console.error('Error saving disease:', error);
        showError(error.message);
    }
}

async function editDisease(id) {
    try {
        const response = await fetch(`${DISEASES_API}/${id}`);
        if (!response.ok) throw new Error('Failed to load disease');
        
        const disease = await response.json();
        openDiseaseModal(disease);
    } catch (error) {
        console.error('Error loading disease:', error);
        showError('Failed to load disease');
    }
}

async function toggleDisease(id, isActive) {
    try {
        const response = await fetch(`${DISEASES_API}/${id}/status`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ isActive })
        });
        
        if (!response.ok) throw new Error('Failed to toggle status');
        
        showSuccess(`Disease ${isActive ? 'activated' : 'deactivated'} successfully`);
        loadDiseases();
    } catch (error) {
        console.error('Error toggling disease:', error);
        showError(error.message);
    }
}

function deleteDisease(id) {
    currentDeleteId = id;
    currentDeleteType = 'disease';
    
    fetch(`${DISEASES_API}/${id}/usage`)
        .then(response => response.json())
        .then(data => {
            const usageCount = data.usageCount || 0;
            const message = document.getElementById('delete-message');
            const warning = document.getElementById('delete-warning');
            const deleteBtn = document.getElementById('confirm-delete-btn');
            
            if (usageCount > 0) {
                message.textContent = `This disease is currently used in ${usageCount} report(s).`;
                warning.style.display = 'block';
                deleteBtn.disabled = true;
            } else {
                message.textContent = 'Are you sure you want to delete this disease?';
                warning.style.display = 'none';
                deleteBtn.disabled = false;
            }
            
            openModal('delete-modal');
        })
        .catch(error => {
            console.error('Error checking usage:', error);
            showError('Failed to check usage');
        });
}

// ========================================
// DELETE HANDLER
// ========================================

async function handleDelete() {
    if (!currentDeleteId || !currentDeleteType) return;
    
    try {
        let url;
        switch (currentDeleteType) {
            case 'farm-type':
                url = `${FARM_TYPES_API}/${currentDeleteId}`;
                break;
            case 'animal-type':
                url = `${ANIMAL_TYPES_API}/${currentDeleteId}`;
                break;
            case 'disease':
                url = `${DISEASES_API}/${currentDeleteId}`;
                break;
            default:
                throw new Error('Unknown delete type');
        }
        
        const response = await fetch(url, {
            method: 'DELETE',
            headers: {
                [csrfHeader]: csrfToken
            }
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to delete item');
        }
        
        showSuccess('Item deleted successfully');
        closeModal('delete-modal');
        
        // Reload appropriate data
        switch (currentDeleteType) {
            case 'farm-type':
                loadFarmTypes();
                break;
            case 'animal-type':
                loadAnimalTypes();
                break;
            case 'disease':
                loadDiseases();
                break;
        }
        
        currentDeleteId = null;
        currentDeleteType = null;
    } catch (error) {
        console.error('Error deleting item:', error);
        showError(error.message);
    }
}

// ========================================
// UTILITY FUNCTIONS
// ========================================

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function showSuccess(message) {
    // TODO: Implement toast notification
    console.log('Success:', message);
    alert(message);
}

function showError(message) {
    // TODO: Implement toast notification
    console.error('Error:', message);
    alert('Error: ' + message);
}
