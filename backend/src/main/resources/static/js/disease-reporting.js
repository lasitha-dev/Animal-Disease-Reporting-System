/**
 * Disease Reporting Page JavaScript
 * Handles form interactions, cascading dropdowns, image upload, view/edit/delete operations
 */

(function () {
    'use strict';

    // State
    let farms = [];
    let farmsWithReports = []; // Farms with disease reports for display
    let otherFarmsWithReports = []; // Other vets' farms with disease reports
    let currentDiseaseData = null;
    let selectedImageFile = null;
    let editMode = false;
    let currentReportId = null;
    let currentViewReport = null;
    let isDiseaseInfoEditMode = false;
    let clearExistingImage = false; // Flag to clear existing image when updating
    let currentAnimalCount = null; // Track registered animal count for validation
    let currentTab = 'my-reports'; // Track current tab
    let isViewingOtherReport = false; // Track if viewing other vet's report
    let otherReportsLoaded = false; // Track if other reports have been loaded
    let currentViewMode = 'grid'; // View mode: 'grid' or 'table'
    const VIEW_MODE_STORAGE_KEY = 'disease-reports-view-mode'; // localStorage key
    let isOtherDiseaseSelected = false; // Track if "Other" disease is selected
    const OTHER_DISEASE_VALUE = '__OTHER__'; // Special value for "Other" option
    let searchQuery = ''; // Current search query
    let provinceFilter = ''; // Current province filter
    let districtFilter = ''; // Current district filter
    let selectedDiseases = new Set(); // Selected disease names for filtering
    let filteredFarms = []; // Filtered farms based on search and location
    let filteredOtherFarms = []; // Filtered other vets' farms
    let allReports = []; // All my reports
    let allOtherReports = []; // All other reports

    // DOM Elements
    const elements = {
        // Report Modal
        reportModal: document.getElementById('reportModal'),
        reportModalTitle: document.getElementById('reportModalTitle'),
        openReportModalBtn: document.getElementById('openReportModal'),
        closeModalBtn: document.getElementById('closeModal'),
        cancelBtn: document.getElementById('cancelBtn'),
        submitBtn: document.getElementById('submitBtn'),
        editReportId: document.getElementById('editReportId'),

        // Form
        form: document.getElementById('disease-report-form'),
        formMessage: document.getElementById('form-message'),

        // Location Filter Dropdowns (main page filters)
        provinceFilterSelect: document.getElementById('provinceFilter'),
        districtFilterSelect: document.getElementById('districtFilter'),

        // Modal Location Filter Dropdowns
        filterProvinceSelect: document.getElementById('filterProvince'),
        filterDistrictSelect: document.getElementById('filterDistrict'),

        // Form Dropdowns
        farmSelect: document.getElementById('farmId'),
        animalTypeSelect: document.getElementById('animalTypeId'),
        diseaseSelect: document.getElementById('diseaseId'),

        // Disease info display/edit
        diseaseInfoSection: document.getElementById('diseaseInfoSection'),
        diseaseInfoDisplay: document.getElementById('diseaseInfoDisplay'),
        diseaseInfoEdit: document.getElementById('diseaseInfoEdit'),
        toggleEditDiseaseInfo: document.getElementById('toggleEditDiseaseInfo'),
        editDiseaseInfoLabel: document.getElementById('editDiseaseInfoLabel'),
        displayDiseaseName: document.getElementById('display-diseaseName'),
        displayDiseaseCode: document.getElementById('display-diseaseCode'),
        displaySeverity: document.getElementById('display-severity'),
        displayNotifiable: document.getElementById('display-notifiable'),
        displayDescription: document.getElementById('display-description'),
        editDiseaseName: document.getElementById('editDiseaseName'),
        editSeverity: document.getElementById('editSeverity'),
        editNotifiable: document.getElementById('editNotifiable'),
        editDiseaseDescription: document.getElementById('editDiseaseDescription'),

        // Other Disease form elements (for "Other" disease selection)
        otherDiseaseSection: document.getElementById('otherDiseaseSection'),
        newDiseaseName: document.getElementById('newDiseaseName'),
        newDiseaseCode: document.getElementById('newDiseaseCode'),
        newDiseaseSeverity: document.getElementById('newDiseaseSeverity'),
        newDiseaseNotifiable: document.getElementById('newDiseaseNotifiable'),
        newDiseaseDescription: document.getElementById('newDiseaseDescription'),

        // Form fields
        reportDate: document.getElementById('reportDate'),
        symptoms: document.getElementById('symptoms'),
        treatment: document.getElementById('treatment'),

        // Image upload
        imageDropzone: document.getElementById('imageDropzone'),
        imageInput: document.getElementById('imageInput'),
        imagePreviewContainer: document.getElementById('imagePreviewContainer'),
        imagePreview: document.getElementById('imagePreview'),
        removeImageBtn: document.getElementById('removeImage'),

        // Reports container
        reportsContainer: document.getElementById('reports-table-container'),
        loadingState: document.getElementById('loading-state'),
        emptyState: document.getElementById('empty-state'),
        emptyStateBtn: document.getElementById('emptyStateReportBtn'),

        // View Modal
        viewModal: document.getElementById('viewModal'),
        closeViewModalBtn: document.getElementById('closeViewModal'),
        closeViewBtn: document.getElementById('closeViewBtn'),
        editReportBtn: document.getElementById('editReportBtn'),
        deleteReportBtn: document.getElementById('deleteReportBtn'),
        viewLoading: document.getElementById('viewLoading'),
        viewContent: document.getElementById('viewContent'),
        viewPhotoCard: document.getElementById('viewPhotoCard'),
        viewPhoto: document.getElementById('viewPhoto'),

        // Delete Modal
        deleteModal: document.getElementById('deleteModal'),
        closeDeleteModalBtn: document.getElementById('closeDeleteModal'),
        cancelDeleteBtn: document.getElementById('cancelDeleteBtn'),
        confirmDeleteBtn: document.getElementById('confirmDeleteBtn'),
        deleteReportId: document.getElementById('deleteReportId'),

        // Tab elements
        tabButtons: document.querySelectorAll('.tab-btn'),
        myReportsTab: document.getElementById('my-reports-tab'),
        otherReportsTab: document.getElementById('other-reports-tab'),

        // Other reports elements
        otherReportsContainer: document.getElementById('other-reports-grid-container'),
        otherLoadingState: document.getElementById('other-loading-state'),
        otherEmptyState: document.getElementById('other-empty-state'),

        // Farm Cards Grid elements
        farmsGridContainer: document.getElementById('farms-grid-container'),
        farmsTableContainer: document.getElementById('farms-table-container'),
        farmsTableBody: document.getElementById('farms-table-body'),
        otherFarmsGridContainer: document.getElementById('other-farms-grid-container'),
        otherFarmsTableContainer: document.getElementById('other-farms-table-container'),
        otherFarmsTableBody: document.getElementById('other-farms-table-body'),

        // Search elements
        searchInput: document.getElementById('farmSearchInput'),
        clearSearchBtn: document.getElementById('clearSearchBtn'),
        noSearchResults: document.getElementById('no-search-results'),
        clearSearchFromEmptyBtn: document.getElementById('clearSearchFromEmpty'),

        // Province/District Filter elements
        provinceFilterSelect: document.getElementById('provinceFilter'),
        districtFilterSelect: document.getElementById('districtFilter'),

        // Disease Filter elements (multi-select checkbox dropdown)
        diseaseFilterDropdown: document.getElementById('diseaseFilterDropdown'),
        diseaseFilterToggle: document.getElementById('diseaseFilterToggle'),
        diseaseFilterMenu: document.getElementById('diseaseFilterMenu'),
        diseaseFilterSearch: document.getElementById('diseaseFilterSearch'),
        diseaseFilterOptions: document.getElementById('diseaseFilterOptions'),
        selectAllPageDiseases: document.getElementById('selectAllPageDiseases'),
        clearAllPageDiseases: document.getElementById('clearAllPageDiseases')
    };

    // CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    // Initialize
    document.addEventListener('DOMContentLoaded', init);

    function init() {
        loadViewPreference(); // Load saved view preference first
        setupEventListeners();
        setDefaultDate();
        loadPageProvinces(); // Load provinces for main page filter
        loadProvinces(); // Load provinces for modal filter
        loadFarms();
        loadFarmsWithReports(); // New farm-centric data loading
        handleUrlParams();
        setupTabListeners();
        setupViewToggleListeners();

        // Initialize Lucide icons for any static template icons
        if (typeof lucide !== 'undefined') lucide.createIcons();
    }

    function setupEventListeners() {
        // Report Modal controls
        elements.openReportModalBtn?.addEventListener('click', () => openModal(false));
        elements.closeModalBtn?.addEventListener('click', closeModal);
        elements.cancelBtn?.addEventListener('click', closeModal);
        elements.emptyStateBtn?.addEventListener('click', () => openModal(false));
        elements.reportModal?.addEventListener('click', (e) => {
            if (e.target === elements.reportModal) closeModal();
        });

        // Form submission
        elements.form?.addEventListener('submit', handleSubmit);

        // Search functionality
        elements.searchInput?.addEventListener('input', handleSearchInput);
        elements.clearSearchBtn?.addEventListener('click', clearSearch);
        elements.clearSearchFromEmptyBtn?.addEventListener('click', clearSearch);

        // Main page province/district filters
        elements.provinceFilterSelect?.addEventListener('change', handlePageProvinceChange);
        elements.districtFilterSelect?.addEventListener('change', handlePageDistrictChange);

        // Disease filter dropdown events
        setupDiseaseFilterListeners();

        // Modal cascading dropdowns - Location filters
        elements.filterProvinceSelect?.addEventListener('change', onProvinceFilterChange);
        elements.filterDistrictSelect?.addEventListener('change', onDistrictFilterChange);

        // Cascading dropdowns - Form fields
        elements.farmSelect?.addEventListener('change', onFarmChange);
        elements.animalTypeSelect?.addEventListener('change', onAnimalTypeChange);
        elements.diseaseSelect?.addEventListener('change', onDiseaseChange);

        // Disease info edit toggle
        elements.toggleEditDiseaseInfo?.addEventListener('click', toggleDiseaseInfoEditMode);

        // Image upload - only trigger if not clicking the label or input directly
        elements.imageDropzone?.addEventListener('click', (e) => {
            // Don't trigger if clicking the label or input (they have their own behavior)
            if (e.target.tagName !== 'LABEL' && e.target.tagName !== 'INPUT') {
                elements.imageInput?.click();
            }
        });
        elements.imageInput?.addEventListener('change', handleImageSelect);
        elements.removeImageBtn?.addEventListener('click', removeImage);

        // Drag and drop
        elements.imageDropzone?.addEventListener('dragover', handleDragOver);
        elements.imageDropzone?.addEventListener('dragleave', handleDragLeave);
        elements.imageDropzone?.addEventListener('drop', handleDrop);

        // View Modal controls
        elements.closeViewModalBtn?.addEventListener('click', closeViewModal);
        elements.closeViewBtn?.addEventListener('click', closeViewModal);
        elements.editReportBtn?.addEventListener('click', onEditReport);
        elements.deleteReportBtn?.addEventListener('click', onDeleteReport);
        elements.viewModal?.addEventListener('click', (e) => {
            if (e.target === elements.viewModal) closeViewModal();
        });

        // Delete Modal controls
        elements.closeDeleteModalBtn?.addEventListener('click', closeDeleteModal);
        elements.cancelDeleteBtn?.addEventListener('click', closeDeleteModal);
        elements.confirmDeleteBtn?.addEventListener('click', confirmDelete);
        elements.deleteModal?.addEventListener('click', (e) => {
            if (e.target === elements.deleteModal) closeDeleteModal();
        });

        // Escape key to close modals
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                if (!elements.deleteModal?.classList.contains('hidden')) {
                    closeDeleteModal();
                } else if (!elements.viewModal?.classList.contains('hidden')) {
                    closeViewModal();
                } else if (!elements.reportModal?.classList.contains('hidden')) {
                    closeModal();
                }
            }
        });
    }

    function setDefaultDate() {
        const today = new Date().toISOString().split('T')[0];
        if (elements.reportDate) {
            elements.reportDate.value = today;
        }
    }

    // ========================================
    // Tab Switching Functions
    // ========================================

    function setupTabListeners() {
        elements.tabButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const tabName = btn.dataset.tab;
                switchTab(tabName);
            });
        });
    }

    function switchTab(tabName) {
        currentTab = tabName;

        // Update tab buttons
        elements.tabButtons.forEach(btn => {
            if (btn.dataset.tab === tabName) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });

        // Update tab content
        if (tabName === 'my-reports') {
            elements.myReportsTab?.classList.add('active');
            elements.otherReportsTab?.classList.remove('active');
        } else {
            elements.myReportsTab?.classList.remove('active');
            elements.otherReportsTab?.classList.add('active');

            // Load other farms with reports on first switch
            if (!otherReportsLoaded) {
                loadOtherFarmsWithReports();
            }
        }
    }

    // ========================================
    // View Toggle Functions
    // ========================================

    function setupViewToggleListeners() {
        elements.gridViewBtn?.addEventListener('click', () => setViewMode('grid'));
        elements.tableViewBtn?.addEventListener('click', () => setViewMode('table'));
    }

    function loadViewPreference() {
        const savedMode = localStorage.getItem(VIEW_MODE_STORAGE_KEY);
        if (savedMode && (savedMode === 'grid' || savedMode === 'table')) {
            currentViewMode = savedMode;
        }
        applyViewMode();
    }

    function saveViewPreference(mode) {
        localStorage.setItem(VIEW_MODE_STORAGE_KEY, mode);
    }

    function setViewMode(mode) {
        if (mode === currentViewMode) return;
        currentViewMode = mode;
        saveViewPreference(mode);
        applyViewMode();
        updateViewDisplay();
    }

    function applyViewMode() {
        // Update toggle button active states
        if (currentViewMode === 'grid') {
            elements.gridViewBtn?.classList.add('active');
            elements.tableViewBtn?.classList.remove('active');
        } else {
            elements.gridViewBtn?.classList.remove('active');
            elements.tableViewBtn?.classList.add('active');
        }
    }

    function updateViewDisplay() {
        if (currentViewMode === 'grid') {
            if (elements.reportsGridContainer) elements.reportsGridContainer.style.display = 'block';
            if (elements.reportsTableContainer) elements.reportsTableContainer.style.display = 'none';
            if (elements.otherReportsGridContainer) elements.otherReportsGridContainer.style.display = 'block';
            if (elements.otherReportsTableContainer) elements.otherReportsTableContainer.style.display = 'none';
        } else {
            if (elements.reportsGridContainer) elements.reportsGridContainer.style.display = 'none';
            if (elements.reportsTableContainer) elements.reportsTableContainer.style.display = 'block';
            if (elements.otherReportsGridContainer) elements.otherReportsGridContainer.style.display = 'none';
            if (elements.otherReportsTableContainer) elements.otherReportsTableContainer.style.display = 'block';
        }
    }

    // ========================================
    // Search Functions
    // ========================================

    function handleSearchInput(e) {
        const query = e.target.value.trim().toLowerCase();
        searchQuery = query;

        // Show/hide clear button
        if (elements.clearSearchBtn) {
            elements.clearSearchBtn.style.display = query.length > 0 ? 'flex' : 'none';
        }

        // Apply search filter
        applyFarmFilters();
    }

    function clearSearch() {
        searchQuery = '';
        if (elements.searchInput) {
            elements.searchInput.value = '';
        }
        if (elements.clearSearchBtn) {
            elements.clearSearchBtn.style.display = 'none';
        }
        if (elements.searchResultsCount) {
            elements.searchResultsCount.style.display = 'none';
        }
        if (elements.noSearchResults) {
            elements.noSearchResults.style.display = 'none';
        }

        // Re-render without filter
        applyFarmFilters();
    }

    function handleDiseaseFilterChange(e) {
        diseaseFilter = e.target.value;
        applySearchFilter();
    }

    function populateDiseaseFilterDropdown() {
        const select = elements.diseaseFilterSelect;
        if (!select) return;

        // Collect unique disease names from all reports
        const allDiseaseReports = [...allReports, ...allOtherReports];
        const uniqueDiseases = new Map();

        allDiseaseReports.forEach(report => {
            const diseaseName = report.effectiveDiseaseName || report.diseaseName;
            if (diseaseName && !uniqueDiseases.has(diseaseName)) {
                uniqueDiseases.set(diseaseName, diseaseName);
            }
        });

        // Sort diseases alphabetically
        const sortedDiseases = Array.from(uniqueDiseases.keys()).sort((a, b) =>
            a.localeCompare(b, undefined, { sensitivity: 'base' })
        );

        // Keep the first "All Diseases" option and add unique diseases
        select.innerHTML = '<option value="">All Diseases</option>';
        sortedDiseases.forEach(disease => {
            const option = document.createElement('option');
            option.value = disease;
            option.textContent = disease;
            // Restore selected value if it exists
            if (disease === diseaseFilter) {
                option.selected = true;
            }
            select.appendChild(option);
        });
    }

    function applySearchFilter() {
        // Filter reports based on search query and disease filter
        let myFiltered = [...allReports];
        let otherFiltered = [...allOtherReports];

        // Apply disease filter
        if (diseaseFilter) {
            myFiltered = myFiltered.filter(report => {
                const diseaseName = report.effectiveDiseaseName || report.diseaseName;
                return diseaseName === diseaseFilter;
            });
            otherFiltered = otherFiltered.filter(report => {
                const diseaseName = report.effectiveDiseaseName || report.diseaseName;
                return diseaseName === diseaseFilter;
            });
        }

        // Apply search query filter
        if (searchQuery.length > 0) {
            myFiltered = myFiltered.filter(report => matchesReportSearch(report, searchQuery));
            otherFiltered = otherFiltered.filter(report => matchesReportSearch(report, searchQuery));
        }

        filteredReports = myFiltered;
        filteredOtherReports = otherFiltered;

        // Re-render the current tab
        if (currentTab === 'my-reports') {
            renderFilteredReports();
        } else {
            renderFilteredOtherReports();
        }

        // Update search results count
        updateSearchResultsCount();
    }

    function matchesReportSearch(report, query) {
        const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
        const effectiveSeverity = report.effectiveSeverity || report.severity;

        const searchableFields = [
            effectiveDiseaseName,
            report.farmName,
            report.animalTypeName,
            effectiveSeverity,
            report.reportedByUsername,
            report.symptoms,
            report.diagnosis,
            report.treatment,
            report.outcome,
            report.farmDistrictDisplayName,
            report.farmProvinceDisplayName,
            report.farmDistrict,
            report.farmProvince
        ];

        return searchableFields.some(field => {
            if (field && typeof field === 'string') {
                return field.toLowerCase().includes(query);
            }
            return false;
        });
    }

    function renderFilteredReports() {
        if (elements.loadingState) elements.loadingState.style.display = 'none';

        // Show no results when filtering is active but no matches found
        if ((searchQuery.length > 0 || diseaseFilter) && filteredReports.length === 0) {
            showNoSearchResults();
            return;
        }

        if (filteredReports.length === 0 && allReports.length === 0) {
            if (elements.emptyState) elements.emptyState.style.display = 'block';
            return;
        }

        if (elements.emptyState) elements.emptyState.style.display = 'none';
        hideNoSearchResults();

        // Render both grid and table views
        renderReportsCards(filteredReports, searchQuery);
        renderReportsTableRows(filteredReports, searchQuery);
        updateViewDisplay();
    }

    function renderFilteredOtherReports() {
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'none';

        // Show no results when filtering is active but no matches found
        if ((searchQuery.length > 0 || diseaseFilter) && filteredOtherReports.length === 0) {
            showNoSearchResults();
            return;
        }

        if (filteredOtherReports.length === 0 && allOtherReports.length === 0) {
            if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'block';
            return;
        }

        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'none';
        hideNoSearchResults();

        // Render both grid and table views
        renderOtherReportsCards(filteredOtherReports, searchQuery);
        renderOtherReportsTableRows(filteredOtherReports, searchQuery);
        updateViewDisplay();
    }

    function updateSearchResultsCount() {
        if (!elements.searchResultsCount) return;

        // Show count only when filtering is active (search or disease filter)
        if (searchQuery.length === 0 && !diseaseFilter) {
            elements.searchResultsCount.style.display = 'none';
            return;
        }

        const count = currentTab === 'my-reports' ? filteredReports.length : filteredOtherReports.length;
        const total = currentTab === 'my-reports' ? allReports.length : allOtherReports.length;

        elements.searchResultsCount.innerHTML = `Found <strong>${count}</strong> of ${total} report${total !== 1 ? 's' : ''}`;
        elements.searchResultsCount.style.display = 'block';
    }

    function showNoSearchResults() {
        if (elements.noSearchResults) {
            elements.noSearchResults.style.display = 'flex';
        }
        if (elements.emptyState) elements.emptyState.style.display = 'none';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'none';
        // Hide farm card containers so no cards show underneath the "no results" message
        if (elements.farmsGridContainer) elements.farmsGridContainer.style.display = 'none';
        if (elements.farmsTableContainer) elements.farmsTableContainer.style.display = 'none';
        if (elements.otherFarmsGridContainer) elements.otherFarmsGridContainer.style.display = 'none';
        if (elements.otherFarmsTableContainer) elements.otherFarmsTableContainer.style.display = 'none';
        // Also clear legacy containers
        if (elements.reportsGridContainer) elements.reportsGridContainer.innerHTML = '';
        if (elements.reportsTableBody) elements.reportsTableBody.innerHTML = '';
        if (elements.otherReportsGridContainer) elements.otherReportsGridContainer.innerHTML = '';
        if (elements.otherReportsTableBody) elements.otherReportsTableBody.innerHTML = '';
    }

    function hideNoSearchResults() {
        if (elements.noSearchResults) {
            elements.noSearchResults.style.display = 'none';
        }
        // Restore farm card containers visibility
        if (elements.farmsGridContainer) elements.farmsGridContainer.style.display = 'block';
        if (elements.otherFarmsGridContainer) elements.otherFarmsGridContainer.style.display = 'block';
    }

    function highlightMatch(text, query) {
        if (!text || !query || query.length === 0) return escapeHtml(text || '');

        const escapedText = escapeHtml(text);
        const escapedQuery = escapeHtml(query);
        const regex = new RegExp(`(${escapedQuery.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
        return escapedText.replace(regex, '<span class="search-highlight">$1</span>');
    }

    function renderReportsCards(reports, query = '') {
        const container = elements.reportsGridContainer;
        if (!container) return;

        container.innerHTML = '';
        const cardsGrid = document.createElement('div');
        cardsGrid.className = 'reports-grid';

        reports.forEach(report => {
            const card = document.createElement('div');
            card.className = 'report-card';
            card.dataset.reportId = report.id;

            const date = new Date(report.reportDate).toLocaleDateString();
            const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
            const effectiveSeverity = report.effectiveSeverity || report.severity;
            const severityClass = effectiveSeverity?.toLowerCase() || '';
            const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
                ? report.effectiveNotifiable
                : report.isNotifiable;

            // Build location string for card title
            const locationParts = [];
            if (report.farmDistrictDisplayName) {
                locationParts.push(report.farmDistrictDisplayName);
            }
            if (report.farmProvinceDisplayName) {
                locationParts.push(report.farmProvinceDisplayName);
            }
            const farmLocation = locationParts.length > 0 ? locationParts.join(', ') : (report.farmName || 'Unknown Location');

            const diseaseNameDisplay = query ? highlightMatch(effectiveDiseaseName, query) : escapeHtml(effectiveDiseaseName);
            const farmLocationDisplay = query ? highlightMatch(farmLocation, query) : escapeHtml(farmLocation);
            const farmNameDisplay = query ? highlightMatch(report.farmName, query) : escapeHtml(report.farmName);
            const animalTypeDisplay = query ? highlightMatch(report.animalTypeName, query) : escapeHtml(report.animalTypeName);

            const imageHtml = report.imageUrl
                ? `<img src="${report.imageUrl}" alt="Disease Photo" class="report-card-image">`
                : `<div class="report-card-placeholder"><i data-lucide="bug" class="icon icon-xl icon-muted"></i><span class="placeholder-text">No Image</span></div>`;

            card.innerHTML = `
                <div class="report-card-header">
                    ${imageHtml}
                    <div class="report-card-badges">
                        <span class="severity-badge ${severityClass}">${effectiveSeverity || '-'}</span>
                    </div>
                </div>
                <div class="report-card-body">
                    <h4 class="report-card-title">${farmNameDisplay}</h4>
                    <p class="report-card-disease-name"><i data-lucide="bug" class="icon icon-xs"></i> ${diseaseNameDisplay}</p>
                    <p class="report-card-subtitle">${animalTypeDisplay} • ${farmLocationDisplay}</p>
                    <span class="report-card-date"><i data-lucide="calendar" class="icon icon-xs"></i> ${date}</span>
                    ${report.affectedCount ? `<span class="affected-count"><i data-lucide="paw-print" class="icon icon-xs"></i> ${report.affectedCount} affected</span>` : ''}
                </div>
                <div class="report-card-footer">
                    <button class="btn btn-outline btn-sm view-btn" data-report-id="${report.id}">
                        <i data-lucide="eye" class="icon icon-xs"></i> View Details
                    </button>
                    ${effectiveNotifiable ? '<span class="notifiable-badge notifiable-corner"><i data-lucide="alert-triangle" class="icon icon-xs"></i> Notifiable</span>' : ''}
                </div>
            `;

            if (typeof lucide !== 'undefined') lucide.createIcons({ nodes: [card] });

            card.querySelector('.view-btn').addEventListener('click', () => {
                isViewingOtherReport = false;
                openViewModal(report.id);
            });

            cardsGrid.appendChild(card);
        });

        container.appendChild(cardsGrid);
    }

    function renderReportsTableRows(reports, query = '') {
        const tbody = elements.reportsTableBody;
        if (!tbody) return;

        tbody.innerHTML = reports.map(report => {
            const date = new Date(report.reportDate).toLocaleDateString();
            const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
            const effectiveSeverity = report.effectiveSeverity || report.severity;
            const severityClass = effectiveSeverity?.toLowerCase() || '';
            const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
                ? report.effectiveNotifiable
                : report.isNotifiable;

            const diseaseNameDisplay = query ? highlightMatch(effectiveDiseaseName, query) : escapeHtml(effectiveDiseaseName || 'Unknown');
            const farmNameDisplay = query ? highlightMatch(report.farmName, query) : escapeHtml(report.farmName || '-');
            const animalTypeDisplay = query ? highlightMatch(report.animalTypeName, query) : escapeHtml(report.animalTypeName || '-');

            return `
                <tr data-report-id="${report.id}">
                    <td><span class="table-disease-name">${diseaseNameDisplay}</span></td>
                    <td>${farmNameDisplay}</td>
                    <td>${animalTypeDisplay}</td>
                    <td>${date}</td>
                    <td><span class="severity-badge ${severityClass}">${effectiveSeverity || '-'}</span></td>
                    <td>${effectiveNotifiable ? '<span class="notifiable-badge-small"><i data-lucide="alert-triangle" class="icon icon-xs"></i> Yes</span>' : 'No'}</td>
                    <td>${report.affectedCount || '-'}</td>
                    <td>
                        <div class="table-actions">
                            <button class="btn btn-outline btn-sm view-btn" data-id="${report.id}">
                                <i data-lucide="eye" class="icon icon-xs"></i> View
                            </button>
                            <button class="btn btn-outline btn-sm edit-btn" data-id="${report.id}">
                                <i data-lucide="pencil" class="icon icon-xs"></i> Edit
                            </button>
                            <button class="btn btn-danger-outline btn-sm delete-btn" data-id="${report.id}">
                                <i data-lucide="trash-2" class="icon icon-xs"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
        if (typeof lucide !== 'undefined') lucide.createIcons({ nodes: [tbody] });

        // Add event listeners
        tbody.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                isViewingOtherReport = false;
                openViewModal(btn.dataset.id);
            });
        });
        tbody.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => editReport(btn.dataset.id));
        });
        tbody.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => openDeleteModal(btn.dataset.id));
        });
    }

    function renderOtherReportsCards(reports, query = '') {
        const container = elements.otherReportsGridContainer;
        if (!container) return;

        container.innerHTML = '';
        const cardsGrid = document.createElement('div');
        cardsGrid.className = 'reports-grid';

        reports.forEach(report => {
            const card = document.createElement('div');
            card.className = 'report-card other-vet-card';
            card.dataset.reportId = report.id;

            const date = new Date(report.reportDate).toLocaleDateString();
            const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
            const effectiveSeverity = report.effectiveSeverity || report.severity;
            const severityClass = effectiveSeverity?.toLowerCase() || '';
            const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
                ? report.effectiveNotifiable
                : report.isNotifiable;

            // Build location string for card title
            const locationParts = [];
            if (report.farmDistrictDisplayName) {
                locationParts.push(report.farmDistrictDisplayName);
            }
            if (report.farmProvinceDisplayName) {
                locationParts.push(report.farmProvinceDisplayName);
            }
            const farmLocation = locationParts.length > 0 ? locationParts.join(', ') : (report.farmName || 'Unknown Location');

            const diseaseNameDisplay = query ? highlightMatch(effectiveDiseaseName, query) : escapeHtml(effectiveDiseaseName);
            const farmLocationDisplay = query ? highlightMatch(farmLocation, query) : escapeHtml(farmLocation);
            const farmNameDisplay = query ? highlightMatch(report.farmName, query) : escapeHtml(report.farmName);
            const animalTypeDisplay = query ? highlightMatch(report.animalTypeName, query) : escapeHtml(report.animalTypeName);
            const vetNameDisplay = query ? highlightMatch(report.reportedByUsername, query) : escapeHtml(report.reportedByUsername);

            const imageHtml = report.imageUrl
                ? `<img src="${report.imageUrl}" alt="Disease Photo" class="report-card-image">`
                : `<div class="report-card-placeholder"><i data-lucide="bug" class="icon icon-xl icon-muted"></i><span class="placeholder-text">No Image</span></div>`;

            card.innerHTML = `
                <div class="report-card-header">
                    ${imageHtml}
                    <div class="report-card-badges">
                        <span class="severity-badge ${severityClass}">${effectiveSeverity || '-'}</span>
                    </div>
                </div>
                <div class="report-card-body">
                    <h4 class="report-card-title">${farmNameDisplay}</h4>
                    <p class="report-card-disease-name"><i data-lucide="bug" class="icon icon-xs"></i> ${diseaseNameDisplay}</p>
                    <p class="report-card-subtitle">${animalTypeDisplay} • ${farmLocationDisplay}</p>
                    <div class="report-card-meta">
                        <span class="meta-item"><i data-lucide="calendar" class="icon icon-xs"></i> ${date}</span>
                        <span class="meta-item"><i data-lucide="user" class="icon icon-xs"></i> ${vetNameDisplay}</span>
                    </div>
                    ${report.affectedCount ? `<span class="affected-count"><i data-lucide="paw-print" class="icon icon-xs"></i> ${report.affectedCount} affected</span>` : ''}
                </div>
                <div class="report-card-footer">
                    <button class="btn btn-outline btn-sm view-btn" data-report-id="${report.id}">
                        <i data-lucide="eye" class="icon icon-xs"></i> View Details
                    </button>
                    ${effectiveNotifiable ? '<span class="notifiable-badge notifiable-corner"><i data-lucide="alert-triangle" class="icon icon-xs"></i> Notifiable</span>' : ''}
                </div>
            `;

            if (typeof lucide !== 'undefined') lucide.createIcons({ nodes: [card] });

            card.querySelector('.view-btn').addEventListener('click', () => {
                isViewingOtherReport = true;
                openViewModal(report.id);
            });

            cardsGrid.appendChild(card);
        });

        container.appendChild(cardsGrid);
    }

    function renderOtherReportsTableRows(reports, query = '') {
        const tbody = elements.otherReportsTableBody;
        if (!tbody) return;

        tbody.innerHTML = reports.map(report => {
            const date = new Date(report.reportDate).toLocaleDateString();
            const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
            const effectiveSeverity = report.effectiveSeverity || report.severity;
            const severityClass = effectiveSeverity?.toLowerCase() || '';
            const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
                ? report.effectiveNotifiable
                : report.isNotifiable;

            const diseaseNameDisplay = query ? highlightMatch(effectiveDiseaseName, query) : escapeHtml(effectiveDiseaseName || 'Unknown');
            const farmNameDisplay = query ? highlightMatch(report.farmName, query) : escapeHtml(report.farmName || '-');
            const animalTypeDisplay = query ? highlightMatch(report.animalTypeName, query) : escapeHtml(report.animalTypeName || '-');
            const vetNameDisplay = query ? highlightMatch(report.reportedByUsername, query) : escapeHtml(report.reportedByUsername || 'Unknown');

            return `
                <tr data-report-id="${report.id}">
                    <td><span class="table-disease-name">${diseaseNameDisplay}</span></td>
                    <td>${farmNameDisplay}</td>
                    <td>${animalTypeDisplay}</td>
                    <td>${date}</td>
                    <td><span class="severity-badge ${severityClass}">${effectiveSeverity || '-'}</span></td>
                    <td>${effectiveNotifiable ? '<span class="notifiable-badge-small"><i data-lucide="alert-triangle" class="icon icon-xs"></i> Yes</span>' : 'No'}</td>
                    <td>${report.affectedCount || '-'}</td>
                    <td>
                        <div class="table-vet-name">
                            <i data-lucide="stethoscope" class="icon icon-xs"></i>
                            <span>${vetNameDisplay}</span>
                        </div>
                    </td>
                    <td>
                        <div class="table-actions">
                            <button class="btn btn-outline btn-sm view-btn" data-id="${report.id}">
                                <i data-lucide="eye" class="icon icon-xs"></i> View
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
        if (typeof lucide !== 'undefined') lucide.createIcons({ nodes: [tbody] });

        // Add event listeners
        tbody.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                isViewingOtherReport = true;
                openViewModal(btn.dataset.id);
            });
        });
    }

    function handleUrlParams() {
        const urlParams = new URLSearchParams(window.location.search);
        const tab = urlParams.get('tab');
        const reportId = urlParams.get('reportId') || urlParams.get('viewReport');

        // Switch to other reports tab if specified
        if (tab === 'others') {
            switchTab('other-reports');
        }

        // Open view modal for specific report
        if (reportId) {
            // Determine if this is viewing other vet's report
            isViewingOtherReport = (tab === 'others');

            // Wait a bit for page to load, then open view modal
            setTimeout(() => {
                openViewModal(reportId);
            }, 500);
        }
    }

    async function loadOtherReports() {
        showOtherLoadingState();

        try {
            const reports = await fetchAPI('/api/vet/disease-reports/others');

            // Store all other reports for search
            allOtherReports = reports;
            filteredOtherReports = [...reports];

            // Re-populate disease filter dropdown to include diseases from other reports
            populateDiseaseFilterDropdown();

            if (reports.length === 0) {
                showOtherEmptyState();
            } else {
                // Apply any existing search/disease filter
                if (searchQuery.length > 0 || diseaseFilter) {
                    applySearchFilter();
                } else {
                    renderOtherReportsTable(reports);
                    showOtherReportsTable();
                }
            }
            otherReportsLoaded = true;
        } catch (error) {
            console.error('Error loading other reports:', error);
            showOtherEmptyState();
        }
    }

    function showOtherLoadingState() {
        if (elements.otherReportsGridContainer) elements.otherReportsGridContainer.innerHTML = '';
        if (elements.otherReportsTableContainer) elements.otherReportsTableContainer.style.display = 'none';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'none';
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'block';
    }

    function showOtherEmptyState() {
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'none';
        if (elements.otherReportsGridContainer) elements.otherReportsGridContainer.style.display = 'none';
        if (elements.otherReportsTableContainer) elements.otherReportsTableContainer.style.display = 'none';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'block';
    }

    function showOtherReportsTable() {
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'none';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'none';
        updateViewDisplay();
    }

    function renderOtherReportsTable(reports) {
        // Render grid view
        const cardsGrid = document.createElement('div');
        cardsGrid.className = 'reports-cards-grid';

        reports.forEach(report => {
            const card = document.createElement('div');
            card.className = 'report-card other-vet-card';

            const date = new Date(report.reportDate).toLocaleDateString();
            // Use effective values (overrides applied)
            const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
            const effectiveSeverity = report.effectiveSeverity || report.severity;
            const severityClass = effectiveSeverity?.toLowerCase() || '';
            const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
                ? report.effectiveNotifiable
                : report.isNotifiable;

            // Build location string for card title
            const locationParts = [];
            if (report.farmDistrictDisplayName) {
                locationParts.push(report.farmDistrictDisplayName);
            }
            if (report.farmProvinceDisplayName) {
                locationParts.push(report.farmProvinceDisplayName);
            }
            const farmLocation = locationParts.length > 0 ? locationParts.join(', ') : (report.farmName || 'Unknown Location');

            const imageHtml = report.imageUrl
                ? `<img src="${report.imageUrl}" alt="Disease Photo" class="report-card-image">`
                : `<div class="report-card-placeholder"><i data-lucide="bug" class="icon icon-xl icon-muted"></i><span class="placeholder-text">No Image</span></div>`;

            card.innerHTML = `
                <div class="report-card-header">
                    ${imageHtml}
                    <div class="report-card-badges">
                        <span class="severity-badge ${severityClass}">${effectiveSeverity || '-'}</span>
                    </div>
                </div>
                <div class="report-card-body">
                    <h4 class="report-card-title">${escapeHtml(report.farmName) || 'Unknown Farm'}</h4>
                    <p class="report-card-disease-name"><i data-lucide="bug" class="icon icon-xs"></i> ${escapeHtml(effectiveDiseaseName)}</p>
                    <p class="report-card-subtitle">${escapeHtml(report.animalTypeName)} • ${escapeHtml(farmLocation)}</p>
                    <div class="report-card-meta">
                        <span class="meta-item"><i data-lucide="calendar" class="icon icon-xs"></i> ${date}</span>
                        <span class="meta-item"><i data-lucide="user" class="icon icon-xs"></i> ${escapeHtml(report.reportedByUsername)}</span>
                    </div>
                    ${report.affectedCount ? `<span class="affected-count"><i data-lucide="paw-print" class="icon icon-xs"></i> ${report.affectedCount} affected</span>` : ''}
                </div>
                <div class="report-card-footer">
                    <button class="btn btn-outline btn-sm view-btn" data-report-id="${report.id}">
                        <i data-lucide="eye" class="icon icon-xs"></i> View Details
                    </button>
                    ${effectiveNotifiable ? '<span class="notifiable-badge notifiable-corner"><i data-lucide="alert-triangle" class="icon icon-xs"></i> Notifiable</span>' : ''}
                </div>
            `;

            if (typeof lucide !== 'undefined') lucide.createIcons({ nodes: [card] });

            // Add click listener for view (no edit/delete for other vets' reports)
            card.querySelector('.view-btn').addEventListener('click', () => {
                isViewingOtherReport = true;
                openViewModal(report.id);
            });

            cardsGrid.appendChild(card);
        });

        // Clear and populate grid container
        if (elements.otherReportsGridContainer) {
            elements.otherReportsGridContainer.innerHTML = '';
            elements.otherReportsGridContainer.appendChild(cardsGrid);
        }

        // Render table view
        if (elements.otherReportsTableBody) {
            elements.otherReportsTableBody.innerHTML = reports.map(report => createOtherReportTableRow(report)).join('');
            // Add click events for table view buttons
            elements.otherReportsTableBody.querySelectorAll('.view-btn').forEach(btn => {
                btn.addEventListener('click', () => {
                    isViewingOtherReport = true;
                    openViewModal(btn.dataset.id);
                });
            });
        }

        // Apply current view mode
        updateViewDisplay();
    }

    /**
     * Create table row HTML for other vet's report (view-only)
     */
    function createOtherReportTableRow(report) {
        const date = new Date(report.reportDate).toLocaleDateString();
        // Use effective values (overrides applied)
        const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
        const effectiveSeverity = report.effectiveSeverity || report.severity;
        const severityClass = effectiveSeverity?.toLowerCase() || '';
        const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
            ? report.effectiveNotifiable
            : report.isNotifiable;

        return `
            <tr data-report-id="${report.id}">
                <td><span class="table-disease-name">${escapeHtml(effectiveDiseaseName || 'Unknown')}</span></td>
                <td>${escapeHtml(report.farmName || '-')}</td>
                <td>${escapeHtml(report.animalTypeName || '-')}</td>
                <td>${date}</td>
                <td><span class="severity-badge ${severityClass}">${effectiveSeverity || '-'}</span></td>
                <td>${effectiveNotifiable ? '<span class="notifiable-badge-small"><i data-lucide="alert-triangle" class="icon icon-xs"></i> Yes</span>' : 'No'}</td>
                <td>${report.affectedCount || '-'}</td>
                <td>
                    <div class="table-vet-name">
                        <i data-lucide="stethoscope" class="icon icon-xs"></i>
                        <span>${escapeHtml(report.reportedByUsername || 'Unknown')}</span>
                    </div>
                </td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-outline btn-sm view-btn" data-id="${report.id}">
                            <i data-lucide="eye" class="icon icon-xs"></i> View
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }

    // ========================================
    // Report Modal Functions
    // ========================================


    function openModal(isEdit = false, reportData = null) {
        editMode = isEdit;
        currentReportId = reportData?.id || null;

        // Always hide any previous form messages when opening modal
        hideFormMessage();

        if (isEdit && reportData) {
            elements.reportModalTitle.textContent = 'Edit Disease Report';
            elements.submitBtn.querySelector('.btn-text').textContent = 'Update Report';
            elements.editReportId.value = reportData.id;
            populateEditForm(reportData);
        } else {
            elements.reportModalTitle.textContent = 'New Disease Report';
            elements.submitBtn.querySelector('.btn-text').textContent = 'Submit Report';
            elements.editReportId.value = '';
            resetForm();
        }

        elements.reportModal?.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    function closeModal() {
        elements.reportModal?.classList.add('hidden');
        document.body.style.overflow = '';
        editMode = false;
        currentReportId = null;
        clearExistingImage = false;
    }

    function resetForm() {
        elements.form?.reset();
        setDefaultDate();

        // Reset province/district filters
        elements.filterProvinceSelect.value = '';
        elements.filterDistrictSelect.innerHTML = '<option value="">Select province first</option>';
        elements.filterDistrictSelect.disabled = true;

        // Repopulate farm dropdown with all farms
        populateFarmDropdown();

        elements.animalTypeSelect.disabled = true;
        elements.animalTypeSelect.innerHTML = '<option value="">Select farm first...</option>';
        elements.diseaseSelect.disabled = true;
        elements.diseaseSelect.innerHTML = '<option value="">Select animal type first...</option>';
        elements.diseaseInfoSection.style.display = 'none';
        resetDiseaseInfoEditMode();
        hideOtherDiseaseSection(); // Reset "Other" disease section
        removeImage();
        hideFormMessage();
        currentDiseaseData = null;
        clearExistingImage = false;
        // Refresh CustomSelect dropdowns to reflect reset state
        if (window.CustomSelect) {
            CustomSelect.refresh('filterProvince');
            CustomSelect.refresh('filterDistrict');
            CustomSelect.refresh('farmId');
            CustomSelect.refresh('animalTypeId');
            CustomSelect.refresh('diseaseId');
            CustomSelect.refresh('outcome');
            CustomSelect.refresh('editSeverity');
        }
    }

    async function populateEditForm(report) {
        // Store values we need to set after async operations
        const farmId = report.farmId || '';
        const animalTypeId = report.animalTypeId || '';
        const diseaseId = report.diseaseId || '';

        // Find the farm to get its province/district
        const farm = farms.find(f => f.id === farmId);
        if (farm) {
            // Set province filter and load its districts
            elements.filterProvinceSelect.value = farm.province || '';
            if (farm.province) {
                await onProvinceFilterChange();
                // Set district filter
                elements.filterDistrictSelect.value = farm.district || '';
                onDistrictFilterChange();
            }
        }

        // Set farm and trigger cascade
        elements.farmSelect.value = farmId;
        await onFarmChange();

        // Set animal type after farm change completes and trigger cascade
        elements.animalTypeSelect.value = animalTypeId;
        await onAnimalTypeChange();

        // Set disease after animal type change completes
        elements.diseaseSelect.value = diseaseId;
        onDiseaseChange();

        // Set all other form values AFTER the cascades complete
        document.getElementById('affectedCount').value = report.affectedCount || '';
        elements.reportDate.value = report.reportDate || '';
        document.getElementById('outcome').value = report.outcome || 'ONGOING';

        // These should not be overwritten by onDiseaseChange since editMode is true
        elements.symptoms.value = report.symptoms || '';
        elements.treatment.value = report.treatment || '';
        document.getElementById('diagnosis').value = report.diagnosis || '';
        document.getElementById('notes').value = report.notes || '';

        // Populate override fields if they exist
        if (report.overrideDiseaseName || report.overrideSeverity || report.overrideNotifiable !== null || report.overrideDescription) {
            // Show disease info section and enter edit mode
            if (elements.diseaseInfoSection) {
                elements.diseaseInfoSection.style.display = 'block';
            }
            // Populate override fields
            if (elements.editDiseaseName) {
                elements.editDiseaseName.value = report.overrideDiseaseName || '';
            }
            if (elements.editSeverity) {
                elements.editSeverity.value = report.overrideSeverity || '';
                if (window.CustomSelect) {
                    CustomSelect.refresh('editSeverity');
                }
            }
            if (elements.editNotifiable) {
                elements.editNotifiable.value = report.overrideNotifiable !== null ? String(report.overrideNotifiable) : '';
                if (window.CustomSelect) {
                    CustomSelect.refresh('editNotifiable');
                }
            }
            if (elements.editDiseaseDescription) {
                elements.editDiseaseDescription.value = report.overrideDescription || '';
            }

            // Update the display with effective values (show overridden values in display mode)
            updateDiseaseInfoDisplay();

            // Keep in display mode but show that overrides are applied
            isDiseaseInfoEditMode = false;
            if (elements.diseaseInfoDisplay) elements.diseaseInfoDisplay.style.display = 'block';
            if (elements.diseaseInfoEdit) elements.diseaseInfoEdit.style.display = 'none';
            if (elements.editDiseaseInfoLabel) elements.editDiseaseInfoLabel.innerHTML = '<i data-lucide="pencil" class="icon icon-xs"></i> Edit Info';
            if (typeof lucide !== 'undefined') lucide.createIcons();
        }

        // Handle existing image
        clearExistingImage = false; // Reset the clear flag
        selectedImageFile = null; // Reset any selected file
        if (report.imageUrl) {
            elements.imagePreview.src = report.imageUrl;
            elements.imageDropzone.style.display = 'none';
            elements.imagePreviewContainer.style.display = 'inline-block';
        } else {
            elements.imagePreview.src = '';
            elements.imageDropzone.style.display = 'block';
            elements.imagePreviewContainer.style.display = 'none';
        }
    }

    function toggleDiseaseInfoEditMode() {
        isDiseaseInfoEditMode = !isDiseaseInfoEditMode;

        if (isDiseaseInfoEditMode) {
            elements.diseaseInfoDisplay.style.display = 'none';
            elements.diseaseInfoEdit.style.display = 'block';
            elements.editDiseaseInfoLabel.textContent = '✓ Done Editing';

            // Pre-fill with current override values or original values
            if (currentDiseaseData) {
                // If override fields already have values, keep them; otherwise use original
                if (!elements.editDiseaseName.value) {
                    elements.editDiseaseName.value = currentDiseaseData.diseaseName || '';
                }
                // Keep existing severity selection if set
                if (!elements.editSeverity.value) {
                    elements.editSeverity.value = '';
                }
                // Keep existing description if set
                if (!elements.editDiseaseDescription.value) {
                    elements.editDiseaseDescription.value = '';
                }
            }
        } else {
            // Switching back to display mode - update display with effective values
            updateDiseaseInfoDisplay();
            elements.diseaseInfoDisplay.style.display = 'block';
            elements.diseaseInfoEdit.style.display = 'none';
            elements.editDiseaseInfoLabel.innerHTML = '<i data-lucide="pencil" class="icon icon-xs"></i> Edit Info';
            if (typeof lucide !== 'undefined') lucide.createIcons();
        }
    }

    /**
     * Update the disease info display with effective values (overrides applied)
     */
    function updateDiseaseInfoDisplay() {
        if (!currentDiseaseData) return;

        // Get override values from edit fields
        const overrideName = elements.editDiseaseName?.value?.trim() || '';
        const overrideSeverity = elements.editSeverity?.value || '';
        const overrideNotifiable = elements.editNotifiable?.value || '';
        const overrideDescription = elements.editDiseaseDescription?.value?.trim() || '';

        // Calculate effective values (use override if set, otherwise use original)
        const effectiveName = overrideName || currentDiseaseData.diseaseName || '-';
        const effectiveSeverity = overrideSeverity || currentDiseaseData.severity || '-';
        const effectiveNotifiable = overrideNotifiable !== ''
            ? (overrideNotifiable === 'true')
            : currentDiseaseData.isNotifiable;
        const effectiveDescription = overrideDescription || currentDiseaseData.description || '-';

        // Update display elements
        if (elements.displayDiseaseName) {
            elements.displayDiseaseName.textContent = effectiveName;
            // Add visual indicator if overridden
            if (overrideName && overrideName !== currentDiseaseData.diseaseName) {
                elements.displayDiseaseName.classList.add('overridden');
            } else {
                elements.displayDiseaseName.classList.remove('overridden');
            }
        }

        if (elements.displaySeverity) {
            elements.displaySeverity.textContent = effectiveSeverity;
            // Update severity badge class
            elements.displaySeverity.className = 'info-value severity-badge';
            if (effectiveSeverity && effectiveSeverity !== '-') {
                elements.displaySeverity.classList.add(effectiveSeverity.toLowerCase());
            }
            // Add visual indicator if overridden
            if (overrideSeverity && overrideSeverity !== currentDiseaseData.severity) {
                elements.displaySeverity.classList.add('overridden');
            }
        }

        if (elements.displayNotifiable) {
            elements.displayNotifiable.textContent = effectiveNotifiable ? 'Yes' : 'No';
            // Add visual indicator if overridden
            if (overrideNotifiable !== '' && (overrideNotifiable === 'true') !== currentDiseaseData.isNotifiable) {
                elements.displayNotifiable.classList.add('overridden');
            } else {
                elements.displayNotifiable.classList.remove('overridden');
            }
        }

        if (elements.displayDescription) {
            elements.displayDescription.textContent = effectiveDescription;
            // Add visual indicator if overridden
            if (overrideDescription && overrideDescription !== currentDiseaseData.description) {
                elements.displayDescription.classList.add('overridden');
            } else {
                elements.displayDescription.classList.remove('overridden');
            }
        }
    }

    function resetDiseaseInfoEditMode() {
        isDiseaseInfoEditMode = false;
        if (elements.diseaseInfoDisplay) elements.diseaseInfoDisplay.style.display = 'block';
        if (elements.diseaseInfoEdit) elements.diseaseInfoEdit.style.display = 'none';
        if (elements.editDiseaseInfoLabel) {
            elements.editDiseaseInfoLabel.innerHTML = '<i data-lucide="pencil" class="icon icon-xs"></i> Edit Info';
            if (typeof lucide !== 'undefined') lucide.createIcons();
        }
        if (elements.editDiseaseName) elements.editDiseaseName.value = '';
        if (elements.editSeverity) elements.editSeverity.value = '';
        if (elements.editNotifiable) elements.editNotifiable.value = '';
        if (elements.editDiseaseDescription) elements.editDiseaseDescription.value = '';
    }

    // ========================================
    // Other Disease Functions
    // ========================================

    /**
     * Show the "Other Disease" form section and initialize CustomSelect for its dropdowns
     */
    function showOtherDiseaseSection() {
        if (elements.otherDiseaseSection) {
            elements.otherDiseaseSection.style.display = 'block';

            // Initialize CustomSelect for the new disease form dropdowns
            if (window.CustomSelect) {
                CustomSelect.refresh('newDiseaseSeverity');
                CustomSelect.refresh('newDiseaseNotifiable');
            }
        }
    }

    /**
     * Hide the "Other Disease" form section and reset its fields
     */
    function hideOtherDiseaseSection() {
        isOtherDiseaseSelected = false;
        if (elements.otherDiseaseSection) {
            elements.otherDiseaseSection.style.display = 'none';
        }
        // Reset the fields
        if (elements.newDiseaseName) elements.newDiseaseName.value = '';
        if (elements.newDiseaseCode) elements.newDiseaseCode.value = '';
        if (elements.newDiseaseSeverity) {
            elements.newDiseaseSeverity.value = '';
            if (window.CustomSelect) {
                CustomSelect.refresh('newDiseaseSeverity');
            }
        }
        if (elements.newDiseaseNotifiable) {
            elements.newDiseaseNotifiable.value = '';
            if (window.CustomSelect) {
                CustomSelect.refresh('newDiseaseNotifiable');
            }
        }
        if (elements.newDiseaseDescription) elements.newDiseaseDescription.value = '';
    }

    /**
     * Validate the "Other Disease" form fields
     * @returns {boolean} True if valid, false otherwise
     */
    function validateOtherDiseaseForm() {
        let isValid = true;

        // Clear previous errors
        const errorFields = ['newDiseaseName', 'newDiseaseSeverity', 'newDiseaseNotifiable'];
        errorFields.forEach(field => {
            const errorEl = document.getElementById(`${field}-error`);
            if (errorEl) errorEl.textContent = '';
        });

        // Validate disease name
        const diseaseName = elements.newDiseaseName?.value?.trim();
        if (!diseaseName) {
            const errorEl = document.getElementById('newDiseaseName-error');
            if (errorEl) errorEl.textContent = 'Disease name is required';
            isValid = false;
        } else if (diseaseName.length < 2) {
            const errorEl = document.getElementById('newDiseaseName-error');
            if (errorEl) errorEl.textContent = 'Disease name must be at least 2 characters';
            isValid = false;
        }

        // Validate severity
        const severity = elements.newDiseaseSeverity?.value;
        if (!severity) {
            const errorEl = document.getElementById('newDiseaseSeverity-error');
            if (errorEl) errorEl.textContent = 'Severity is required';
            isValid = false;
        }

        // Validate notifiable status
        const notifiable = elements.newDiseaseNotifiable?.value;
        if (notifiable === '' || notifiable === null || notifiable === undefined) {
            const errorEl = document.getElementById('newDiseaseNotifiable-error');
            if (errorEl) errorEl.textContent = 'Notifiable status is required';
            isValid = false;
        }

        return isValid;
    }

    // ========================================
    // View Modal Functions
    // ========================================

    function openViewModal(reportId) {
        elements.viewModal?.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
        elements.viewLoading.style.display = 'block';
        elements.viewContent.style.display = 'none';

        loadReportDetails(reportId);
    }

    function closeViewModal() {
        elements.viewModal?.classList.add('hidden');
        document.body.style.overflow = '';
        currentViewReport = null;
    }

    async function loadReportDetails(reportId) {
        try {
            const report = await fetchAPI(`/api/vet/disease-reports/${reportId}`);
            currentViewReport = report;
            displayReportDetails(report);
        } catch (error) {
            console.error('Error loading report:', error);
            closeViewModal();
            alert('Failed to load report details.');
        }
    }

    function displayReportDetails(report) {
        // Photo Card
        if (report.imageUrl) {
            elements.viewPhoto.src = report.imageUrl;
            elements.viewPhotoCard.style.display = 'block';
        } else {
            elements.viewPhotoCard.style.display = 'none';
        }

        // Location & Animal
        document.getElementById('view-farmName').textContent = report.farmName || '-';
        document.getElementById('view-farmAddress').textContent = report.farmAddress || '-';
        document.getElementById('view-animalType').textContent = report.animalTypeName || '-';
        document.getElementById('view-affectedCount').textContent = report.affectedCount || '-';

        // Disease Information - use effective values (overrides applied)
        document.getElementById('view-diseaseName').textContent = report.effectiveDiseaseName || report.diseaseName || '-';
        document.getElementById('view-diseaseCode').textContent = report.diseaseCode || '-';

        const severityEl = document.getElementById('view-severity');
        const effectiveSeverity = report.effectiveSeverity || report.severity;
        if (effectiveSeverity) {
            severityEl.innerHTML = `<span class="severity-badge ${effectiveSeverity.toLowerCase()}">${effectiveSeverity}</span>`;
        } else {
            severityEl.textContent = '-';
        }

        // Use effective notifiable value (override applied)
        const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
            ? report.effectiveNotifiable
            : report.isNotifiable;
        document.getElementById('view-notifiable').textContent = effectiveNotifiable ? 'Yes' : 'No';

        // Report Details
        document.getElementById('view-reportDate').textContent = report.reportDate
            ? new Date(report.reportDate).toLocaleDateString()
            : '-';

        const outcomeEl = document.getElementById('view-outcome');
        if (report.outcome) {
            outcomeEl.innerHTML = `<span class="outcome-badge ${report.outcome.toLowerCase()}">${report.outcome}</span>`;
        } else {
            outcomeEl.textContent = '-';
        }

        document.getElementById('view-reportedBy').textContent = report.reportedByUsername || '-';

        // Text sections
        const symptomsSection = document.getElementById('view-symptomsSection');
        const symptomsText = document.getElementById('view-symptoms');
        if (report.symptoms) {
            symptomsText.textContent = report.symptoms;
            symptomsSection.style.display = 'block';
        } else {
            symptomsSection.style.display = 'none';
        }

        const diagnosisSection = document.getElementById('view-diagnosisSection');
        const diagnosisText = document.getElementById('view-diagnosis');
        if (report.diagnosis) {
            diagnosisText.textContent = report.diagnosis;
            diagnosisSection.style.display = 'block';
        } else {
            diagnosisSection.style.display = 'none';
        }

        const treatmentSection = document.getElementById('view-treatmentSection');
        const treatmentText = document.getElementById('view-treatment');
        if (report.treatment) {
            treatmentText.textContent = report.treatment;
            treatmentSection.style.display = 'block';
        } else {
            treatmentSection.style.display = 'none';
        }

        const notesSection = document.getElementById('view-notesSection');
        const notesText = document.getElementById('view-notes');
        if (report.notes) {
            notesText.textContent = report.notes;
            notesSection.style.display = 'block';
        } else {
            notesSection.style.display = 'none';
        }

        // Determine if this is the current user's report
        const currentUsername = window.currentUsername || '';
        const isMyReport = report.reportedByUsername === currentUsername;

        // Show/hide Edit and Delete buttons based on ownership
        if (elements.editReportBtn) {
            elements.editReportBtn.style.display = isMyReport ? 'inline-flex' : 'none';
        }
        if (elements.deleteReportBtn) {
            elements.deleteReportBtn.style.display = isMyReport ? 'inline-flex' : 'none';
        }

        // Show content
        elements.viewLoading.style.display = 'none';
        elements.viewContent.style.display = 'block';
    }


    function onEditReport() {
        if (currentViewReport) {
            // Save the report data before closing the modal (which resets currentViewReport to null)
            const reportToEdit = { ...currentViewReport };
            closeViewModal();
            openModal(true, reportToEdit);
        }
    }

    function onDeleteReport() {
        if (currentViewReport) {
            elements.deleteReportId.value = currentViewReport.id;
            closeViewModal();
            openDeleteModal();
        }
    }

    // ========================================
    // Delete Modal Functions
    // ========================================

    function openDeleteModal() {
        elements.deleteModal?.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    function closeDeleteModal() {
        elements.deleteModal?.classList.add('hidden');
        document.body.style.overflow = '';
    }

    async function confirmDelete() {
        const reportId = elements.deleteReportId.value;
        if (!reportId) return;

        setDeleteLoading(true);

        try {
            const headers = {};
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            const response = await fetch(`/api/vet/disease-reports/${reportId}`, {
                method: 'DELETE',
                headers: headers
            });

            if (!response.ok) {
                throw new Error('Failed to delete report');
            }

            closeDeleteModal();
            await loadReports();

        } catch (error) {
            console.error('Error deleting report:', error);
            alert('Failed to delete report. Please try again.');
        } finally {
            setDeleteLoading(false);
        }
    }

    function setDeleteLoading(loading) {
        const btnText = elements.confirmDeleteBtn.querySelector('.btn-text');
        const btnLoading = elements.confirmDeleteBtn.querySelector('.btn-loading');

        if (loading) {
            btnText.style.display = 'none';
            btnLoading.style.display = 'inline';
            elements.confirmDeleteBtn.disabled = true;
        } else {
            btnText.style.display = 'inline';
            btnLoading.style.display = 'none';
            elements.confirmDeleteBtn.disabled = false;
        }
    }

    // ========================================
    // API Functions
    // ========================================

    async function fetchAPI(url, options = {}) {
        const headers = {
            ...options.headers
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        const response = await fetch(url, { ...options, headers });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Request failed' }));
            throw new Error(error.message || `HTTP error! status: ${response.status}`);
        }

        return response.json();
    }

    // ========================================
    // Province/District Filter Functions
    // ========================================

    async function loadProvinces() {
        try {
            const provinces = await fetchAPI('/api/locations/provinces');
            elements.filterProvinceSelect.innerHTML = '<option value="">All provinces</option>';

            provinces.forEach(province => {
                const option = document.createElement('option');
                option.value = province.value;
                option.textContent = province.label;
                elements.filterProvinceSelect.appendChild(option);
            });

            if (window.CustomSelect) {
                CustomSelect.refresh('filterProvince');
            }
        } catch (error) {
            console.error('Error loading provinces:', error);
        }
    }

    async function onProvinceFilterChange() {
        const provinceValue = elements.filterProvinceSelect.value;

        // Reset district dropdown
        elements.filterDistrictSelect.innerHTML = '<option value="">All districts</option>';

        if (!provinceValue) {
            elements.filterDistrictSelect.disabled = true;
            if (window.CustomSelect) {
                CustomSelect.refresh('filterDistrict');
            }
            // Show all farms
            filterFarmsByLocation('', '');
            return;
        }

        // Load districts for selected province
        try {
            const districts = await fetchAPI(`/api/locations/districts?provinceName=${provinceValue}`);
            elements.filterDistrictSelect.disabled = false;

            districts.forEach(district => {
                const option = document.createElement('option');
                option.value = district.value;
                option.textContent = district.label;
                elements.filterDistrictSelect.appendChild(option);
            });

            if (window.CustomSelect) {
                CustomSelect.refresh('filterDistrict');
            }

            // Filter farms by province only
            filterFarmsByLocation(provinceValue, '');
        } catch (error) {
            console.error('Error loading districts:', error);
        }
    }

    function onDistrictFilterChange() {
        const provinceValue = elements.filterProvinceSelect.value;
        const districtValue = elements.filterDistrictSelect.value;
        filterFarmsByLocation(provinceValue, districtValue);
    }

    function filterFarmsByLocation(province, district) {
        // Filter the farms array based on province/district
        const filteredFarms = farms.filter(farm => {
            if (province && farm.province !== province) return false;
            if (district && farm.district !== district) return false;
            return true;
        });

        // Repopulate the farm dropdown with filtered farms
        elements.farmSelect.innerHTML = '<option value="">Select farm...</option>';

        if (filteredFarms.length === 0) {
            elements.farmSelect.innerHTML = '<option value="">No farms in this location</option>';
        } else {
            filteredFarms.forEach(farm => {
                const option = document.createElement('option');
                option.value = farm.id;
                option.textContent = farm.farmName;
                option.dataset.animals = JSON.stringify(farm.animalTags || []);
                option.dataset.province = farm.province || '';
                option.dataset.district = farm.district || '';
                elements.farmSelect.appendChild(option);
            });
        }

        // Refresh CustomSelect
        if (window.CustomSelect) {
            CustomSelect.refresh('farmId');
        }

        // Reset dependent dropdowns
        elements.animalTypeSelect.innerHTML = '<option value="">Select farm first...</option>';
        elements.animalTypeSelect.disabled = true;
        elements.diseaseSelect.innerHTML = '<option value="">Select animal type first...</option>';
        elements.diseaseSelect.disabled = true;
        elements.diseaseInfoSection.style.display = 'none';

        if (window.CustomSelect) {
            CustomSelect.refresh('animalTypeId');
            CustomSelect.refresh('diseaseId');
        }
    }

    // ========================================
    // Farm and Disease Dropdown Functions
    // ========================================

    async function loadFarms() {
        try {
            farms = await fetchAPI('/api/vet/farms');
            populateFarmDropdown();
        } catch (error) {
            console.error('Error loading farms:', error);
            showFormMessage('Failed to load farms. Please refresh the page.', 'error');
        }
    }

    function populateFarmDropdown() {
        elements.farmSelect.innerHTML = '<option value="">Select farm...</option>';

        farms.forEach(farm => {
            const option = document.createElement('option');
            option.value = farm.id;
            option.textContent = farm.farmName;
            option.dataset.animals = JSON.stringify(farm.animalTags || []);
            option.dataset.province = farm.province || '';
            option.dataset.district = farm.district || '';
            elements.farmSelect.appendChild(option);
        });
        // Refresh CustomSelect to reflect new options
        if (window.CustomSelect) {
            CustomSelect.refresh('farmId');
        }
    }

    async function onFarmChange() {
        const farmId = elements.farmSelect.value;

        // Reset dependent dropdowns
        elements.animalTypeSelect.innerHTML = '<option value="">Select animal type...</option>';
        elements.diseaseSelect.innerHTML = '<option value="">Select animal type first...</option>';
        elements.diseaseSelect.disabled = true;
        elements.diseaseInfoSection.style.display = 'none';

        if (!farmId) {
            elements.animalTypeSelect.disabled = true;
            // Refresh CustomSelect for dependent dropdowns
            if (window.CustomSelect) {
                CustomSelect.refresh('animalTypeId');
                CustomSelect.refresh('diseaseId');
            }
            return;
        }

        // Get farm animals from the selected option
        const selectedOption = elements.farmSelect.options[elements.farmSelect.selectedIndex];
        const farmAnimals = JSON.parse(selectedOption.dataset.animals || '[]');

        if (farmAnimals.length === 0) {
            elements.animalTypeSelect.innerHTML = '<option value="">No animals registered for this farm</option>';
            elements.animalTypeSelect.disabled = true;
            return;
        }

        elements.animalTypeSelect.disabled = false;

        farmAnimals.forEach(animal => {
            const option = document.createElement('option');
            option.value = animal.animalTypeId;
            option.textContent = `${animal.animalTypeName} (${animal.count})`;
            option.dataset.count = animal.count; // Store count for validation
            elements.animalTypeSelect.appendChild(option);
        });
        // Refresh CustomSelect to reflect new options
        if (window.CustomSelect) {
            CustomSelect.refresh('animalTypeId');
        }
    }

    async function onAnimalTypeChange() {
        const animalTypeId = elements.animalTypeSelect.value;

        // Reset disease dropdown and Other Disease section
        elements.diseaseSelect.innerHTML = '<option value="">Loading diseases...</option>';
        elements.diseaseInfoSection.style.display = 'none';
        hideOtherDiseaseSection();

        if (!animalTypeId) {
            elements.diseaseSelect.innerHTML = '<option value="">Select animal type first...</option>';
            elements.diseaseSelect.disabled = true;
            currentAnimalCount = null;
            updateAffectedCountMax();
            // Refresh CustomSelect for disease dropdown
            if (window.CustomSelect) {
                CustomSelect.refresh('diseaseId');
            }
            return;
        }

        // Store the registered animal count for validation
        const selectedOption = elements.animalTypeSelect.options[elements.animalTypeSelect.selectedIndex];
        currentAnimalCount = parseInt(selectedOption.dataset.count) || null;
        updateAffectedCountMax();

        try {
            const diseases = await fetchAPI(`/api/vet/diseases?animalTypeId=${animalTypeId}`);

            elements.diseaseSelect.innerHTML = '<option value="">Select disease...</option>';

            elements.diseaseSelect.disabled = false;

            diseases.forEach(disease => {
                const option = document.createElement('option');
                option.value = disease.id;
                option.textContent = disease.diseaseName;
                option.dataset.disease = JSON.stringify(disease);
                elements.diseaseSelect.appendChild(option);
            });

            // Add "Other" option at the end
            const otherOption = document.createElement('option');
            otherOption.value = OTHER_DISEASE_VALUE;
            otherOption.textContent = '➕ Other (Add new disease)';
            otherOption.className = 'other-disease-option';
            elements.diseaseSelect.appendChild(otherOption);

            // Refresh CustomSelect to reflect new options
            if (window.CustomSelect) {
                CustomSelect.refresh('diseaseId');
            }
        } catch (error) {
            console.error('Error loading diseases:', error);
            elements.diseaseSelect.innerHTML = '<option value="">Error loading diseases</option>';
            elements.diseaseSelect.disabled = true;
        }
    }

    function onDiseaseChange() {
        const diseaseId = elements.diseaseSelect.value;

        // Check if "Other" is selected
        if (diseaseId === OTHER_DISEASE_VALUE) {
            isOtherDiseaseSelected = true;
            elements.diseaseInfoSection.style.display = 'none';
            currentDiseaseData = null;
            showOtherDiseaseSection();
            return;
        }

        // Hide Other Disease section if a regular disease is selected
        isOtherDiseaseSelected = false;
        hideOtherDiseaseSection();

        if (!diseaseId) {
            elements.diseaseInfoSection.style.display = 'none';
            currentDiseaseData = null;
            return;
        }

        const selectedOption = elements.diseaseSelect.options[elements.diseaseSelect.selectedIndex];
        const disease = JSON.parse(selectedOption.dataset.disease || '{}');
        currentDiseaseData = disease;

        // Reset override fields when disease changes (for new disease selection)
        if (elements.editDiseaseName) elements.editDiseaseName.value = '';
        if (elements.editSeverity) elements.editSeverity.value = '';
        if (elements.editNotifiable) elements.editNotifiable.value = '';
        if (elements.editDiseaseDescription) elements.editDiseaseDescription.value = '';
        if (window.CustomSelect) {
            CustomSelect.refresh('editSeverity');
            CustomSelect.refresh('editNotifiable');
        }

        // Remove override indicators
        elements.displayDiseaseName.classList.remove('overridden');
        elements.displaySeverity.classList.remove('overridden');
        elements.displayNotifiable.classList.remove('overridden');
        elements.displayDescription.classList.remove('overridden');

        // Populate disease info display
        elements.displayDiseaseName.textContent = disease.diseaseName || '-';
        elements.displayDiseaseCode.textContent = disease.diseaseCode || '-';

        // Severity badge
        const severity = disease.severity?.toLowerCase() || '';
        elements.displaySeverity.textContent = disease.severity || '-';
        elements.displaySeverity.className = 'info-value severity-badge ' + severity;

        elements.displayNotifiable.textContent = disease.isNotifiable ? 'Yes' : 'No';
        elements.displayDescription.textContent = disease.description || 'No description available';

        // Auto-fill symptoms and treatment from disease defaults (only for new reports)
        if (!editMode) {
            if (disease.symptoms) {
                elements.symptoms.value = disease.symptoms;
            }
            if (disease.treatment) {
                elements.treatment.value = disease.treatment;
            }
        }

        elements.diseaseInfoSection.style.display = 'block';
    }

    // ========================================
    // Image handling
    // ========================================

    function handleDragOver(e) {
        e.preventDefault();
        elements.imageDropzone.classList.add('dragover');
    }

    function handleDragLeave(e) {
        e.preventDefault();
        elements.imageDropzone.classList.remove('dragover');
    }

    function handleDrop(e) {
        e.preventDefault();
        elements.imageDropzone.classList.remove('dragover');

        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFile(files[0]);
        }
    }

    function handleImageSelect(e) {
        const file = e.target.files[0];
        if (file) {
            handleFile(file);
        }
    }

    function handleFile(file) {
        // Validate file type
        const allowedTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
        if (!allowedTypes.includes(file.type)) {
            showFormMessage('Invalid file type. Please upload a JPEG, PNG, WebP, or GIF image.', 'error');
            return;
        }

        // Validate file size (5MB)
        if (file.size > 5 * 1024 * 1024) {
            showFormMessage('File too large. Maximum size is 5MB.', 'error');
            return;
        }

        selectedImageFile = file;

        // Show preview
        const reader = new FileReader();
        reader.onload = (e) => {
            elements.imagePreview.src = e.target.result;
            elements.imageDropzone.style.display = 'none';
            elements.imagePreviewContainer.style.display = 'inline-block';
        };
        reader.readAsDataURL(file);
    }

    function removeImage() {
        selectedImageFile = null;
        if (elements.imageInput) elements.imageInput.value = '';
        if (elements.imagePreview) elements.imagePreview.src = '';
        if (elements.imageDropzone) elements.imageDropzone.style.display = 'block';
        if (elements.imagePreviewContainer) elements.imagePreviewContainer.style.display = 'none';

        // If in edit mode, mark that the existing image should be cleared
        if (editMode) {
            clearExistingImage = true;
        }
    }

    // ========================================
    // Form submission
    // ========================================

    async function handleSubmit(e) {
        e.preventDefault();

        // Validate required fields
        if (!elements.farmSelect.value || !elements.animalTypeSelect.value || !elements.diseaseSelect.value) {
            showFormMessage('Please fill in all required fields.', 'error');
            return;
        }

        // If "Other" disease is selected, validate the new disease form
        if (isOtherDiseaseSelected) {
            if (!validateOtherDiseaseForm()) {
                showFormMessage('Please fill in all required fields for the new disease.', 'error');
                return;
            }
        }

        // Validate affected count does not exceed registered animal count
        const affectedCountInput = document.getElementById('affectedCount');
        const affectedCount = affectedCountInput.value ? parseInt(affectedCountInput.value) : null;
        if (affectedCount && currentAnimalCount && affectedCount > currentAnimalCount) {
            showFormMessage(`Affected count (${affectedCount}) cannot exceed the registered animal count (${currentAnimalCount}) for this farm.`, 'error');
            affectedCountInput.focus();
            return;
        }

        // Validate we have a report ID when in edit mode
        if (editMode && !currentReportId) {
            showFormMessage('Unable to update: Report ID is missing. Please try again.', 'error');
            return;
        }

        // Prepare form data
        const reportData = {
            farmId: elements.farmSelect.value,
            animalTypeId: elements.animalTypeSelect.value,
            reportDate: elements.reportDate.value,
            affectedCount: document.getElementById('affectedCount').value ? parseInt(document.getElementById('affectedCount').value) : null,
            symptoms: elements.symptoms.value,
            diagnosis: document.getElementById('diagnosis').value,
            treatment: elements.treatment.value,
            outcome: document.getElementById('outcome').value,
            notes: document.getElementById('notes').value,
            clearImage: editMode && clearExistingImage // Include flag to clear existing image
        };

        // Handle disease ID or "Other" disease data
        if (isOtherDiseaseSelected) {
            // "Other" disease - include new disease fields
            reportData.isOtherDisease = true;
            reportData.diseaseId = null;
            reportData.newDiseaseName = elements.newDiseaseName?.value?.trim() || null;
            reportData.newDiseaseCode = elements.newDiseaseCode?.value?.trim() || null;
            reportData.newDiseaseSeverity = elements.newDiseaseSeverity?.value || null;
            reportData.newDiseaseIsNotifiable = elements.newDiseaseNotifiable?.value === 'true';
            reportData.newDiseaseDescription = elements.newDiseaseDescription?.value?.trim() || null;
        } else {
            // Existing disease
            reportData.isOtherDisease = false;
            reportData.diseaseId = elements.diseaseSelect.value;
            // Disease info overrides (vet-specific, does not affect admin data)
            reportData.overrideDiseaseName = elements.editDiseaseName?.value || null;
            reportData.overrideSeverity = elements.editSeverity?.value || null;
            reportData.overrideNotifiable = elements.editNotifiable?.value ? (elements.editNotifiable.value === 'true') : null;
            reportData.overrideDescription = elements.editDiseaseDescription?.value || null;
        }

        // Create FormData for multipart request
        const formData = new FormData();
        formData.append('report', new Blob([JSON.stringify(reportData)], { type: 'application/json' }));

        if (selectedImageFile) {
            formData.append('image', selectedImageFile);
        }

        // Show loading state
        setSubmitLoading(true);

        try {
            const headers = {};
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            const url = editMode
                ? `/api/vet/disease-reports/${currentReportId}`
                : '/api/vet/disease-reports';

            const method = editMode ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: headers,
                body: formData
            });

            if (!response.ok) {
                const error = await response.json().catch(() => ({ message: 'Failed to submit report' }));
                throw new Error(error.message || 'Failed to submit report');
            }

            const result = await response.json();

            const successMessage = editMode
                ? 'Disease report updated successfully!'
                : 'Disease report submitted successfully!';
            showFormMessage(successMessage, 'success');

            // Refresh reports table
            await loadReports();

            // Close modal after delay
            setTimeout(() => {
                closeModal();
            }, 1500);

        } catch (error) {
            console.error('Error submitting report:', error);
            showFormMessage(error.message || 'Failed to submit report. Please try again.', 'error');
        } finally {
            setSubmitLoading(false);
        }
    }

    function setSubmitLoading(loading) {
        const btnText = elements.submitBtn.querySelector('.btn-text');
        const btnLoading = elements.submitBtn.querySelector('.btn-loading');

        if (loading) {
            btnText.style.display = 'none';
            btnLoading.style.display = 'inline';
            elements.submitBtn.disabled = true;
        } else {
            btnText.style.display = 'inline';
            btnLoading.style.display = 'none';
            elements.submitBtn.disabled = false;
        }
    }

    // ========================================
    // Farm-centric Functions
    // ========================================

    async function loadFarmsWithReports() {
        showLoadingState();

        try {
            // Load disease reports for current user
            const reports = await fetchAPI('/api/vet/disease-reports');
            allReports = reports;

            // Group reports by farm
            farmsWithReports = groupReportsByFarm(reports);
            filteredFarms = [...farmsWithReports];

            // Populate the disease filter dropdown
            populatePageDiseaseFilter();

            if (farmsWithReports.length === 0) {
                showEmptyState();
            } else {
                renderFarmCards();
                hideLoadingState();
            }
        } catch (error) {
            console.error('Error loading farms with reports:', error);
            showEmptyState();
        }
    }

    async function loadOtherFarmsWithReports() {
        if (otherReportsLoaded) return;

        showOtherLoadingState();

        try {
            const reports = await fetchAPI('/api/vet/disease-reports/others');
            allOtherReports = reports;

            // Group reports by farm
            otherFarmsWithReports = groupReportsByFarm(reports, true);
            filteredOtherFarms = [...otherFarmsWithReports];

            otherReportsLoaded = true;

            // Re-populate disease filter to include diseases from other reports
            populatePageDiseaseFilter();

            if (otherFarmsWithReports.length === 0) {
                showOtherEmptyState();
            } else {
                renderOtherFarmCards();
                hideOtherLoadingState();
            }
        } catch (error) {
            console.error('Error loading other farms with reports:', error);
            showOtherEmptyState();
        }
    }

    function groupReportsByFarm(reports, isOther = false) {
        const farmMap = new Map();

        reports.forEach(report => {
            const farmId = report.farmId;
            if (!farmId) return;

            if (!farmMap.has(farmId)) {
                farmMap.set(farmId, {
                    id: farmId,
                    name: report.farmName || 'Unknown Farm',
                    province: report.farmProvince,
                    provinceName: report.farmProvinceDisplayName || '',
                    district: report.farmDistrict,
                    districtName: report.farmDistrictDisplayName || '',
                    reports: [],
                    reportCount: 0,
                    latestReportDate: null,
                    isOther: isOther,
                    reportedBy: isOther ? report.reporterName : null
                });
            }

            const farm = farmMap.get(farmId);
            farm.reports.push(report);
            farm.reportCount++;

            // Track latest report date
            const reportDate = new Date(report.reportDate || report.createdAt);
            if (!farm.latestReportDate || reportDate > farm.latestReportDate) {
                farm.latestReportDate = reportDate;
            }
        });

        // Convert to array and sort by latest report date (most recent first)
        return Array.from(farmMap.values()).sort((a, b) =>
            (b.latestReportDate || 0) - (a.latestReportDate || 0)
        );
    }

    // ========================================
    // Disease Filter (checkbox dropdown) Functions
    // ========================================

    function setupDiseaseFilterListeners() {
        // Toggle dropdown open/close
        elements.diseaseFilterToggle?.addEventListener('click', (e) => {
            e.stopPropagation();
            const dropdown = elements.diseaseFilterDropdown;
            if (!dropdown) return;
            dropdown.classList.toggle('open');
            // Focus search input when opening
            if (dropdown.classList.contains('open') && elements.diseaseFilterSearch) {
                setTimeout(() => elements.diseaseFilterSearch.focus(), 50);
            }
        });

        // Search within disease options
        elements.diseaseFilterSearch?.addEventListener('input', (e) => {
            const searchTerm = e.target.value.toLowerCase();
            const options = elements.diseaseFilterOptions?.querySelectorAll('.dropdown-option');
            let hasVisible = false;
            options?.forEach(option => {
                const label = option.querySelector('label');
                const text = label?.textContent?.toLowerCase() || '';
                if (text.includes(searchTerm)) {
                    option.classList.remove('hidden');
                    hasVisible = true;
                } else {
                    option.classList.add('hidden');
                }
            });

            // Show/hide no results message
            let noResults = elements.diseaseFilterOptions?.querySelector('.dropdown-no-results');
            if (!hasVisible) {
                if (!noResults) {
                    noResults = document.createElement('div');
                    noResults.className = 'dropdown-no-results';
                    noResults.textContent = 'No diseases found';
                    elements.diseaseFilterOptions?.appendChild(noResults);
                }
                noResults.style.display = 'block';
            } else if (noResults) {
                noResults.style.display = 'none';
            }
        });

        // Select All
        elements.selectAllPageDiseases?.addEventListener('click', () => {
            const checkboxes = elements.diseaseFilterOptions?.querySelectorAll('input[type="checkbox"]');
            checkboxes?.forEach(cb => {
                cb.checked = true;
                selectedDiseases.add(cb.value);
            });
            updateDiseaseFilterToggleText();
            applyFarmFilters();
        });

        // Clear All
        elements.clearAllPageDiseases?.addEventListener('click', () => {
            const checkboxes = elements.diseaseFilterOptions?.querySelectorAll('input[type="checkbox"]');
            checkboxes?.forEach(cb => {
                cb.checked = false;
            });
            selectedDiseases.clear();
            updateDiseaseFilterToggleText();
            applyFarmFilters();
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', (e) => {
            const dropdown = elements.diseaseFilterDropdown;
            if (dropdown && !dropdown.contains(e.target)) {
                dropdown.classList.remove('open');
            }
        });
    }

    function populatePageDiseaseFilter() {
        const optionsContainer = elements.diseaseFilterOptions;
        if (!optionsContainer) return;

        // Collect unique disease names from all reports
        const diseaseNames = new Set();
        [...allReports, ...allOtherReports].forEach(report => {
            const name = report.effectiveDiseaseName || report.diseaseName;
            if (name) diseaseNames.add(name);
        });

        // Sort alphabetically
        const sorted = Array.from(diseaseNames).sort((a, b) => a.localeCompare(b));

        // Clear existing options (except no-results)
        optionsContainer.innerHTML = '';

        if (sorted.length === 0) {
            optionsContainer.innerHTML = '<div class="dropdown-no-results">No diseases available</div>';
            return;
        }

        // Create checkbox options
        sorted.forEach(name => {
            const option = document.createElement('div');
            option.className = 'dropdown-option';

            const id = 'disease-filter-' + name.replace(/\s+/g, '-').toLowerCase();
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.id = id;
            checkbox.value = name;
            checkbox.checked = selectedDiseases.has(name);

            checkbox.addEventListener('change', () => {
                if (checkbox.checked) {
                    selectedDiseases.add(name);
                } else {
                    selectedDiseases.delete(name);
                }
                updateDiseaseFilterToggleText();
                applyFarmFilters();
            });

            const label = document.createElement('label');
            label.htmlFor = id;
            label.textContent = name;

            option.appendChild(checkbox);
            option.appendChild(label);
            optionsContainer.appendChild(option);
        });

        // Clear search input
        if (elements.diseaseFilterSearch) {
            elements.diseaseFilterSearch.value = '';
        }

        updateDiseaseFilterToggleText();
    }

    function updateDiseaseFilterToggleText() {
        const textEl = elements.diseaseFilterToggle?.querySelector('.dropdown-text');
        if (!textEl) return;

        if (selectedDiseases.size === 0) {
            textEl.textContent = 'All Diseases';
            textEl.classList.remove('has-selection');
        } else {
            textEl.textContent = `${selectedDiseases.size} selected`;
            textEl.classList.add('has-selection');
        }
    }

    // ========================================
    // Main Page Province/District Filters
    // ========================================

    async function loadPageProvinces() {
        try {
            const provinces = await fetchAPI('/api/locations/provinces');
            console.log('Loaded provinces for filter:', provinces);

            if (elements.provinceFilterSelect && Array.isArray(provinces)) {
                elements.provinceFilterSelect.innerHTML = '<option value="">All Provinces</option>';
                provinces.forEach(province => {
                    const option = document.createElement('option');
                    option.value = province.value; // API returns 'value' not 'code'
                    option.textContent = province.label; // API returns 'label' not 'name'
                    elements.provinceFilterSelect.appendChild(option);
                });
                console.log('Province filter populated with', provinces.length, 'provinces');
            } else {
                console.warn('Province filter element not found or provinces not an array:', {
                    element: elements.provinceFilterSelect,
                    provinces: provinces
                });
            }
        } catch (error) {
            console.error('Error loading provinces:', error);
        }
    }

    async function handlePageProvinceChange() {
        const provinceCode = elements.provinceFilterSelect?.value || '';
        provinceFilter = provinceCode;
        districtFilter = '';

        // Reset and update district dropdown
        if (elements.districtFilterSelect) {
            elements.districtFilterSelect.innerHTML = '<option value="">All Districts</option>';
            elements.districtFilterSelect.disabled = !provinceCode;

            if (provinceCode) {
                try {
                    // API expects 'provinceName' parameter, not 'province'
                    const districts = await fetchAPI(`/api/locations/districts?provinceName=${provinceCode}`);
                    districts.forEach(district => {
                        const option = document.createElement('option');
                        option.value = district.value; // API returns 'value' not 'code'
                        option.textContent = district.label; // API returns 'label' not 'name'
                        elements.districtFilterSelect.appendChild(option);
                    });
                } catch (error) {
                    console.error('Error loading districts:', error);
                }
            }
        }

        applyFarmFilters();
    }

    function handlePageDistrictChange() {
        districtFilter = elements.districtFilterSelect?.value || '';
        applyFarmFilters();
    }

    function applyFarmFilters() {
        // Filter farms based on province, district, search query, and disease
        filteredFarms = farmsWithReports.filter(farm => {
            // Province filter
            if (provinceFilter && farm.province !== provinceFilter) return false;
            // District filter
            if (districtFilter && farm.district !== districtFilter) return false;
            // Search filter
            if (searchQuery) {
                const query = searchQuery.toLowerCase();
                const matchesName = farm.name?.toLowerCase().includes(query);
                const matchesProvince = farm.provinceName?.toLowerCase().includes(query);
                const matchesDistrict = farm.districtName?.toLowerCase().includes(query);
                if (!matchesName && !matchesProvince && !matchesDistrict) return false;
            }
            // Disease filter — keep farm only if it has at least one report matching a selected disease
            if (selectedDiseases.size > 0) {
                const hasMatchingReport = farm.reports.some(report => {
                    const diseaseName = report.effectiveDiseaseName || report.diseaseName;
                    return selectedDiseases.has(diseaseName);
                });
                if (!hasMatchingReport) return false;
            }
            return true;
        });

        filteredOtherFarms = otherFarmsWithReports.filter(farm => {
            if (provinceFilter && farm.province !== provinceFilter) return false;
            if (districtFilter && farm.district !== districtFilter) return false;
            if (searchQuery) {
                const query = searchQuery.toLowerCase();
                const matchesName = farm.name?.toLowerCase().includes(query);
                const matchesProvince = farm.provinceName?.toLowerCase().includes(query);
                const matchesDistrict = farm.districtName?.toLowerCase().includes(query);
                if (!matchesName && !matchesProvince && !matchesDistrict) return false;
            }
            // Disease filter — keep farm only if it has at least one report matching a selected disease
            if (selectedDiseases.size > 0) {
                const hasMatchingReport = farm.reports.some(report => {
                    const diseaseName = report.effectiveDiseaseName || report.diseaseName;
                    return selectedDiseases.has(diseaseName);
                });
                if (!hasMatchingReport) return false;
            }
            return true;
        });

        // Render based on current tab
        if (currentTab === 'my-reports') {
            if (filteredFarms.length === 0) {
                showNoSearchResults();
            } else {
                hideNoSearchResults();
                renderFarmCards();
            }
        } else {
            if (filteredOtherFarms.length === 0) {
                showNoSearchResults();
            } else {
                hideNoSearchResults();
                renderOtherFarmCards();
            }
        }
    }

    // ========================================
    // Farm Cards Rendering
    // ========================================

    function renderFarmCards() {
        const container = elements.farmsGridContainer;
        if (!container) return;

        container.innerHTML = filteredFarms.map(farm => createFarmCardHtml(farm)).join('');

        // Add click handlers
        container.querySelectorAll('.farm-card').forEach(card => {
            card.addEventListener('click', (e) => {
                if (!e.target.closest('.btn')) {
                    navigateToFarm(card.dataset.farmId);
                }
            });
        });

        container.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                navigateToFarm(btn.dataset.farmId);
            });
        });

        hideLoadingState();
    }

    function renderOtherFarmCards() {
        const container = elements.otherFarmsGridContainer;
        if (!container) return;

        container.innerHTML = filteredOtherFarms.map(farm => createFarmCardHtml(farm, true)).join('');

        // Add click handlers
        container.querySelectorAll('.farm-card').forEach(card => {
            card.addEventListener('click', (e) => {
                if (!e.target.closest('.btn')) {
                    navigateToFarm(card.dataset.farmId);
                }
            });
        });

        container.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                navigateToFarm(btn.dataset.farmId);
            });
        });

        hideOtherLoadingState();
    }

    function createFarmCardHtml(farm, isOther = false) {
        const location = [farm.districtName, farm.provinceName].filter(Boolean).join(', ') || 'Location not specified';
        const latestDate = farm.latestReportDate ? farm.latestReportDate.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) : 'N/A';

        return `
            <div class="farm-card ${isOther ? 'other-vet-card' : ''}" data-farm-id="${farm.id}" tabindex="0" role="button" aria-label="View ${escapeHtml(farm.name)} disease reports">
                <div class="farm-card-header">
                    <div class="farm-card-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                            <polyline points="9 22 9 12 15 12 15 22"></polyline>
                        </svg>
                    </div>
                    <div class="farm-card-badge">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                            <polyline points="14 2 14 8 20 8"></polyline>
                        </svg>
                        ${farm.reportCount} Report${farm.reportCount !== 1 ? 's' : ''}
                    </div>
                </div>
                <div class="farm-card-body">
                    <h3 class="farm-card-title">${escapeHtml(farm.name)}</h3>
                    <div class="farm-card-location">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                            <circle cx="12" cy="10" r="3"></circle>
                        </svg>
                        <span>${escapeHtml(location)}</span>
                    </div>
                    <div class="farm-card-meta">
                        <span class="farm-card-meta-item">
                            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                <line x1="16" y1="2" x2="16" y2="6"></line>
                                <line x1="8" y1="2" x2="8" y2="6"></line>
                                <line x1="3" y1="10" x2="21" y2="10"></line>
                            </svg>
                            Latest: ${latestDate}
                        </span>
                        ${isOther && farm.reportedBy ? `
                        <span class="farm-card-meta-item">
                            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                <circle cx="12" cy="7" r="4"></circle>
                            </svg>
                            ${escapeHtml(farm.reportedBy)}
                        </span>
                        ` : ''}
                    </div>
                </div>
                <div class="farm-card-footer">
                    <button class="btn btn-primary btn-sm view-btn" data-farm-id="${farm.id}">
                        View Reports
                    </button>
                </div>
            </div>
        `;
    }

    function navigateToFarm(farmId) {
        window.location.href = `/vet/disease-reporting/farm/${farmId}`;
    }

    // ========================================
    // Legacy Reports Table (kept for compatibility)
    // ========================================

    async function loadReports() {
        // This function is kept for backwards compatibility
        // Now replaced by loadFarmsWithReports
        loadFarmsWithReports();
    }

    function renderReportsTable(reports) {
        // Render grid view
        const cardsGrid = document.createElement('div');
        cardsGrid.className = 'reports-cards-grid';

        reports.forEach(report => {
            const card = document.createElement('div');
            card.className = 'report-card';

            // Format date
            const date = new Date(report.reportDate).toLocaleDateString();

            // Use effective values (overrides applied)
            const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
            const effectiveSeverity = report.effectiveSeverity || report.severity;
            const severityClass = effectiveSeverity?.toLowerCase() || '';
            const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
                ? report.effectiveNotifiable
                : report.isNotifiable;

            // Build location string for card title
            const locationParts = [];
            if (report.farmDistrictDisplayName) {
                locationParts.push(report.farmDistrictDisplayName);
            }
            if (report.farmProvinceDisplayName) {
                locationParts.push(report.farmProvinceDisplayName);
            }
            const farmLocation = locationParts.length > 0 ? locationParts.join(', ') : (report.farmName || 'Unknown Location');

            // Image HTML - show actual image or placeholder
            const imageHtml = report.imageUrl
                ? `<img src="${report.imageUrl}" alt="Disease Photo" class="report-card-image">`
                : `<div class="report-card-placeholder"><i data-lucide="bug" class="icon icon-xl icon-muted"></i><span class="placeholder-text">No Image</span></div>`;

            card.innerHTML = `
                <div class="report-card-media">
                    ${imageHtml}
                </div>
                <div class="report-card-content">
                    <div class="report-card-header">
                        <h4 class="report-card-title">${escapeHtml(report.farmName) || 'Unknown Farm'}</h4>
                        <span class="severity-badge ${severityClass}">${effectiveSeverity || '-'}</span>
                    </div>
                    <p class="report-card-disease-name"><i data-lucide="bug" class="icon icon-xs"></i> ${escapeHtml(effectiveDiseaseName) || 'Unknown Disease'}</p>
                    <div class="report-card-meta">
                        <div class="report-card-info">
                            <i data-lucide="map-pin" class="icon icon-xs info-icon"></i>
                            <span>${farmLocation}</span>
                        </div>
                        <div class="report-card-info">
                            <i data-lucide="paw-print" class="icon icon-xs info-icon"></i>
                            <span>${report.animalTypeName || '-'}</span>
                        </div>
                        <div class="report-card-info">
                            <i data-lucide="calendar" class="icon icon-xs info-icon"></i>
                            <span>${date}</span>
                        </div>
                    </div>
                    <div class="report-card-footer">
                        <button class="btn btn-outline btn-sm view-btn" data-id="${report.id}">
                            <i data-lucide="eye" class="icon icon-xs"></i> View Details
                        </button>
                        ${effectiveNotifiable ? '<span class="notifiable-badge notifiable-corner"><i data-lucide="alert-triangle" class="icon icon-xs"></i> Notifiable</span>' : ''}
                    </div>
                </div>
            `;

            if (typeof lucide !== 'undefined') lucide.createIcons({ nodes: [card] });

            // Add click event for view button
            const viewBtn = card.querySelector('.view-btn');
            viewBtn.addEventListener('click', () => openViewModal(report.id));

            cardsGrid.appendChild(card);
        });

        // Clear and populate grid container
        if (elements.reportsGridContainer) {
            elements.reportsGridContainer.innerHTML = '';
            elements.reportsGridContainer.appendChild(cardsGrid);
        }

        // Render table view
        if (elements.reportsTableBody) {
            elements.reportsTableBody.innerHTML = reports.map(report => createReportTableRow(report)).join('');
            // Add click events for table view buttons
            elements.reportsTableBody.querySelectorAll('.view-btn').forEach(btn => {
                btn.addEventListener('click', () => openViewModal(btn.dataset.id));
            });
        }

        // Apply current view mode
        updateViewDisplay();
    }

    /**
     * Create table row HTML for a report (My Reports)
     */
    function createReportTableRow(report) {
        const date = new Date(report.reportDate).toLocaleDateString();
        // Use effective values (overrides applied)
        const effectiveDiseaseName = report.effectiveDiseaseName || report.diseaseName;
        const effectiveSeverity = report.effectiveSeverity || report.severity;
        const severityClass = effectiveSeverity?.toLowerCase() || '';
        const effectiveNotifiable = report.effectiveNotifiable !== null && report.effectiveNotifiable !== undefined
            ? report.effectiveNotifiable
            : report.isNotifiable;

        return `
            <tr data-report-id="${report.id}">
                <td><span class="table-disease-name">${escapeHtml(effectiveDiseaseName || 'Unknown')}</span></td>
                <td>${escapeHtml(report.farmName || '-')}</td>
                <td>${escapeHtml(report.animalTypeName || '-')}</td>
                <td>${date}</td>
                <td><span class="severity-badge ${severityClass}">${effectiveSeverity || '-'}</span></td>
                <td>${effectiveNotifiable ? '<span class="notifiable-badge-small"><i data-lucide="alert-triangle" class="icon icon-xs"></i> Yes</span>' : 'No'}</td>
                <td>${report.affectedCount || '-'}</td>
                <td>
                    <div class="table-actions">
                        <button class="btn btn-outline btn-sm view-btn" data-id="${report.id}">
                            <i data-lucide="eye" class="icon icon-xs"></i> View
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }

    function showLoadingState() {
        if (elements.loadingState) elements.loadingState.style.display = 'block';
        if (elements.emptyState) elements.emptyState.style.display = 'none';
        if (elements.farmsGridContainer) elements.farmsGridContainer.style.display = 'none';
        if (elements.farmsTableContainer) elements.farmsTableContainer.style.display = 'none';
    }

    function hideLoadingState() {
        if (elements.loadingState) elements.loadingState.style.display = 'none';
        if (elements.farmsGridContainer) elements.farmsGridContainer.style.display = 'block';
    }

    function showEmptyState() {
        if (elements.loadingState) elements.loadingState.style.display = 'none';
        if (elements.emptyState) elements.emptyState.style.display = 'block';
        if (elements.farmsGridContainer) elements.farmsGridContainer.style.display = 'none';
        if (elements.farmsTableContainer) elements.farmsTableContainer.style.display = 'none';
    }

    function showOtherLoadingState() {
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'block';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'none';
        if (elements.otherFarmsGridContainer) elements.otherFarmsGridContainer.style.display = 'none';
        if (elements.otherFarmsTableContainer) elements.otherFarmsTableContainer.style.display = 'none';
    }

    function hideOtherLoadingState() {
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'none';
        if (elements.otherFarmsGridContainer) elements.otherFarmsGridContainer.style.display = 'block';
    }

    function showOtherEmptyState() {
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'none';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'block';
        if (elements.otherFarmsGridContainer) elements.otherFarmsGridContainer.style.display = 'none';
        if (elements.otherFarmsTableContainer) elements.otherFarmsTableContainer.style.display = 'none';
    }

    function showReportsTable() {
        if (elements.loadingState) elements.loadingState.style.display = 'none';
        if (elements.emptyState) elements.emptyState.style.display = 'none';
        updateViewDisplay();
    }

    // ========================================
    // Message helpers
    // ========================================

    function showFormMessage(message, type) {
        elements.formMessage.textContent = message;
        elements.formMessage.className = `alert alert-${type}`;
        elements.formMessage.style.display = 'block';

        // Scroll to message
        elements.formMessage.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    function hideFormMessage() {
        elements.formMessage.style.display = 'none';
    }

    // Update affected count input max value and hint
    function updateAffectedCountMax() {
        const affectedCountInput = document.getElementById('affectedCount');
        const affectedCountHint = document.getElementById('affectedCount-hint');

        if (currentAnimalCount && affectedCountInput) {
            affectedCountInput.max = currentAnimalCount;
            if (affectedCountHint) {
                affectedCountHint.textContent = `Maximum: ${currentAnimalCount} (registered count)`;
                affectedCountHint.style.display = 'block';
            }
        } else {
            if (affectedCountInput) affectedCountInput.removeAttribute('max');
            if (affectedCountHint) affectedCountHint.style.display = 'none';
        }
    }

    // ========================================
    // Utility Functions
    // ========================================

    /**
     * Escape HTML to prevent XSS attacks.
     */
    function escapeHtml(text) {
        if (typeof text !== 'string') return text || '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

})();

