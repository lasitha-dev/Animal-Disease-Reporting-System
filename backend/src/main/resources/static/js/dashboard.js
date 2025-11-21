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
let usersDataLoaded = false;

const RECENT_ITEMS_LIMIT = 5;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', () => {
    setupDashboardViews();
    loadDashboardData();
});

// Load all dashboard data
async function loadDashboardData() {
    await loadSummaryStats();
    await loadOverviewChart();
    
    // Load overview sections immediately
    loadFarmTypesData();
    loadAnimalTypesData();
    loadDiseasesData();
}

// Setup overview/users view toggle
function setupDashboardViews() {
    const viewButtons = document.querySelectorAll('.view-button');
    const views = document.querySelectorAll('.dashboard-view');
    if (!viewButtons.length || !views.length) {
        return;
    }

    for (const button of viewButtons) {
        button.addEventListener('click', async () => {
            const targetId = button.dataset.target;
            if (!targetId) {
                return;
            }

            for (const btn of viewButtons) {
                btn.classList.remove('active');
            }
            button.classList.add('active');

            for (const view of views) {
                if (view.id === targetId) {
                    view.classList.add('active');
                    view.removeAttribute('hidden');
                } else {
                    view.classList.remove('active');
                    view.setAttribute('hidden', 'hidden');
                }
            }

            if (targetId === 'users-content' && !usersDataLoaded) {
                await loadUsersData();
                usersDataLoaded = true;
            }
        });
    }
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
        globalThis.dashboardStats = stats;
        
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
    const stats = globalThis.dashboardStats || {};
    
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
    
    const recentTypes = getRecentItems(farmTypes);
    if (recentTypes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="2" style="text-align: center;">No farm types found</td></tr>';
        return;
    }
    
    tbody.innerHTML = recentTypes.map(ft => `
        <tr>
            <td>${escapeHtml(ft.typeName)}</td>
            <td><span class="badge ${ft.isActive ? 'badge-success' : 'badge-secondary'}">
                ${ft.isActive ? 'Active' : 'Inactive'}
            </span></td>
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
    
    const recentAnimals = getRecentItems(animalTypes);
    if (recentAnimals.length === 0) {
        tbody.innerHTML = '<tr><td colspan="2" style="text-align: center;">No animal types found</td></tr>';
        return;
    }
    
    tbody.innerHTML = recentAnimals.map(at => `
        <tr>
            <td>${escapeHtml(at.typeName)}</td>
            <td><span class="badge ${at.isActive ? 'badge-success' : 'badge-secondary'}">
                ${at.isActive ? 'Active' : 'Inactive'}
            </span></td>
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
        tbody.innerHTML = '<tr><td colspan="3" style="text-align: center;">No diseases found</td></tr>';
        return;
    }
    
    tbody.innerHTML = diseases.map(d => `
        <tr>
            <td>${escapeHtml(d.diseaseName)}</td>
            <td><span class="badge badge-${getSeverityClass(d.severity)}">
                ${d.severity}
            </span></td>
            <td><span class="badge ${d.isActive ? 'badge-success' : 'badge-secondary'}">
                ${d.isActive ? 'Active' : 'Inactive'}
            </span></td>
        </tr>
    `).join('');
}

// Load users data
async function loadUsersData() {
    const stats = globalThis.dashboardStats || {};
    
    // Update stats
    updateStat('stat-admin-count', stats.adminCount || 0);
    updateStat('stat-vet-count', stats.vetCount || 0);
    
    // Load chart
    loadUsersChart(stats.adminCount || 0, stats.vetCount || 0);
    
    // Initialize and load district map
    await initDistrictMap();
    usersDataLoaded = true;
}

// Initialize district map
async function initDistrictMap() {
    // Initialize the map using the new district map implementation
    const mapContainer = document.getElementById('sri-lanka-map');
    if (!mapContainer) {
        console.error('Map container not found');
        return;
    }
    
    // Initialize map with click handler
    globalThis.districtMapInstance = initializeDistrictMap('sri-lanka-map', handleDistrictClick);
    
    // Load initial data (all users)
    await loadDistrictMapData();
    
    // Setup role filter buttons
    setupMapFilters();
    
    // Setup modal handlers
    setupDistrictModal();
}

// Handle district click event
function handleDistrictClick(districtKey, districtName) {
    const activeFilter = document.querySelector('.map-filter-btn.active');
    const role = activeFilter ? activeFilter.dataset.role : null;
    
    showDistrictModal({
        district: districtKey,
        displayName: districtName,
        role: role
    });
}

// Load district map data
async function loadDistrictMapData(role = null) {
    try {
        // Show loading state
        const loadingEl = document.getElementById('map-loading');
        if (loadingEl) loadingEl.style.display = 'block';
        
        // Build URL with optional role parameter
        let url = `${API_DASHBOARD}/users/district-distribution`;
        if (role) {
            url += `?role=${role}`;
        }
        
        const response = await fetch(url);
        if (!response.ok) throw new Error('Failed to load district data');
        
        const data = await response.json();
        
        // Update district colors based on user count
        updateDistrictColors(globalThis.districtMapInstance, data);
        
        // Hide loading state
        if (loadingEl) loadingEl.style.display = 'none';
        
    } catch (error) {
        console.error('Error loading district map data:', error);
        const loadingEl = document.getElementById('map-loading');
        if (loadingEl) {
            loadingEl.textContent = 'Failed to load map data';
            loadingEl.style.color = 'var(--color-error)';
        }
    }
}

