/**
 * Dashboard Charts JavaScript
 * Handles Chart.js initialization and data fetching for dashboard analytics
 */

// CSRF Token
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

// API URLs
const DASHBOARD_API = '/api/dashboard';

// Chart instances (for cleanup/update)
const chartInstances = {};

// Chart color schemes
const COLORS = {
    primary: ['#3b82f6', '#60a5fa', '#93c5fd', '#bfdbfe', '#dbeafe'],
    success: ['#10b981', '#34d399', '#6ee7b7', '#a7f3d0', '#d1fae5'],
    warning: ['#f59e0b', '#fbbf24', '#fcd34d', '#fde68a', '#fef3c7'],
    danger: ['#ef4444', '#f87171', '#fca5a5', '#fecaca', '#fee2e2'],
    info: ['#8b5cf6', '#a78bfa', '#c4b5fd', '#ddd6fe', '#ede9fe'],
    mixed: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899']
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    loadDashboardStatistics();
    loadCharts();
});

// ========================================
// STATISTICS LOADING
// ========================================

async function loadDashboardStatistics() {
    try {
        const response = await fetch(`${DASHBOARD_API}/summary`);
        if (!response.ok) throw new Error('Failed to load statistics');
        
        const stats = await response.json();
        updateStatisticsDisplay(stats);
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

function updateStatisticsDisplay(stats) {
    // Main statistics
    updateStat('total-farms', stats.totalFarms || 0);
    updateStat('total-animals', stats.totalAnimals || 0);
    updateStat('total-reports', stats.totalDiseaseReports || 0);
    updateStat('total-users', stats.totalUsers || 0);
    
    // Configuration statistics
    updateStat('total-farm-types', stats.activeFarmTypes || 0);
    updateStat('total-animal-types', stats.activeAnimalTypes || 0);
    updateStat('total-diseases', stats.activeDiseases || 0);
    updateStat('notifiable-diseases', stats.notifiableDiseases || 0);
}

function updateStat(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = value.toLocaleString();
        animateValue(element, 0, value, 1000);
    }
}

function animateValue(element, start, end, duration) {
    const range = end - start;
    const increment = range / (duration / 16); // 60fps
    let current = start;
    
    const timer = setInterval(() => {
        current += increment;
        if ((increment > 0 && current >= end) || (increment < 0 && current <= end)) {
            element.textContent = Math.round(end).toLocaleString();
            clearInterval(timer);
        } else {
            element.textContent = Math.round(current).toLocaleString();
        }
    }, 16);
}

// ========================================
// CHART LOADING
// ========================================

async function loadCharts() {
    // Load pie charts
    await loadFarmTypeChart();
    await loadDiseaseSeverityChart();
    
    // Check if user is admin (elements only visible to admin)
    if (document.getElementById('userRoleChart')) {
        await loadUserRoleChart();
        await loadUserStatusChart();
        await loadUserTrendChart();
    }
    
    // Load trend charts
    await loadFarmTrendChart();
    await loadDiseaseReportTrendChart();
}

// ========================================
// PIE CHARTS
// ========================================

async function loadFarmTypeChart() {
    try {
        const response = await fetch(`${DASHBOARD_API}/charts/farm-types`);
        if (!response.ok) throw new Error('Failed to load farm type data');
        
        const data = await response.json();
        createPieChart('farmTypeChart', data.labels, data.data, 'Farm Types');
    } catch (error) {
        console.error('Error loading farm type chart:', error);
        showChartError('farmTypeChart');
    }
}

async function loadDiseaseSeverityChart() {
    try {
        const response = await fetch(`${DASHBOARD_API}/charts/disease-severity`);
        if (!response.ok) throw new Error('Failed to load disease severity data');
        
        const data = await response.json();
        const severityColors = {
            'LOW': COLORS.info[0],
            'MEDIUM': COLORS.warning[0],
            'HIGH': COLORS.danger[0],
            'CRITICAL': COLORS.danger[2]
        };
        const backgroundColors = data.labels.map(label => severityColors[label] || COLORS.primary[0]);
        
        createPieChart('diseaseSeverityChart', data.labels, data.data, 'Disease Severity', backgroundColors);
    } catch (error) {
        console.error('Error loading disease severity chart:', error);
        showChartError('diseaseSeverityChart');
    }
}

async function loadUserRoleChart() {
    try {
        const response = await fetch(`${DASHBOARD_API}/charts/user-roles`);
        if (!response.ok) throw new Error('Failed to load user role data');
        
        const data = await response.json();
        createPieChart('userRoleChart', data.labels, data.data, 'User Roles');
    } catch (error) {
        console.error('Error loading user role chart:', error);
        showChartError('userRoleChart');
    }
}

async function loadUserStatusChart() {
    try {
        const response = await fetch(`${DASHBOARD_API}/charts/user-status`);
        if (!response.ok) throw new Error('Failed to load user status data');
        
        const data = await response.json();
        const statusColors = ['#10b981', '#ef4444']; // Green for active, Red for inactive
        createPieChart('userStatusChart', data.labels, data.data, 'User Status', statusColors);
    } catch (error) {
        console.error('Error loading user status chart:', error);
        showChartError('userStatusChart');
    }
}

function createPieChart(canvasId, labels, data, title, customColors = null) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    
    // Destroy existing chart if it exists
    if (chartInstances[canvasId]) {
        chartInstances[canvasId].destroy();
    }
    
    const backgroundColors = customColors || COLORS.mixed.slice(0, labels.length);
    
    chartInstances[canvasId] = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: backgroundColors,
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        font: {
                            size: 12
                        }
                    }
                },
                title: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

