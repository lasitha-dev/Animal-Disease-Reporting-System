/**
 * Vet Dashboard JavaScript
 * Handles KPI loading, Chart.js initialization, and activity feed
 */

// Chart instance
let healthTrendChart = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initVetDashboard();
});

/**
 * Initialize the vet dashboard
 */
async function initVetDashboard() {
    // Load KPIs, chart, and activity in parallel (independent data sources)
    await Promise.all([
        loadVetKPIs(),
        initHealthTrendChartAndData(),
        loadRecentActivity()
    ]);
}

/**
 * Initialize chart and load data together
 */
async function initHealthTrendChartAndData() {
    initHealthTrendChart();
    // loadHealthTrendData is called inside initHealthTrendChart, no need to await separately
}

/**
 * Load KPI data from API
 */
async function loadVetKPIs() {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    const headers = {};
    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    try {
        const response = await fetch('/api/vet/stats', { headers });
        if (!response.ok) throw new Error('Failed to load stats');

        const stats = await response.json();

        // Update KPI values with real data from backend
        updateKPI('kpi-outbreaks', stats.activeOutbreaks || 0);
        updateKPI('kpi-high-risk', stats.highRiskFarms || 0);
        updateKPI('kpi-coverage', calculateCoverage(stats));
        updateKPI('kpi-followups', stats.pendingFollowups || 0);

    } catch (error) {
        console.error('Error loading KPI data:', error);
    }
}

/**
 * Update a KPI card value
 */
function updateKPI(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = value;
    }
}

/**
 * Calculate monthly coverage percentage
 */
function calculateCoverage(stats) {
    if (!stats.farmsCount || stats.farmsCount === 0) return '0%';
    const inspected = stats.farmsInspectedThisMonth || stats.farmsCount;
    const percentage = Math.round((inspected / stats.farmsCount) * 100);
    return `${Math.min(percentage, 100)}%`;
}

/**
 * Initialize the Health Trend Chart
 * Uses Chart.js with Indigo/Emerald palette
 */
function initHealthTrendChart() {
    const ctx = document.getElementById('health-trend-chart');
    if (!ctx) return;

    // Destroy existing chart
    if (healthTrendChart) {
        healthTrendChart.destroy();
    }

    // Generate sample data for the last 7 days
    const labels = generateDateLabels(7);

    healthTrendChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Reports',
                    data: generateSampleData(7, 0, 10),
                    borderColor: '#4f46e5', // Indigo
                    backgroundColor: 'rgba(79, 70, 229, 0.1)',
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    pointBackgroundColor: '#4f46e5',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                },
                {
                    label: 'Resolved',
                    data: generateSampleData(7, 0, 8),
                    borderColor: '#10b981', // Emerald
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    pointBackgroundColor: '#10b981',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            layout: {
                padding: {
                    left: 0,
                    right: 10,
                    top: 10,
                    bottom: 0
                }
            },
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    titleFont: { size: 13, weight: '600' },
                    bodyFont: { size: 12 },
                    padding: 12,
                    cornerRadius: 8,
                    displayColors: true,
                    boxWidth: 8,
                    boxHeight: 8,
                    boxPadding: 4
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        font: { size: 10 },
                        color: '#64748b',
                        maxRotation: 0,
                        autoSkip: true,
                        maxTicksLimit: 7
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(226, 232, 240, 0.8)',
                        drawBorder: false
                    },
                    ticks: {
                        font: { size: 10 },
                        color: '#64748b',
                        stepSize: 2,
                        padding: 4,
                        maxTicksLimit: 6
                    }
                }
            }
        }
    });

    // Load real data
    loadHealthTrendData();
}

/**
 * Load health trend data from API
 */
async function loadHealthTrendData() {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    const headers = {};
    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    try {
        const response = await fetch('/api/vet/health-trend?days=7', { headers });
        if (response.ok) {
            const data = await response.json();
            if (healthTrendChart && data.labels && data.reports) {
                healthTrendChart.data.labels = data.labels;
                healthTrendChart.data.datasets[0].data = data.reports;
                healthTrendChart.data.datasets[1].data = data.resolved || data.reports.map(() => 0);
                healthTrendChart.update();
            }
        }
    } catch (error) {
        console.error('Error loading health trend data:', error);
        // Keep showing sample data
    }
}