// Setup map filter buttons
function setupMapFilters() {
    const filterButtons = document.querySelectorAll('.map-filter-btn');
    
    for (const button of filterButtons) {
        button.addEventListener('click', async () => {
            // Update active button
            for (const btn of filterButtons) {
                btn.classList.remove('active');
            }
            button.classList.add('active');
            
            // Get selected role
            const { role } = button.dataset;
            
            // Reload map with filter
            await loadDistrictMapData(role || null);
        });
    }
}

// Setup district modal
function setupDistrictModal() {
    const modal = document.getElementById('province-modal');
    const closeBtn = document.getElementById('close-province-modal');
    
    // Close button handler
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            modal.classList.remove('active');
        });
    }
    
    // Click outside to close
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });
    
    // Listen for district click events from the map (legacy event name maintained for compatibility)
    document.addEventListener('districtClicked', async (e) => {
        await showDistrictModal(e.detail);
    });
}

// Show district modal with user list
async function showDistrictModal(detail) {
    const modal = document.getElementById('province-modal');
    const title = document.getElementById('province-modal-title');
    const statsContainer = document.getElementById('province-modal-stats');
    const userList = document.getElementById('province-user-list');
    const loading = document.getElementById('province-modal-loading');
    const errorDiv = document.getElementById('province-modal-error');
    
    // Set title
    title.textContent = `Users in ${detail.displayName}`;
    
    // Clear previous content
    statsContainer.innerHTML = '';
    userList.innerHTML = '';
    
    // Show modal and loading state
    modal.classList.add('active');
    loading.style.display = 'block';
    errorDiv.style.display = 'none';
    
    try {
        // Fetch users for this district
        let url = `${API_DASHBOARD}/users/by-district?district=${detail.district}`;
        if (detail.role) {
            url += `&role=${detail.role}`;
        }
        
        const response = await fetch(url);
        if (!response.ok) throw new Error('Failed to load users');
        
        const users = await response.json();
        
        // Hide loading
        loading.style.display = 'none';
        
        // Display stats
        displayDistrictStats(users, statsContainer);
        
        // Display user list
        displayDistrictUsers(users, userList);
        
    } catch (error) {
        console.error('Error loading district users:', error);
        loading.style.display = 'none';
        errorDiv.style.display = 'block';
    }
}

// Display district statistics
function displayDistrictStats(users, container) {
    const totalUsers = users.length;
    const adminCount = users.filter(u => u.role === 'ADMIN').length;
    const vetCount = users.filter(u => u.role === 'VETERINARY_OFFICER').length;
    
    const statsHTML = `
        <div class="province-modal-stat">
            <div class="province-modal-stat-value">${totalUsers}</div>
            <div class="province-modal-stat-label">Total Users</div>
        </div>
        <div class="province-modal-stat">
            <div class="province-modal-stat-value">${adminCount}</div>
            <div class="province-modal-stat-label">Administrators</div>
        </div>
        <div class="province-modal-stat">
            <div class="province-modal-stat-value">${vetCount}</div>
            <div class="province-modal-stat-label">Veterinary Officers</div>
        </div>
    `;
    
    container.innerHTML = statsHTML;
}

// Display district users
function displayDistrictUsers(users, container) {
    if (users.length === 0) {
        container.innerHTML = '<li class="province-modal-loading">No users found in this district.</li>';
        return;
    }
    
    const userItems = users.map(user => {
        const roleClass = user.role === 'ADMIN' ? 'admin' : 'veterinary-officer';
        const roleDisplay = user.role === 'ADMIN' ? 'Admin' : 'Vet Officer';
        
        return `
            <li class="province-user-item">
                <div class="province-user-name">
                    ${escapeHtml(user.firstName)} ${escapeHtml(user.lastName)}
                </div>
                <div class="province-user-details">
                    <div class="province-user-detail">
                        <span>👤</span>
                        <span>${escapeHtml(user.username)}</span>
                    </div>
                    <div class="province-user-detail">
                        <span>📧</span>
                        <span>${escapeHtml(user.email)}</span>
                    </div>
                    ${user.phoneNumber ? `
                    <div class="province-user-detail">
                        <span>📞</span>
                        <span>${escapeHtml(user.phoneNumber)}</span>
                    </div>
                    ` : ''}
                    ${user.district ? `
                    <div class="province-user-detail">
                        <span>📍</span>
                        <span>${escapeHtml(user.district)}</span>
                    </div>
                    ` : ''}
                    <div class="province-user-detail">
                        <span class="province-user-role ${roleClass}">${roleDisplay}</span>
                    </div>
                </div>
            </li>
        `;
    }).join('');
    
    container.innerHTML = userItems;
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

function getRecentItems(items) {
    if (!Array.isArray(items)) {
        return [];
    }
    const sortedItems = [...items].sort((a, b) => getTimestamp(b?.createdAt) - getTimestamp(a?.createdAt));
    return sortedItems.slice(0, RECENT_ITEMS_LIMIT);
}

function getTimestamp(value) {
    if (!value) {
        return 0;
    }
    const parsed = Date.parse(value);
    return Number.isNaN(parsed) ? 0 : parsed;
}

function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .toString()
        .replaceAll('&', "&amp;")
        .replaceAll('<', "&lt;")
        .replaceAll('>', "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
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