// ========================================
// LINE CHARTS (TRENDS)
// ========================================

async function loadFarmTrendChart() {
    try {
        const response = await fetch(`${DASHBOARD_API}/charts/farm-trend?months=6`);
        if (!response.ok) throw new Error('Failed to load farm trend data');
        
        const data = await response.json();
        createLineChart('farmTrendChart', data.labels, data.data, 'Farms Registered', COLORS.primary[0]);
    } catch (error) {
        console.error('Error loading farm trend chart:', error);
        showChartError('farmTrendChart');
    }
}

async function loadDiseaseReportTrendChart() {
    try {
        const response = await fetch(`${DASHBOARD_API}/charts/disease-report-trend?months=6`);
        if (!response.ok) throw new Error('Failed to load disease report trend data');
        
        const data = await response.json();
        createLineChart('diseaseReportTrendChart', data.labels, data.data, 'Disease Reports', COLORS.danger[0]);
    } catch (error) {
        console.error('Error loading disease report trend chart:', error);
        showChartError('diseaseReportTrendChart');
    }
}

async function loadUserTrendChart() {
    try {
        const response = await fetch(`${DASHBOARD_API}/charts/user-trend?months=6`);
        if (!response.ok) throw new Error('Failed to load user trend data');
        
        const data = await response.json();
        createLineChart('userTrendChart', data.labels, data.data, 'User Registrations', COLORS.success[0]);
    } catch (error) {
        console.error('Error loading user trend chart:', error);
        showChartError('userTrendChart');
    }
}

function createLineChart(canvasId, labels, data, label, color) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    
    // Destroy existing chart if it exists
    if (chartInstances[canvasId]) {
        chartInstances[canvasId].destroy();
    }
    
    chartInstances[canvasId] = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: label,
                data: data,
                borderColor: color,
                backgroundColor: color + '20', // Add transparency
                borderWidth: 2,
                tension: 0.4,
                fill: true,
                pointBackgroundColor: color,
                pointBorderColor: '#ffffff',
                pointBorderWidth: 2,
                pointRadius: 4,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    callbacks: {
                        label: function(context) {
                            return `${context.dataset.label}: ${context.parsed.y}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        precision: 0
                    },
                    grid: {
                        color: '#e5e7eb'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            },
            interaction: {
                mode: 'nearest',
                axis: 'x',
                intersect: false
            }
        }
    });
}

// ========================================
// ERROR HANDLING
// ========================================

function showChartError(canvasId) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    
    const container = canvas.parentElement;
    container.innerHTML = `
        <div style="
            display: flex;
            align-items: center;
            justify-content: center;
            height: 300px;
            color: #6b7280;
            flex-direction: column;
            gap: 0.5rem;
        ">
            <div style="font-size: 2rem;">📊</div>
            <div>Unable to load chart data</div>
        </div>
    `;
}

// ========================================
// REFRESH FUNCTIONALITY
// ========================================

function refreshDashboard() {
    loadDashboardStatistics();
    loadCharts();
}

// Auto-refresh every 5 minutes
setInterval(refreshDashboard, 5 * 60 * 1000);
