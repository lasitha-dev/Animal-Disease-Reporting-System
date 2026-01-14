/**
 * Analytics page JavaScript
 * Handles filter interactions and Chart.js line chart rendering
 */

// Global state
let currentChart = null;
let selectedGroupBy = 'MONTHLY';
let animalTypes = [];
let diseases = [];

// CSRF token for API calls
const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

// Chart color palette for multiple disease lines
const CHART_COLORS = [
    'rgb(59, 130, 246)',   // Blue
    'rgb(239, 68, 68)',    // Red
    'rgb(34, 197, 94)',    // Green
    'rgb(249, 115, 22)',   // Orange
    'rgb(168, 85, 247)',   // Purple
    'rgb(236, 72, 153)',   // Pink
    'rgb(20, 184, 166)',   // Teal
    'rgb(245, 158, 11)',   // Amber
    'rgb(99, 102, 241)',   // Indigo
    'rgb(107, 114, 128)',  // Gray
];

// Elements
const elements = {
    periodButtons: null,
    startDateInput: null,
    endDateInput: null,
    animalTypeSelect: null,
    diseaseSelect: null,
    applyButton: null,
    resetButton: null,
    retryButton: null,
    chartCanvas: null,
    chartLoading: null,
    chartEmptyState: null,
    chartErrorState: null,
    chartTitle: null,
    chartPeriodInfo: null,
};

/**
 * Initialize the analytics page
 */
function initAnalytics() {
    console.log('Initializing Analytics page...');
    initElements();
    initEventListeners();
    setDefaultDateRange();
    loadAnimalTypes();
    loadDiseases(); // Load all diseases initially
    fetchAndRenderChart();
}

// Handle both cases: script loaded before or after DOMContentLoaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAnalytics);
} else {
    // DOM is already ready, run immediately
    initAnalytics();
}

/**
 * Initialize DOM element references
 */
function initElements() {
    elements.periodButtons = document.querySelectorAll('.period-btn');
    elements.startDateInput = document.getElementById('startDate');
    elements.endDateInput = document.getElementById('endDate');
    elements.animalTypeSelect = document.getElementById('animalTypeSelect');
    elements.diseaseSelect = document.getElementById('diseaseSelect');
    elements.applyButton = document.getElementById('applyFilters');
    elements.resetButton = document.getElementById('resetFilters');
    elements.retryButton = document.getElementById('retryButton');
    elements.chartCanvas = document.getElementById('trendChart');
    elements.chartLoading = document.getElementById('chartLoading');
    elements.chartEmptyState = document.getElementById('chartEmptyState');
    elements.chartErrorState = document.getElementById('chartErrorState');
    elements.chartTitle = document.getElementById('chartTitle');
    elements.chartPeriodInfo = document.getElementById('chartPeriodInfo');
}

/**
 * Initialize event listeners
 */
function initEventListeners() {
    // Period toggle buttons
    elements.periodButtons.forEach(btn => {
        btn.addEventListener('click', function () {
            elements.periodButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            selectedGroupBy = this.dataset.period;
        });
    });

    // Animal type change - reload diseases
    elements.animalTypeSelect.addEventListener('change', function () {
        const animalTypeId = this.value;
        loadDiseases(animalTypeId);
    });

    // Apply button
    elements.applyButton.addEventListener('click', fetchAndRenderChart);

    // Reset button
    elements.resetButton.addEventListener('click', resetFilters);

    // Retry button
    elements.retryButton.addEventListener('click', fetchAndRenderChart);
}

/**
 * Set default date range (last month)
 */
function setDefaultDateRange() {
    const today = new Date();
    const oneMonthAgo = new Date(today);
    oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);

    elements.endDateInput.value = formatDateForInput(today);
    elements.startDateInput.value = formatDateForInput(oneMonthAgo);
}

/**
 * Format date for input[type="date"]
 */
function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * Load animal types from API
 */
async function loadAnimalTypes() {
    try {
        const response = await fetch('/api/vet/animal-types', {
            headers: getHeaders()
        });

        if (response.ok) {
            animalTypes = await response.json();
            populateAnimalTypeSelect();
        }
    } catch (error) {
        console.error('Error loading animal types:', error);
    }
}

/**
 * Populate animal type select dropdown
 */
function populateAnimalTypeSelect() {
    elements.animalTypeSelect.innerHTML = '<option value="">All Animal Types</option>';

    animalTypes.forEach(at => {
        const option = document.createElement('option');
        option.value = at.id;
        option.textContent = at.typeName;
        elements.animalTypeSelect.appendChild(option);
    });
}

/**
 * Load diseases from API (optionally filtered by animal type)
 */
async function loadDiseases(animalTypeId = null) {
    try {
        let url = '/api/vet/diseases/with-reports';
        if (animalTypeId) {
            url += `?animalTypeIds=${animalTypeId}`;
        }

        const response = await fetch(url, {
            headers: getHeaders()
        });

        if (response.ok) {
            diseases = await response.json();
            populateDiseaseSelect();
        }
    } catch (error) {
        console.error('Error loading diseases:', error);
    }
}

/**
 * Populate disease multi-select
 */
function populateDiseaseSelect() {
    elements.diseaseSelect.innerHTML = '';

    if (diseases.length === 0) {
        const option = document.createElement('option');
        option.value = '';
        option.textContent = 'No diseases available';
        option.disabled = true;
        elements.diseaseSelect.appendChild(option);
        return;
    }

    diseases.forEach(d => {
        const option = document.createElement('option');
        option.value = d.id;
        option.textContent = `${d.diseaseName} (${d.diseaseCode || 'N/A'})`;
        elements.diseaseSelect.appendChild(option);
    });
}

