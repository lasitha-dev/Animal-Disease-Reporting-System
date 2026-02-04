/**
 * Farm Diseases Page JavaScript
 * Handles loading and displaying disease reports for a specific farm
 */

document.addEventListener('DOMContentLoaded', function () {
    // Get farm ID from hidden field
    const farmId = document.getElementById('farmIdValue')?.value;

    if (!farmId) {
        console.error('Farm ID not found');
        showError('Farm ID not found. Please go back and select a farm.');
        return;
    }

    // State
    let allReports = [];
    let filteredReports = [];
    let farmData = null;
    let isOwnFarm = true; // Whether current user owns this farm
    let currentView = localStorage.getItem('farmDiseasesViewMode') || 'grid';

    // DOM Elements
    const elements = {
        farmName: document.getElementById('farmName'),
        farmAddress: document.getElementById('farmAddress'),
        reportsCountText: document.getElementById('reportsCountText'),
        animalTypeFilter: document.getElementById('animalTypeFilter'),
        diseaseFilter: document.getElementById('diseaseFilter'),
        gridViewBtn: document.getElementById('gridViewBtn'),
        tableViewBtn: document.getElementById('tableViewBtn'),
        reportsGridContainer: document.getElementById('reports-grid-container'),
        reportsTableContainer: document.getElementById('reports-table-container'),
        reportsTableBody: document.getElementById('reports-table-body'),
        emptyState: document.getElementById('empty-state'),
        loadingState: document.getElementById('loading-state'),
        openReportModal: document.getElementById('openReportModal'),
        emptyStateReportBtn: document.getElementById('emptyStateReportBtn'),
        viewModal: document.getElementById('viewModal'),
        viewModalBody: document.getElementById('viewModalBody'),
        closeViewModal: document.getElementById('closeViewModal'),
        closeViewModalBtn: document.getElementById('closeViewModalBtn'),
        editFromViewBtn: document.getElementById('editFromViewBtn'),
        deleteFromViewBtn: document.getElementById('deleteFromViewBtn'),
        deleteModal: document.getElementById('deleteModal'),
        closeDeleteModal: document.getElementById('closeDeleteModal'),
        cancelDeleteBtn: document.getElementById('cancelDeleteBtn'),
        confirmDeleteBtn: document.getElementById('confirmDeleteBtn'),
        // Edit Modal
        editModal: document.getElementById('editModal'),
        editReportForm: document.getElementById('editReportForm'),
        closeEditModal: document.getElementById('closeEditModal'),
        cancelEditBtn: document.getElementById('cancelEditBtn'),
        editReportId: document.getElementById('editReportId'),
        editDiseaseName: document.getElementById('edit-diseaseName'),
        editAnimalType: document.getElementById('edit-animalType'),
        editReportDate: document.getElementById('editReportDate'),
        editAffectedCount: document.getElementById('editAffectedCount'),
        editOutcome: document.getElementById('editOutcome'),
        editSymptoms: document.getElementById('editSymptoms'),
        editDiagnosis: document.getElementById('editDiagnosis'),
        editTreatment: document.getElementById('editTreatment'),
        editNotes: document.getElementById('editNotes')
    };

    // Initialize
    init();

    async function init() {
        showLoading();
        setupEventListeners();
        setInitialView();

        try {
            await Promise.all([
                loadFarmDetails(),
                loadDiseaseReports()
            ]);
            populateFilters();
            renderReports();
        } catch (error) {
            console.error('Error initializing:', error);
            showError('Failed to load farm details. Please try again.');
        }
    }

    function setupEventListeners() {
        // Filters
        elements.animalTypeFilter?.addEventListener('change', onAnimalTypeFilterChange);
        elements.diseaseFilter?.addEventListener('change', applyFilters);

        // View toggle
        elements.gridViewBtn?.addEventListener('click', () => setView('grid'));
        elements.tableViewBtn?.addEventListener('click', () => setView('table'));

        // New Report buttons - redirect to disease reporting page
        elements.openReportModal?.addEventListener('click', redirectToNewReport);
        elements.emptyStateReportBtn?.addEventListener('click', redirectToNewReport);

        // View Modal
        elements.closeViewModal?.addEventListener('click', closeViewModal);
        elements.closeViewModalBtn?.addEventListener('click', closeViewModal);
        elements.editFromViewBtn?.addEventListener('click', onEditFromView);
        elements.deleteFromViewBtn?.addEventListener('click', onDeleteFromView);

        // Delete Modal
        elements.closeDeleteModal?.addEventListener('click', closeDeleteModal);
        elements.cancelDeleteBtn?.addEventListener('click', closeDeleteModal);
        elements.confirmDeleteBtn?.addEventListener('click', confirmDelete);

        // Close modals on overlay click
        elements.viewModal?.addEventListener('click', (e) => {
            if (e.target === elements.viewModal) closeViewModal();
        });
        elements.deleteModal?.addEventListener('click', (e) => {
            if (e.target === elements.deleteModal) closeDeleteModal();
        });

        // Edit Modal
        elements.closeEditModal?.addEventListener('click', closeEditModal);
        elements.cancelEditBtn?.addEventListener('click', closeEditModal);
        elements.editReportForm?.addEventListener('submit', handleEditSubmit);
        elements.editModal?.addEventListener('click', (e) => {
            if (e.target === elements.editModal) closeEditModal();
        });

        // Keyboard navigation
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                closeViewModal();
                closeDeleteModal();
                closeEditModal();
            }
        });
    }

    function setInitialView() {
        if (currentView === 'table') {
            elements.gridViewBtn?.classList.remove('active');
            elements.tableViewBtn?.classList.add('active');
        }
    }

    function setView(view) {
        currentView = view;
        localStorage.setItem('farmDiseasesViewMode', view);

        if (view === 'grid') {
            elements.gridViewBtn?.classList.add('active');
            elements.tableViewBtn?.classList.remove('active');
            elements.reportsGridContainer.style.display = '';
            elements.reportsTableContainer.style.display = 'none';
        } else {
            elements.gridViewBtn?.classList.remove('active');
            elements.tableViewBtn?.classList.add('active');
            elements.reportsGridContainer.style.display = 'none';
            elements.reportsTableContainer.style.display = '';
        }
    }

    async function loadFarmDetails() {
        try {
            // Try to get from my farms first
            const myFarms = await fetchAPI('/api/vet/farms');
            console.log('[DEBUG] My farms:', myFarms);
            console.log('[DEBUG] Looking for farmId:', farmId, 'type:', typeof farmId);

            farmData = myFarms.find(f => {
                const match = String(f.id) === String(farmId);
                console.log('[DEBUG] Checking my farm:', f.id, f.farmName, 'match:', match);
                return match;
            });
            isOwnFarm = true;

            if (!farmData) {
                // Try other farms
                console.log('[DEBUG] Farm not found in my farms, checking others...');
                const otherFarms = await fetchAPI('/api/vet/farms/others');
                console.log('[DEBUG] Other farms:', otherFarms);

                farmData = otherFarms.find(f => {
                    const match = String(f.id) === String(farmId);
                    console.log('[DEBUG] Checking other farm:', f.id, f.farmName, 'match:', match);
                    return match;
                });
                isOwnFarm = false;
            }

            console.log('[DEBUG] Final farmData:', farmData);
            console.log('[DEBUG] isOwnFarm:', isOwnFarm);

            if (farmData) {
                updateFarmHeader();
            } else {
                showError('Farm not found.');
            }
        } catch (error) {
            console.error('Error loading farm details:', error);
            throw error;
        }
    }

    function updateFarmHeader() {
        if (!farmData) return;

        elements.farmName.textContent = farmData.farmName || 'Unknown Farm';

        const location = [farmData.district, farmData.province].filter(Boolean).join(', ');
        elements.farmAddress.textContent = location || farmData.address || 'Location not specified';

        // Hide new report buttons if not own farm
        if (!isOwnFarm) {
            elements.openReportModal?.style.setProperty('display', 'none');
            elements.emptyStateReportBtn?.style.setProperty('display', 'none');

            // Update empty state message for view-only farms
            const emptyStateP = elements.emptyState?.querySelector('p');
            if (emptyStateP) {
                emptyStateP.textContent = 'This farm has no disease reports from other veterinarians.';
            }
            const emptyStateTitle = elements.emptyState?.querySelector('h3');
            if (emptyStateTitle) {
                emptyStateTitle.textContent = 'No Reports Available';
            }
        }
    }

    async function loadDiseaseReports() {
        try {
            // Always load from both endpoints and combine, then filter by farm
            // This ensures we get all reports for a farm regardless of who created them
            console.log('[DEBUG] Loading reports for farmId:', farmId);
            console.log('[DEBUG] isOwnFarm:', isOwnFarm);

            const [myReports, otherReports] = await Promise.all([
                fetchAPI('/api/vet/disease-reports'),
                fetchAPI('/api/vet/disease-reports/others')
            ]);

            console.log('[DEBUG] My reports count:', myReports.length);
            console.log('[DEBUG] Other reports count:', otherReports.length);

            // Combine all reports
            const allReportsFromAPI = [...myReports, ...otherReports];
            console.log('[DEBUG] Total reports:', allReportsFromAPI.length);

            // Filter reports for this farm
            allReports = allReportsFromAPI.filter(r => String(r.farmId) === String(farmId));
            console.log('[DEBUG] Reports for this farm:', allReports.length);

            filteredReports = [...allReports];

            // Update reports count
            updateReportsCount();
        } catch (error) {
            console.error('Error loading disease reports:', error);
            throw error;
        }
    }

    function updateReportsCount() {
        const count = allReports.length;
        elements.reportsCountText.textContent = `${count} Disease Report${count !== 1 ? 's' : ''}`;
    }

    function populateFilters() {
        // Get unique animal types from reports
        const animalTypes = new Map();
        const diseases = new Map();

        allReports.forEach(report => {
            if (report.animalTypeId && report.animalTypeName) {
                animalTypes.set(report.animalTypeId, report.animalTypeName);
            }
            const diseaseName = report.effectiveDiseaseName || report.diseaseName;
            const diseaseId = report.diseaseId || 'other-' + diseaseName;
            if (diseaseName) {
                diseases.set(diseaseId, diseaseName);
            }
        });

        // Populate animal type filter
        elements.animalTypeFilter.innerHTML = '<option value="">All Animal Types</option>';
        animalTypes.forEach((name, id) => {
            const option = document.createElement('option');
            option.value = id;
            option.textContent = name;
            elements.animalTypeFilter.appendChild(option);
        });

        // Populate disease filter
        elements.diseaseFilter.innerHTML = '<option value="">All Diseases</option>';
        diseases.forEach((name, id) => {
            const option = document.createElement('option');
            option.value = id;
            option.textContent = name;
            elements.diseaseFilter.appendChild(option);
        });
    }

    function onAnimalTypeFilterChange() {
        const selectedAnimalTypeId = elements.animalTypeFilter.value;

        // Update disease filter based on selected animal type
        const diseases = new Map();

        allReports
            .filter(r => !selectedAnimalTypeId || r.animalTypeId === selectedAnimalTypeId)
            .forEach(report => {
                const diseaseName = report.effectiveDiseaseName || report.diseaseName;
                const diseaseId = report.diseaseId || 'other-' + diseaseName;
                if (diseaseName) {
                    diseases.set(diseaseId, diseaseName);
                }
            });

        // Repopulate disease filter
        const currentDiseaseValue = elements.diseaseFilter.value;
        elements.diseaseFilter.innerHTML = '<option value="">All Diseases</option>';
        diseases.forEach((name, id) => {
            const option = document.createElement('option');
            option.value = id;
            option.textContent = name;
            elements.diseaseFilter.appendChild(option);
        });

        // Restore selection if still valid
        if (diseases.has(currentDiseaseValue)) {
            elements.diseaseFilter.value = currentDiseaseValue;
        }

        applyFilters();
    }

    function applyFilters() {
        const animalTypeId = elements.animalTypeFilter.value;
        const diseaseId = elements.diseaseFilter.value;

        filteredReports = allReports.filter(report => {
            // Animal type filter
            if (animalTypeId && report.animalTypeId !== animalTypeId) {
                return false;
            }

            // Disease filter
            if (diseaseId) {
                const reportDiseaseId = report.diseaseId || 'other-' + (report.effectiveDiseaseName || report.diseaseName);
                if (reportDiseaseId !== diseaseId) {
                    return false;
                }
            }

            return true;
        });

        renderReports();
    }

    function renderReports() {
        hideLoading();

        if (filteredReports.length === 0) {
            elements.reportsGridContainer.style.display = 'none';
            elements.reportsTableContainer.style.display = 'none';
            elements.emptyState.style.display = '';
            return;
        }

        elements.emptyState.style.display = 'none';

        // Render grid view
        renderGridView();

        // Render table view
        renderTableView();

        // Apply current view
        setView(currentView);
    }

    function renderGridView() {
        elements.reportsGridContainer.innerHTML = filteredReports.map(report => createReportCard(report)).join('');

        // Add click handlers
        elements.reportsGridContainer.querySelectorAll('.report-card').forEach(card => {
            card.addEventListener('click', (e) => {
                if (!e.target.closest('.btn')) {
                    openViewModal(card.dataset.reportId);
                }
            });
        });

        // Add action button handlers
        elements.reportsGridContainer.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                openViewModal(btn.dataset.reportId);
            });
        });

        elements.reportsGridContainer.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                redirectToEditReport(btn.dataset.reportId);
            });
        });

        elements.reportsGridContainer.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                openDeleteModal(btn.dataset.reportId);
            });
        });
    }

    function createReportCard(report) {
        const diseaseName = report.effectiveDiseaseName || report.diseaseName || 'Unknown Disease';
        const animalType = report.animalTypeName || 'Unknown';
        const severity = report.effectiveSeverity || report.severity || 'Unknown';
        const date = formatDate(report.reportDate || report.createdAt);
        const isNotifiable = report.effectiveNotifiable ?? report.notifiable ?? false;
        const affectedCount = report.numberOfAffectedAnimals || 0;
        const imageUrl = report.imageUrl;

        // Only show View button on cards - Edit/Delete are in the modal
        const actionsHtml = `
            <div class="report-card-actions">
                <button class="btn btn-sm btn-primary view-btn" data-report-id="${report.id}">View</button>
            </div>
        `;

        return `
            <div class="report-card" data-report-id="${report.id}" tabindex="0" role="button" aria-label="View ${diseaseName} report">
                <div class="report-card-header">
                    ${imageUrl
                ? `<img src="${imageUrl}" class="report-card-image" alt="${diseaseName}">`
                : `<div class="report-card-placeholder">
                            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M19 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2z"></path>
                                <circle cx="8.5" cy="8.5" r="1.5"></circle>
                                <polyline points="21 15 16 10 5 21"></polyline>
                            </svg>
                           </div>`
            }
                    <div class="report-card-badges">
                        <span class="severity-badge ${severity.toLowerCase()}">${severity}</span>
                        ${isNotifiable ? '<span class="notifiable-badge">⚠️ Notifiable</span>' : ''}
                    </div>
                </div>
                <div class="report-card-body">
                    <h3 class="report-card-title">${diseaseName}</h3>
                    <p class="report-card-subtitle">${animalType}</p>
                    <div class="report-card-meta">
                        <span class="meta-item">${date}</span>
                        ${affectedCount > 0 ? `<span class="affected-count">${affectedCount} affected</span>` : ''}
                    </div>
                </div>
                <div class="report-card-footer">
                    ${actionsHtml}
                </div>
            </div>
        `;
    }

    function renderTableView() {
        elements.reportsTableBody.innerHTML = filteredReports.map(report => createTableRow(report)).join('');

        // Add action button handlers
        elements.reportsTableBody.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', () => openViewModal(btn.dataset.reportId));
        });

        elements.reportsTableBody.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => redirectToEditReport(btn.dataset.reportId));
        });

        elements.reportsTableBody.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => openDeleteModal(btn.dataset.reportId));
        });
    }

    function createTableRow(report) {
        const diseaseName = report.effectiveDiseaseName || report.diseaseName || 'Unknown';
        const animalType = report.animalTypeName || 'Unknown';
        const severity = report.effectiveSeverity || report.severity || 'Unknown';
        const date = formatDate(report.reportDate || report.createdAt);
        const isNotifiable = report.effectiveNotifiable ?? report.notifiable ?? false;
        const affectedCount = report.numberOfAffectedAnimals || 0;

        // Only show View button in table - Edit/Delete are in the modal
        const actionsHtml = `
            <button class="btn btn-sm btn-primary view-btn" data-report-id="${report.id}">View</button>
        `;

        return `
            <tr>
                <td><span class="table-disease-name">${diseaseName}</span></td>
                <td>${animalType}</td>
                <td>${date}</td>
                <td><span class="severity-badge ${severity.toLowerCase()}">${severity}</span></td>
                <td>${isNotifiable ? '<span class="notifiable-badge-small">⚠️ Yes</span>' : 'No'}</td>
                <td>${affectedCount}</td>
                <td class="table-actions">${actionsHtml}</td>
            </tr>
        `;
    }

    // Modal Functions
    let currentReportId = null;

    async function openViewModal(reportId) {
        currentReportId = reportId;
        const report = allReports.find(r => String(r.id) === String(reportId));

        if (!report) {
            console.error('Report not found:', reportId);
            return;
        }

        // Show/hide edit and delete buttons based on ownership
        if (elements.editFromViewBtn) {
            elements.editFromViewBtn.style.display = isOwnFarm ? '' : 'none';
        }
        if (elements.deleteFromViewBtn) {
            elements.deleteFromViewBtn.style.display = isOwnFarm ? '' : 'none';
        }

        // Build modal content
        const diseaseName = report.effectiveDiseaseName || report.diseaseName || 'Unknown Disease';
        const animalType = report.animalTypeName || 'Unknown';
        const severity = report.effectiveSeverity || report.severity || 'Unknown';
        const date = formatDate(report.reportDate || report.createdAt);
        const isNotifiable = report.effectiveNotifiable ?? report.notifiable ?? false;
        const symptoms = report.effectiveSymptoms || report.symptoms || 'Not specified';
        const treatment = report.effectiveTreatment || report.treatment || 'Not specified';
        const notes = report.notes || 'No notes';
        const affectedCount = report.numberOfAffectedAnimals || 0;
        const imageUrl = report.imageUrl;

        elements.viewModalBody.innerHTML = `
            ${imageUrl ? `
                <div class="photo-card">
                    <img src="${imageUrl}" alt="${diseaseName}">
                </div>
            ` : ''}
            
            <div class="view-info-section">
                <h4 class="view-section-title">Disease Information</h4>
                <div class="view-info-grid">
                    <div class="view-info-item">
                        <span class="info-label">Disease</span>
                        <span class="info-value">${diseaseName}</span>
                    </div>
                    <div class="view-info-item">
                        <span class="info-label">Animal Type</span>
                        <span class="info-value">${animalType}</span>
                    </div>
                    <div class="view-info-item">
                        <span class="info-label">Severity</span>
                        <span class="severity-badge ${severity.toLowerCase()}">${severity}</span>
                    </div>
                    <div class="view-info-item">
                        <span class="info-label">Notifiable</span>
                        <span class="info-value">${isNotifiable ? 'Yes' : 'No'}</span>
                    </div>
                    <div class="view-info-item">
                        <span class="info-label">Report Date</span>
                        <span class="info-value">${date}</span>
                    </div>
                    <div class="view-info-item">
                        <span class="info-label">Affected Animals</span>
                        <span class="info-value">${affectedCount}</span>
                    </div>
                </div>
            </div>

            <div class="view-info-section">
                <h4 class="view-section-title">Symptoms</h4>
                <p class="info-text">${symptoms}</p>
            </div>

            <div class="view-info-section">
                <h4 class="view-section-title">Treatment</h4>
                <p class="info-text">${treatment}</p>
            </div>

            <div class="view-info-section">
                <h4 class="view-section-title">Notes</h4>
                <p class="info-text">${notes}</p>
            </div>
        `;

        elements.viewModal?.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    function closeViewModal() {
        elements.viewModal?.classList.add('hidden');
        document.body.style.overflow = '';
        currentReportId = null;
    }

    function onEditFromView() {
        if (currentReportId) {
            const reportId = currentReportId; // Save before close clears it
            closeViewModal();
            openEditModal(reportId);
        }
    }

    function onDeleteFromView() {
        if (currentReportId) {
            const reportId = currentReportId; // Save before close clears it
            closeViewModal();
            openDeleteModal(reportId);
        }
    }

    function openEditModal(reportId) {
        const report = allReports.find(r => String(r.id) === String(reportId));
        if (!report) {
            console.error('Report not found for editing:', reportId);
            return;
        }

        currentReportId = reportId;

        // Populate read-only fields
        elements.editDiseaseName.textContent = report.effectiveDiseaseName || report.diseaseName || 'Unknown Disease';
        elements.editAnimalType.textContent = report.animalTypeName || 'Unknown';

        // Populate editable fields
        elements.editReportId.value = reportId;

        // Format date for input
        const reportDate = report.reportDate || report.createdAt;
        if (reportDate) {
            const date = new Date(reportDate);
            elements.editReportDate.value = date.toISOString().split('T')[0];
        }

        elements.editAffectedCount.value = report.numberOfAffectedAnimals || '';
        elements.editOutcome.value = report.outcome || 'ONGOING';
        elements.editSymptoms.value = report.effectiveSymptoms || report.symptoms || '';
        elements.editDiagnosis.value = report.diagnosis || '';
        elements.editTreatment.value = report.effectiveTreatment || report.treatment || '';
        elements.editNotes.value = report.notes || '';

        elements.editModal?.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    function closeEditModal() {
        elements.editModal?.classList.add('hidden');
        document.body.style.overflow = '';
        elements.editReportForm?.reset();
    }

    async function handleEditSubmit(e) {
        e.preventDefault();

        if (!currentReportId) return;

        const saveBtn = document.getElementById('saveEditBtn');
        try {
            saveBtn.disabled = true;
            saveBtn.textContent = 'Saving...';

            // Get the original report to preserve required fields
            const originalReport = allReports.find(r => String(r.id) === String(currentReportId));
            if (!originalReport) {
                throw new Error('Original report not found');
            }

            // Build request with all required fields
            const reportData = {
                farmId: originalReport.farmId,
                diseaseId: originalReport.diseaseId || null,
                animalTypeId: originalReport.animalTypeId,
                reportDate: elements.editReportDate.value,
                numberOfAffectedAnimals: elements.editAffectedCount.value ? parseInt(elements.editAffectedCount.value) : null,
                outcome: elements.editOutcome.value,
                symptoms: elements.editSymptoms.value || null,
                diagnosis: elements.editDiagnosis.value || null,
                treatment: elements.editTreatment.value || null,
                notes: elements.editNotes.value || null,
                // Preserve custom disease info if it exists
                customDiseaseName: originalReport.customDiseaseName || null,
                customSymptoms: originalReport.customSymptoms || null,
                customTreatment: originalReport.customTreatment || null
            };

            // Backend expects multipart/form-data with 'report' part
            const formData = new FormData();
            formData.append('report', new Blob([JSON.stringify(reportData)], { type: 'application/json' }));

            const response = await fetch(`/api/vet/disease-reports/${currentReportId}`, {
                method: 'PUT',
                body: formData,
                credentials: 'same-origin'
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Update error response:', errorText);
                throw new Error(`Failed to update: ${response.status}`);
            }

            const updatedReport = await response.json();

            // Update local arrays
            const index = allReports.findIndex(r => String(r.id) === String(currentReportId));
            if (index !== -1) {
                allReports[index] = { ...allReports[index], ...updatedReport };
            }
            const filteredIndex = filteredReports.findIndex(r => String(r.id) === String(currentReportId));
            if (filteredIndex !== -1) {
                filteredReports[filteredIndex] = { ...filteredReports[filteredIndex], ...updatedReport };
            }

            closeEditModal();
            renderReports();
            showToast('Report updated successfully', 'success');
        } catch (error) {
            console.error('Error updating report:', error);
            showToast('Failed to update report', 'error');
        } finally {
            saveBtn.disabled = false;
            saveBtn.textContent = 'Save Changes';
        }
    }

    function openDeleteModal(reportId) {
        currentReportId = reportId;
        elements.deleteModal?.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    function closeDeleteModal() {
        elements.deleteModal?.classList.add('hidden');
        document.body.style.overflow = '';
        currentReportId = null;
    }

    async function confirmDelete() {
        if (!currentReportId) return;

        try {
            elements.confirmDeleteBtn.disabled = true;
            elements.confirmDeleteBtn.textContent = 'Deleting...';

            await fetchAPI(`/api/vet/disease-reports/${currentReportId}`, {
                method: 'DELETE'
            });

            // Remove from local arrays
            allReports = allReports.filter(r => String(r.id) !== String(currentReportId));
            filteredReports = filteredReports.filter(r => String(r.id) !== String(currentReportId));

            closeDeleteModal();
            updateReportsCount();
            populateFilters();
            renderReports();

            showToast('Report deleted successfully', 'success');
        } catch (error) {
            console.error('Error deleting report:', error);
            showToast('Failed to delete report', 'error');
        } finally {
            elements.confirmDeleteBtn.disabled = false;
            elements.confirmDeleteBtn.textContent = 'Delete';
        }
    }

    // Navigation
    function redirectToNewReport() {
        // Redirect to disease reporting page with farm pre-selected
        window.location.href = `/vet/disease-reporting?farmId=${farmId}`;
    }

    function redirectToEditReport(reportId) {
        window.location.href = `/vet/disease-reporting?reportId=${reportId}`;
    }

    // Utilities
    async function fetchAPI(url, options = {}) {
        const response = await fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });

        if (!response.ok) {
            throw new Error(`API error: ${response.status}`);
        }

        if (response.status === 204) {
            return null;
        }

        return response.json();
    }

    function formatDate(dateString) {
        if (!dateString) return 'Unknown';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    function showLoading() {
        elements.loadingState.style.display = '';
        elements.reportsGridContainer.style.display = 'none';
        elements.reportsTableContainer.style.display = 'none';
        elements.emptyState.style.display = 'none';
    }

    function hideLoading() {
        elements.loadingState.style.display = 'none';
    }

    function showError(message) {
        elements.loadingState.innerHTML = `
            <div class="error-state">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                <h3>Error</h3>
                <p>${message}</p>
                <a href="/vet/disease-reporting" class="btn btn-primary">Back to Farms</a>
            </div>
        `;
    }

    function showToast(message, type = 'info') {
        // Simple toast implementation
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            bottom: 24px;
            right: 24px;
            padding: 12px 24px;
            background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6'};
            color: white;
            border-radius: 8px;
            font-size: 14px;
            z-index: 9999;
            animation: slideIn 0.3s ease;
        `;
        document.body.appendChild(toast);

        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
});