/**
 * Load recent activity feed
 */
async function loadRecentActivity() {
    const feedContainer = document.getElementById('activity-feed');
    if (!feedContainer) return;

    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    const headers = {};
    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    try {
        const response = await fetch('/api/vet/recent-activity?limit=5', { headers });

        if (response.ok) {
            const activities = await response.json();
            renderActivityFeed(activities);
        } else {
            // Fallback to disease reports
            await loadFallbackActivity();
        }
    } catch (error) {
        console.error('Error loading activity:', error);
        await loadFallbackActivity();
    }
}

/**
 * Fallback: Load recent disease reports as activity (limited to 5)
 */
async function loadFallbackActivity() {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    const headers = {};
    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    try {
        // Use the existing disease-reports endpoint but only display 5
        // TODO: Add a server-side limit parameter to avoid transferring all reports
        const response = await fetch('/api/vet/disease-reports', { headers });

        if (response.ok) {
            const reports = await response.json();
            const activities = (reports.slice(0, 5)).map(report => ({
                type: 'report',
                title: `${report.effectiveDiseaseName || report.diseaseName || 'Disease'} reported`,
                description: report.farmName || 'Unknown farm',
                time: report.createdAt || report.reportDate
            }));
            renderActivityFeed(activities);
        } else {
            showEmptyActivity();
        }
    } catch (error) {
        showEmptyActivity();
    }
}

/**
 * Render the activity feed
 */
function renderActivityFeed(activities) {
    const feedContainer = document.getElementById('activity-feed');
    if (!feedContainer) return;

    if (!activities || activities.length === 0) {
        showEmptyActivity();
        return;
    }

    const html = activities.map(activity => {
        const iconClass = getActivityIconClass(activity.type);
        const iconSymbol = getActivityIcon(activity.type);
        const timeAgo = formatTimeAgo(activity.time);

        return `
            <div class="activity-item">
                <div class="activity-icon ${iconClass}">
                    ${iconSymbol}
                </div>
                <div class="activity-content">
                    <div class="activity-title">${escapeHtml(activity.title)}</div>
                    <div class="activity-meta">${escapeHtml(activity.description)} • ${timeAgo}</div>
                </div>
            </div>
        `;
    }).join('');

    feedContainer.innerHTML = html;
}

/**
 * Show empty activity state
 */
function showEmptyActivity() {
    const feedContainer = document.getElementById('activity-feed');
    if (feedContainer) {
        feedContainer.innerHTML = `
            <div class="activity-empty">
                <p>No recent activity</p>
            </div>
        `;
    }
}

/**
 * Get activity icon class based on type
 */
function getActivityIconClass(type) {
    const classes = {
        'report': 'activity-icon--report',
        'farm': 'activity-icon--farm',
        'resolved': 'activity-icon--resolved'
    };
    return classes[type] || 'activity-icon--report';
}

/**
 * Get activity icon symbol
 */
function getActivityIcon(type) {
    const icons = {
        'report': '<i data-lucide="bug" class="icon icon-xs"></i>',
        'farm': '<i data-lucide="home" class="icon icon-xs"></i>',
        'resolved': '<i data-lucide="check" class="icon icon-xs"></i>'
    };
    return icons[type] || '<i data-lucide="clipboard-list" class="icon icon-xs"></i>';
}

/**
 * Generate date labels for the last N days
 */
function generateDateLabels(days) {
    const labels = [];
    const today = new Date();

    for (let i = days - 1; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(date.getDate() - i);
        labels.push(date.toLocaleDateString('en-US', { weekday: 'short' }));
    }

    return labels;
}

/**
 * Generate sample data for chart
 */
function generateSampleData(count, min, max) {
    const data = [];
    for (let i = 0; i < count; i++) {
        data.push(Math.floor(Math.random() * (max - min + 1)) + min);
    }
    return data;
}

/**
 * Format time ago string
 */
function formatTimeAgo(dateString) {
    if (!dateString) return 'Recently';

    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;

    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
}

/**
 * Escape HTML to prevent XSS
 */
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
