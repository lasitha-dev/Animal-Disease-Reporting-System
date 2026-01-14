/**
 * Analytics page JavaScript
 * Handles filter interactions and Chart.js line chart rendering
 */

(function() {
'use strict';

// Local state (scoped to this IIFE)
let currentChart = null;
let selectedGroupBy = 'MONTHLY';
let animalTypes = [];
let diseases = [];

// Note: CSRF tokens are handled by main.js which wraps fetch globally

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
    // Disease checkbox dropdown elements
    diseaseDropdown: null,
    diseaseDropdownToggle: null,
    diseaseDropdownMenu: null,
    diseaseOptions: null,
    diseaseSearch: null,
    selectAllDiseases: null,
    clearAllDiseases: null,
    selectedCount: null,
    // Other elements
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
    // Prevent double initialization
    if (window.analyticsInitialized) {
        console.log('Analytics already initialized, skipping...');
        return;
    }
    
    // Check if DOM elements exist before initializing
    // This ensures the DOM is actually ready
    const chartCanvas = document.getElementById('trendChart');
    const startDateInput = document.getElementById('startDate');
    
    if (!chartCanvas || !startDateInput) {
        console.log('Analytics: DOM not ready, retrying in 100ms...');
        setTimeout(initAnalytics, 100);
        return;
    }
    
    window.analyticsInitialized = true;

    console.log('Initializing Analytics page...');
    initElements();
    initEventListeners();
    setDefaultDateRange();
    loadAnimalTypes();
    loadDiseases(); // Load all diseases initially
    fetchAndRenderChart();
}

// Make initAnalytics globally accessible for manual triggering
window.initAnalytics = initAnalytics;

// Initialize when DOM is ready
console.log('analytics.js loaded, waiting for DOM...');
if (document.readyState === 'loading') {
    // DOM is still loading, wait for it
    document.addEventListener('DOMContentLoaded', function() {
        console.log('DOMContentLoaded fired, initializing...');
        initAnalytics();
    });
} else {
    // DOM is already ready (script loaded after DOMContentLoaded)
    console.log('DOM already ready, initializing...');
    // Use a small delay to ensure Thymeleaf layout is fully processed
    setTimeout(initAnalytics, 50);
}

/**
 * Initialize DOM element references
 */
function initElements() {
    console.log('initElements: Finding DOM elements...');
    
    elements.periodButtons = document.querySelectorAll('.period-btn');
    elements.startDateInput = document.getElementById('startDate');
    elements.endDateInput = document.getElementById('endDate');
    elements.animalTypeSelect = document.getElementById('animalTypeSelect');
    // Disease checkbox dropdown elements
    elements.diseaseDropdown = document.getElementById('diseaseDropdown');
    elements.diseaseDropdownToggle = document.getElementById('diseaseDropdownToggle');
    elements.diseaseDropdownMenu = document.getElementById('diseaseDropdownMenu');
    elements.diseaseOptions = document.getElementById('diseaseOptions');
    elements.diseaseSearch = document.getElementById('diseaseSearch');
    elements.selectAllDiseases = document.getElementById('selectAllDiseases');
    elements.clearAllDiseases = document.getElementById('clearAllDiseases');
    elements.selectedCount = document.getElementById('selectedCount');
    // Other elements
    elements.applyButton = document.getElementById('applyFilters');
    elements.resetButton = document.getElementById('resetFilters');
    elements.retryButton = document.getElementById('retryButton');
    elements.chartCanvas = document.getElementById('trendChart');
    elements.chartLoading = document.getElementById('chartLoading');
    elements.chartEmptyState = document.getElementById('chartEmptyState');
    elements.chartErrorState = document.getElementById('chartErrorState');
    elements.chartTitle = document.getElementById('chartTitle');
    elements.chartPeriodInfo = document.getElementById('chartPeriodInfo');
    
    // Log which elements were found/missing for debugging
    console.log('initElements complete:', {
        periodButtons: elements.periodButtons?.length || 0,
        startDateInput: !!elements.startDateInput,
        endDateInput: !!elements.endDateInput,
        animalTypeSelect: !!elements.animalTypeSelect,
        diseaseDropdown: !!elements.diseaseDropdown,
        applyButton: !!elements.applyButton,
        chartCanvas: !!elements.chartCanvas,
        chartLoading: !!elements.chartLoading
    });
}

/**
 * Initialize event listeners
 */
function initEventListeners() {
    // Period toggle buttons
    if (elements.periodButtons && elements.periodButtons.length > 0) {
        elements.periodButtons.forEach(btn => {
            btn.addEventListener('click', function () {
                elements.periodButtons.forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                selectedGroupBy = this.dataset.period;
            });
        });
    } else {
        console.error('Analytics: Period buttons not found');
    }

    // Animal type change - reload diseases
    if (elements.animalTypeSelect) {
        elements.animalTypeSelect.addEventListener('change', function () {
            const animalTypeId = this.value;
            loadDiseases(animalTypeId);
        });
    } else {
        console.error('Analytics: Animal type select not found');
    }

    // Disease dropdown toggle
    if (elements.diseaseDropdownToggle && elements.diseaseDropdown) {
        elements.diseaseDropdownToggle.addEventListener('click', function (e) {
            e.stopPropagation();
            elements.diseaseDropdown.classList.toggle('open');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function (e) {
            if (elements.diseaseDropdown && !elements.diseaseDropdown.contains(e.target)) {
                elements.diseaseDropdown.classList.remove('open');
            }
        });
    } else {
        console.error('Analytics: Disease dropdown elements not found');
    }

    // Disease search
    if (elements.diseaseSearch && elements.diseaseOptions) {
        elements.diseaseSearch.addEventListener('input', function () {
            const searchTerm = this.value.toLowerCase();
            const options = elements.diseaseOptions.querySelectorAll('.dropdown-option');
            options.forEach(opt => {
                const label = opt.querySelector('label');
                if (label) {
                    const text = label.textContent.toLowerCase();
                    opt.classList.toggle('hidden', !text.includes(searchTerm));
                }
            });
        });
    } else {
        console.error('Analytics: Disease search elements not found');
    }

    // Select all diseases
    if (elements.selectAllDiseases && elements.diseaseOptions) {
        elements.selectAllDiseases.addEventListener('click', function () {
            const checkboxes = elements.diseaseOptions.querySelectorAll('input[type="checkbox"]');
            checkboxes.forEach(cb => cb.checked = true);
            updateSelectedCount();
        });
    }

    // Clear all diseases
    if (elements.clearAllDiseases && elements.diseaseOptions) {
        elements.clearAllDiseases.addEventListener('click', function () {
            const checkboxes = elements.diseaseOptions.querySelectorAll('input[type="checkbox"]');
            checkboxes.forEach(cb => cb.checked = false);
            updateSelectedCount();
        });
    }

    // Apply button
    if (elements.applyButton) {
        elements.applyButton.addEventListener('click', fetchAndRenderChart);
    } else {
        console.error('Analytics: Apply button not found');
    }

    // Reset button
    if (elements.resetButton) {
        elements.resetButton.addEventListener('click', resetFilters);
    }

    // Retry button
    if (elements.retryButton) {
        elements.retryButton.addEventListener('click', fetchAndRenderChart);
    }
}

/**
 * Set default date range (last month)
 */
function setDefaultDateRange() {
    const today = new Date();
    const oneMonthAgo = new Date(today);
    oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);

    if (elements.endDateInput) {
        elements.endDateInput.value = formatDateForInput(today);
    } else {
        console.error('Analytics: End date input not found');
    }
    
    if (elements.startDateInput) {
        elements.startDateInput.value = formatDateForInput(oneMonthAgo);
    } else {
        console.error('Analytics: Start date input not found');
    }
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
    if (!elements.animalTypeSelect) {
        console.error('Analytics: Animal type select element not found');
        return;
    }
    
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
 * Populate disease checkbox dropdown
 */
function populateDiseaseSelect() {
    if (!elements.diseaseOptions) {
        console.error('Analytics: Disease options element not found');
        return;
    }
    
    elements.diseaseOptions.innerHTML = '';
    if (elements.diseaseSearch) {
        elements.diseaseSearch.value = '';
    }

    if (diseases.length === 0) {
        const noResults = document.createElement('div');
        noResults.className = 'dropdown-no-results';
        noResults.textContent = 'No diseases available';
        elements.diseaseOptions.appendChild(noResults);
        updateSelectedCount();
        return;
    }

    diseases.forEach(d => {
        const div = document.createElement('div');
        div.className = 'dropdown-option';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.id = `disease-${d.id}`;
        checkbox.value = d.id;
        checkbox.addEventListener('change', updateSelectedCount);

        const label = document.createElement('label');
        label.htmlFor = `disease-${d.id}`;
        label.textContent = `${d.diseaseName} (${d.diseaseCode || 'N/A'})`;

        div.appendChild(checkbox);
        div.appendChild(label);
        elements.diseaseOptions.appendChild(div);
    });

    updateSelectedCount();
}

/**
 * Update the selected count display
 */
function updateSelectedCount() {
    if (!elements.diseaseOptions) {
        return;
    }
    
    const checkboxes = elements.diseaseOptions.querySelectorAll('input[type="checkbox"]:checked');
    const count = checkboxes.length;
    
    if (elements.selectedCount) {
        elements.selectedCount.textContent = `${count} disease${count !== 1 ? 's' : ''} selected`;
    }

    // Update dropdown toggle text
    if (elements.diseaseDropdownToggle) {
        const dropdownText = elements.diseaseDropdownToggle.querySelector('.dropdown-text');
        if (dropdownText) {
            if (count === 0) {
                dropdownText.textContent = 'Select diseases...';
            } else if (count <= 2) {
                const names = Array.from(checkboxes).map(cb => {
                    const label = cb.nextElementSibling;
                    return label ? label.textContent.split(' (')[0] : 'Unknown'; // Get disease name without code
                });
                dropdownText.textContent = names.join(', ');
            } else {
                dropdownText.textContent = `${count} diseases selected`;
            }
        }
    }
}

/**
 * Reset all filters to default
 */
function resetFilters() {
    // Reset period to Monthly
    if (elements.periodButtons && elements.periodButtons.length > 0) {
        elements.periodButtons.forEach(btn => btn.classList.remove('active'));
    }
    const monthlyBtn = document.querySelector('[data-period="MONTHLY"]');
    if (monthlyBtn) {
        monthlyBtn.classList.add('active');
    }
    selectedGroupBy = 'MONTHLY';

    // Reset date range
    setDefaultDateRange();

    // Reset selects
    if (elements.animalTypeSelect) {
        elements.animalTypeSelect.value = '';
    }
    loadDiseases();

    // Clear disease selection
    if (elements.diseaseOptions) {
        const checkboxes = elements.diseaseOptions.querySelectorAll('input[type="checkbox"]');
        checkboxes.forEach(cb => cb.checked = false);
    }
    updateSelectedCount();

    // Close dropdown
    if (elements.diseaseDropdown) {
        elements.diseaseDropdown.classList.remove('open');
    }

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

    // Required params - with null safety
    const startDate = elements.startDateInput?.value || '';
    const endDate = elements.endDateInput?.value || '';
    
    if (!startDate || !endDate) {
        console.error('Analytics: Date inputs not available');
        // Use default date range if inputs are missing
        const today = new Date();
        const oneMonthAgo = new Date(today);
        oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);
        params.append('startDate', formatDateForInput(oneMonthAgo));
        params.append('endDate', formatDateForInput(today));
    } else {
        params.append('startDate', startDate);
        params.append('endDate', endDate);
    }
    
    params.append('groupBy', selectedGroupBy);

    // Optional animal type
    const animalTypeId = elements.animalTypeSelect?.value;
    if (animalTypeId) {
        params.append('animalTypeId', animalTypeId);
    }

    // Optional disease IDs (multiple) from checkbox dropdown
    if (elements.diseaseOptions) {
        const selectedCheckboxes = elements.diseaseOptions.querySelectorAll('input[type="checkbox"]:checked');
        selectedCheckboxes.forEach(cb => {
            if (cb.value) {
                params.append('diseaseIds', cb.value);
            }
        });
    }

    return params;
}

/**
 * Render Chart.js line chart
 */
function renderChart(data) {
    // Check if Chart.js is available
    if (typeof Chart === 'undefined') {
        console.error('Chart.js library not loaded');
        showErrorState('Chart library not loaded. Please refresh the page.');
        return;
    }

    // Check if canvas element exists
    if (!elements.chartCanvas) {
        console.error('Chart canvas element not found');
        showErrorState('Chart canvas not found.');
        return;
    }

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
    if (!elements.startDateInput || !elements.endDateInput || !elements.chartPeriodInfo) {
        return;
    }
    
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
    if (elements.chartCanvas) elements.chartCanvas.style.display = 'none';
    if (elements.chartLoading) elements.chartLoading.style.display = 'flex';
    if (elements.chartEmptyState) elements.chartEmptyState.style.display = 'none';
    if (elements.chartErrorState) elements.chartErrorState.style.display = 'none';
}

/**
 * Show empty state
 */
function showEmptyState() {
    if (elements.chartCanvas) elements.chartCanvas.style.display = 'none';
    if (elements.chartLoading) elements.chartLoading.style.display = 'none';
    if (elements.chartEmptyState) elements.chartEmptyState.style.display = 'flex';
    if (elements.chartErrorState) elements.chartErrorState.style.display = 'none';
}

/**
 * Show error state
 */
function showErrorState(message) {
    if (elements.chartCanvas) elements.chartCanvas.style.display = 'none';
    if (elements.chartLoading) elements.chartLoading.style.display = 'none';
    if (elements.chartEmptyState) elements.chartEmptyState.style.display = 'none';
    if (elements.chartErrorState) elements.chartErrorState.style.display = 'flex';

    const errorMessage = document.getElementById('chartErrorMessage');
    if (errorMessage && message) {
        errorMessage.textContent = message;
    }
}

/**
 * Hide all overlay states
 */
function hideAllStates() {
    if (elements.chartCanvas) elements.chartCanvas.style.display = 'block';
    if (elements.chartLoading) elements.chartLoading.style.display = 'none';
    if (elements.chartEmptyState) elements.chartEmptyState.style.display = 'none';
    if (elements.chartErrorState) elements.chartErrorState.style.display = 'none';
}

/**
 * Get headers for API calls
 * Note: CSRF token is automatically added by main.js fetch wrapper
 */
function getHeaders() {
    return {
        'Content-Type': 'application/json'
    };
}

// End of IIFE - close the function scope
})();