/**
 * Reset all filters to default
 */
function resetFilters() {
    // Reset period to Monthly
    elements.periodButtons.forEach(btn => btn.classList.remove('active'));
    document.querySelector('[data-period="MONTHLY"]').classList.add('active');
    selectedGroupBy = 'MONTHLY';

    // Reset date range
    setDefaultDateRange();

    // Reset selects
    elements.animalTypeSelect.value = '';
    loadDiseases();

    // Clear disease selection
    Array.from(elements.diseaseSelect.options).forEach(opt => opt.selected = false);

    // Reload chart
    fetchAndRenderChart();
}

/**
 * Fetch analytics data and render chart
 */
async function fetchAndRenderChart() {
    showLoading();

    try {
        const params = buildQueryParams();
        const url = `/api/vet/analytics/trends?${params.toString()}`;

        const response = await fetch(url, {
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const data = await response.json();

        if (!data.labels || data.labels.length === 0 || !data.datasets || data.datasets.length === 0) {
            showEmptyState();
            return;
        }

        // Check if all datasets have all zeros
        const hasData = data.datasets.some(ds => ds.data.some(v => v > 0));
        if (!hasData) {
            showEmptyState();
            return;
        }

        hideAllStates();
        renderChart(data);
        updateChartInfo();

    } catch (error) {
        console.error('Error fetching analytics data:', error);
        showErrorState(error.message);
    }
}

/**
 * Build query parameters for API call
 */
function buildQueryParams() {
    const params = new URLSearchParams();

    // Required params
    params.append('startDate', elements.startDateInput.value);
    params.append('endDate', elements.endDateInput.value);
    params.append('groupBy', selectedGroupBy);

    // Optional animal type
    const animalTypeId = elements.animalTypeSelect.value;
    if (animalTypeId) {
        params.append('animalTypeId', animalTypeId);
    }

    // Optional disease IDs (multiple)
    const selectedDiseases = Array.from(elements.diseaseSelect.selectedOptions);
    selectedDiseases.forEach(opt => {
        if (opt.value) {
            params.append('diseaseIds', opt.value);
        }
    });

    return params;
}

/**
 * Render Chart.js line chart
 */
function renderChart(data) {
    // Destroy existing chart if any
    if (currentChart) {
        currentChart.destroy();
    }

    const ctx = elements.chartCanvas.getContext('2d');

    // Prepare datasets with colors
    const datasets = data.datasets.map((ds, index) => ({
        label: ds.diseaseName,
        data: ds.data,
        borderColor: CHART_COLORS[index % CHART_COLORS.length],
        backgroundColor: CHART_COLORS[index % CHART_COLORS.length].replace('rgb', 'rgba').replace(')', ', 0.1)'),
        borderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6,
        tension: 0.3, // Smooth curves
        fill: false,
    }));

    currentChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleFont: {
                        size: 13
                    },
                    bodyFont: {
                        size: 12
                    },
                    padding: 12,
                    callbacks: {
                        label: function (context) {
                            return `${context.dataset.label}: ${context.parsed.y} reports`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    display: true,
                    title: {
                        display: true,
                        text: getXAxisLabel(),
                        font: {
                            size: 12,
                            weight: 'bold'
                        }
                    },
                    grid: {
                        display: false
                    },
                    ticks: {
                        maxRotation: 45,
                        minRotation: 0
                    }
                },
                y: {
                    display: true,
                    title: {
                        display: true,
                        text: 'Number of Reports',
                        font: {
                            size: 12,
                            weight: 'bold'
                        }
                    },
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        precision: 0
                    }
                }
            }
        }
    });
}

/**
 * Get X-axis label based on groupBy
 */
function getXAxisLabel() {
    switch (selectedGroupBy) {
        case 'WEEKLY': return 'Week';
        case 'MONTHLY': return 'Month';
        case 'ANNUALLY': return 'Year';
        default: return 'Period';
    }
}

/**
 * Update chart info display
 */
function updateChartInfo() {
    const startDate = new Date(elements.startDateInput.value);
    const endDate = new Date(elements.endDateInput.value);

    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    const startStr = startDate.toLocaleDateString('en-US', options);
    const endStr = endDate.toLocaleDateString('en-US', options);

    elements.chartPeriodInfo.textContent = `${startStr} - ${endStr} (${selectedGroupBy.toLowerCase()})`;
}

/**
 * Show loading state
 */
function showLoading() {
    elements.chartCanvas.style.display = 'none';
    elements.chartLoading.style.display = 'flex';
    elements.chartEmptyState.style.display = 'none';
    elements.chartErrorState.style.display = 'none';
}

/**
 * Show empty state
 */
function showEmptyState() {
    elements.chartCanvas.style.display = 'none';
    elements.chartLoading.style.display = 'none';
    elements.chartEmptyState.style.display = 'flex';
    elements.chartErrorState.style.display = 'none';
}

/**
 * Show error state
 */
function showErrorState(message) {
    elements.chartCanvas.style.display = 'none';
    elements.chartLoading.style.display = 'none';
    elements.chartEmptyState.style.display = 'none';
    elements.chartErrorState.style.display = 'flex';

    const errorMessage = document.getElementById('chartErrorMessage');
    if (errorMessage && message) {
        errorMessage.textContent = message;
    }
}

/**
 * Hide all overlay states
 */
function hideAllStates() {
    elements.chartCanvas.style.display = 'block';
    elements.chartLoading.style.display = 'none';
    elements.chartEmptyState.style.display = 'none';
    elements.chartErrorState.style.display = 'none';
}

/**
 * Get headers for API calls including CSRF token
 */
function getHeaders() {
    const headers = {
        'Content-Type': 'application/json'
    };

    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    return headers;
}
