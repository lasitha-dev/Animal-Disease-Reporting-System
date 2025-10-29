/**
 * Dashboard JavaScript
 * Handles filter controls and data loading for dashboard
 */

// CSRF Token
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

// API endpoints
const API_DASHBOARD = '/api/dashboard';
const API_CONFIG = '/api/configuration';

// Chart instances
let overviewChart = null;
let diseaseChart = null;
let usersChart = null;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', () => {
    setupFilterControls();
    loadDashboardData();
});

// Setup filter button controls
function setupFilterControls() {
    const filterButtons = document.querySelectorAll('.filter-button');
    
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Update active button
            filterButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            
            // Show corresponding section
            const filter = button.getAttribute('data-filter');
            showSection(filter);
        });
    });
}

// Show section based on filter
function showSection(filter) {
    // Hide all sections
    const sections = document.querySelectorAll('.data-section');
    sections.forEach(section => section.classList.remove('active'));
    
    // Show selected section
    const section = document.getElementById(`section-${filter}`);
    if (section) {
        section.classList.add('active');
        
        // Load data for the section
        loadSectionData(filter);
    }
}

// Load all dashboard data
async function loadDashboardData() {
    await loadSummaryStats();
    await loadOverviewChart();
}

// Load summary statistics
async function loadSummaryStats() {
    try {
        const response = await fetch(`${API_DASHBOARD}/summary`);
        if (!response.ok) throw new Error('Failed to load summary stats');
        
        const stats = await response.json();
        
        // Update overview stats
        updateStat('stat-farm-types', stats.activeFarmTypes || 0);
        updateStat('stat-animal-types', stats.activeAnimalTypes || 0);
        updateStat('stat-diseases', stats.activeDiseases || 0);
        updateStat('stat-notifiable', stats.notifiableDiseases || 0);
        updateStat('stat-admins', stats.adminCount || 0);
        updateStat('stat-vets', stats.vetCount || 0);
        
        // Store stats for section-specific displays
        window.dashboardStats = stats;
        
    } catch (error) {
        console.error('Error loading summary stats:', error);
    }
}

// Update stat value
function updateStat(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = value;
    }
}

// Load overview chart
async function loadOverviewChart() {
    const stats = window.dashboardStats || {};
    
    const ctx = document.getElementById('overview-chart');
    if (!ctx) return;
    
    // Destroy existing chart
    if (overviewChart) {
        overviewChart.destroy();
    }
    
    overviewChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Farm Types', 'Animal Types', 'Diseases', 'Admins', 'Vets'],
            datasets: [{
                label: 'Count',
                data: [
                    stats.activeFarmTypes || 0,
                    stats.activeAnimalTypes || 0,
                    stats.activeDiseases || 0,
                    stats.adminCount || 0,
                    stats.vetCount || 0
                ],
                backgroundColor: [
                    '#3b82f6',
                    '#10b981',
                    '#f59e0b',
                    '#8b5cf6',
                    '#06b6d4'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// Load section-specific data
async function loadSectionData(filter) {
    switch (filter) {
        case 'all':
            await loadOverviewChart();
            break;
        case 'farm-types':
            await loadFarmTypesData();
            break;
        case 'animal-types':
            await loadAnimalTypesData();
            break;
        case 'diseases':
            await loadDiseasesData();
            break;
        case 'users':
            await loadUsersData();
            break;
    }
}

// Load farm types data
async function loadFarmTypesData() {
    try {
        const response = await fetch(`${API_CONFIG}/farm-types`);
        if (!response.ok) throw new Error('Failed to load farm types');
        
        const farmTypes = await response.json();
        
        // Update stats
        const activeCount = farmTypes.filter(ft => ft.isActive).length;
        updateStat('stat-total-farm-types', farmTypes.length);
        updateStat('stat-active-farm-types', activeCount);
        
        // Populate table
        populateFarmTypesTable(farmTypes);
        
    } catch (error) {
        console.error('Error loading farm types:', error);
    }
}

// Populate farm types table
function populateFarmTypesTable(farmTypes) {
    const tbody = document.querySelector('#farm-types-table tbody');
    if (!tbody) return;
    
    if (farmTypes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No farm types found</td></tr>';
        return;
    }
    
    tbody.innerHTML = farmTypes.map(ft => `
        <tr>
            <td>${escapeHtml(ft.typeName)}</td>
            <td>${escapeHtml(ft.description || '-')}</td>
            <td><span class="badge ${ft.isActive ? 'badge-success' : 'badge-secondary'}">
                ${ft.isActive ? 'Active' : 'Inactive'}
            </span></td>
            <td>${formatDate(ft.createdAt)}</td>
        </tr>
    `).join('');
}

// Load animal types data
async function loadAnimalTypesData() {
    try {
        const response = await fetch(`${API_CONFIG}/animal-types`);
        if (!response.ok) throw new Error('Failed to load animal types');
        
        const animalTypes = await response.json();
        
        // Update stats
        const activeCount = animalTypes.filter(at => at.isActive).length;
        updateStat('stat-total-animal-types', animalTypes.length);
        updateStat('stat-active-animal-types', activeCount);
        
        // Populate table
        populateAnimalTypesTable(animalTypes);
        
    } catch (error) {
        console.error('Error loading animal types:', error);
    }
}

// Populate animal types table
function populateAnimalTypesTable(animalTypes) {
    const tbody = document.querySelector('#animal-types-table tbody');
    if (!tbody) return;
    
    if (animalTypes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No animal types found</td></tr>';
        return;
    }
    
    tbody.innerHTML = animalTypes.map(at => `
        <tr>
            <td>${escapeHtml(at.typeName)}</td>
            <td>${escapeHtml(at.description || '-')}</td>
            <td><span class="badge ${at.isActive ? 'badge-success' : 'badge-secondary'}">
                ${at.isActive ? 'Active' : 'Inactive'}
            </span></td>
            <td>${formatDate(at.createdAt)}</td>
        </tr>
    `).join('');
}

// Load diseases data
async function loadDiseasesData() {
    try {
        const response = await fetch(`${API_CONFIG}/diseases`);
        if (!response.ok) throw new Error('Failed to load diseases');
        
        const diseases = await response.json();
        
        // Calculate severity counts
        const severityCounts = {
            CRITICAL: diseases.filter(d => d.severity === 'CRITICAL').length,
            HIGH: diseases.filter(d => d.severity === 'HIGH').length,
            MEDIUM: diseases.filter(d => d.severity === 'MEDIUM').length,
            LOW: diseases.filter(d => d.severity === 'LOW').length
        };
        
        const notifiableCount = diseases.filter(d => d.isNotifiable).length;
        
        // Update stats
        updateStat('stat-total-diseases', diseases.length);
        updateStat('stat-notifiable-diseases', notifiableCount);
        updateStat('stat-critical-diseases', severityCounts.CRITICAL);
        updateStat('stat-high-diseases', severityCounts.HIGH);
        
        // Load chart
        loadDiseaseChart(severityCounts);
        
        // Populate table
        populateDiseasesTable(diseases);
        
    } catch (error) {
        console.error('Error loading diseases:', error);
    }
}

// Load disease severity chart
function loadDiseaseChart(severityCounts) {
    const ctx = document.getElementById('disease-chart');
    if (!ctx) return;
    
    // Destroy existing chart
    if (diseaseChart) {
        diseaseChart.destroy();
    }
    
    diseaseChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['Critical', 'High', 'Medium', 'Low'],
            datasets: [{
                data: [
                    severityCounts.CRITICAL,
                    severityCounts.HIGH,
                    severityCounts.MEDIUM,
                    severityCounts.LOW
                ],
                backgroundColor: [
                    '#ef4444',
                    '#f59e0b',
                    '#fbbf24',
                    '#10b981'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

// Populate diseases table
function populateDiseasesTable(diseases) {
    const tbody = document.querySelector('#diseases-table tbody');
    if (!tbody) return;
    
    if (diseases.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">No diseases found</td></tr>';
        return;
    }
    
    tbody.innerHTML = diseases.map(d => `
        <tr>
            <td>${escapeHtml(d.diseaseName)}</td>
            <td><span class="badge badge-${getSeverityClass(d.severity)}">
                ${d.severity}
            </span></td>
            <td>${d.isNotifiable ? '⚠️ Yes' : 'No'}</td>
            <td><span class="badge ${d.isActive ? 'badge-success' : 'badge-secondary'}">
                ${d.isActive ? 'Active' : 'Inactive'}
            </span></td>
            <td>${formatDate(d.createdAt)}</td>
        </tr>
    `).join('');
}

// Load users data
async function loadUsersData() {
    const stats = window.dashboardStats || {};
    
    // Update stats
    updateStat('stat-admin-count', stats.adminCount || 0);
    updateStat('stat-vet-count', stats.vetCount || 0);
    
    // Load chart
    loadUsersChart(stats.adminCount || 0, stats.vetCount || 0);
}

// Load users chart
function loadUsersChart(adminCount, vetCount) {
    const ctx = document.getElementById('users-chart');
    if (!ctx) return;
    
    // Destroy existing chart
    if (usersChart) {
        usersChart.destroy();
    }
    
    usersChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Administrators', 'Veterinary Officers'],
            datasets: [{
                data: [adminCount, vetCount],
                backgroundColor: [
                    '#8b5cf6',
                    '#06b6d4'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

// Utility functions
function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .toString()
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
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

function getSeverityClass(severity) {
    const classes = {
        'CRITICAL': 'danger',
        'HIGH': 'warning',
        'MEDIUM': 'info',
        'LOW': 'success'
    };
    return classes[severity] || 'secondary';
}
