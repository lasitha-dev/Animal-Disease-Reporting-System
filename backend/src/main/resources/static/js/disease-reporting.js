/**
 * Disease Reporting Page JavaScript
 * Handles form interactions, cascading dropdowns, image upload, view/edit/delete operations
 */

(function () {
    'use strict';

    // State
    let farms = [];
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

        // Dropdowns
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
        editDiseaseDescription: document.getElementById('editDiseaseDescription'),

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
        otherReportsContainer: document.getElementById('other-reports-table-container'),
        otherLoadingState: document.getElementById('other-loading-state'),
        otherEmptyState: document.getElementById('other-empty-state')
    };

    // CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    // Initialize
    document.addEventListener('DOMContentLoaded', init);

    function init() {
        setupEventListeners();
        setDefaultDate();
        loadFarms();
        loadReports();
        handleUrlParams();
        setupTabListeners();
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

        // Cascading dropdowns
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

            // Load other reports on first switch
            if (!otherReportsLoaded) {
                loadOtherReports();
            }
        }
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

            if (reports.length === 0) {
                showOtherEmptyState();
            } else {
                renderOtherReportsTable(reports);
                showOtherReportsTable();
            }
            otherReportsLoaded = true;
        } catch (error) {
            console.error('Error loading other reports:', error);
            showOtherEmptyState();
        }
    }

    function showOtherLoadingState() {
        if (elements.otherReportsContainer) elements.otherReportsContainer.innerHTML = '';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'none';
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'block';
    }

    function showOtherEmptyState() {
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'none';
        if (elements.otherReportsContainer) elements.otherReportsContainer.innerHTML = '';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'block';
    }

    function showOtherReportsTable() {
        if (elements.otherLoadingState) elements.otherLoadingState.style.display = 'none';
        if (elements.otherEmptyState) elements.otherEmptyState.style.display = 'none';
    }

    function renderOtherReportsTable(reports) {
        elements.otherReportsContainer.innerHTML = '';

        const cardsGrid = document.createElement('div');
        cardsGrid.className = 'reports-cards-grid';

        reports.forEach(report => {
            const card = document.createElement('div');
            card.className = 'report-card other-vet-card';

            const date = new Date(report.reportDate).toLocaleDateString();
            const severityClass = report.severity?.toLowerCase() || '';
            const statusClass = report.isConfirmed ? 'confirmed' : 'pending';
            const statusText = report.isConfirmed ? 'Confirmed' : 'Pending';

            const imageHtml = report.imageUrl
                ? `<img src="${report.imageUrl}" alt="Disease Photo" class="report-card-image">`
                : `<div class="report-card-placeholder"><span>🦠</span><span class="placeholder-text">No Image</span></div>`;

            card.innerHTML = `
                <div class="report-card-header">
                    ${imageHtml}
                    <div class="report-card-badges">
                        <span class="severity-badge ${severityClass}">${report.severity || '-'}</span>
                        <span class="status-badge ${statusClass}">${statusText}</span>
                    </div>
                </div>
                <div class="report-card-body">
                    <h4 class="report-card-title">${escapeHtml(report.diseaseName)}</h4>
                    <p class="report-card-subtitle">${escapeHtml(report.animalTypeName)} at ${escapeHtml(report.farmName)}</p>
                    <div class="report-card-meta">
                        <span class="meta-item">📅 ${date}</span>
                        <span class="meta-item">👤 ${escapeHtml(report.reportedByUsername)}</span>
                    </div>
                    ${report.affectedCount ? `<span class="affected-count">🐄 ${report.affectedCount} affected</span>` : ''}
                </div>
                <div class="report-card-footer">
                    <button class="btn btn-outline btn-sm view-btn" data-report-id="${report.id}">
                        👁️ View Details
                    </button>
                </div>
            `;

            // Add click listener for view (no edit/delete for other vets' reports)
            card.querySelector('.view-btn').addEventListener('click', () => {
                isViewingOtherReport = true;
                openViewModal(report.id);
            });

            cardsGrid.appendChild(card);
        });

        elements.otherReportsContainer.appendChild(cardsGrid);
    }

    // ========================================
    // Report Modal Functions
    // ========================================


    function openModal(isEdit = false, reportData = null) {
        editMode = isEdit;
        currentReportId = reportData?.id || null;

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
        elements.animalTypeSelect.disabled = true;
        elements.animalTypeSelect.innerHTML = '<option value="">Select farm first...</option>';
        elements.diseaseSelect.disabled = true;
        elements.diseaseSelect.innerHTML = '<option value="">Select animal type first...</option>';
        elements.diseaseInfoSection.style.display = 'none';
        resetDiseaseInfoEditMode();
        removeImage();
        hideFormMessage();
        currentDiseaseData = null;
        clearExistingImage = false;
    }

    async function populateEditForm(report) {
        // Store values we need to set after async operations
        const farmId = report.farmId || '';
        const animalTypeId = report.animalTypeId || '';
        const diseaseId = report.diseaseId || '';

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

            // Pre-fill with current values
            if (currentDiseaseData) {
                elements.editDiseaseName.value = currentDiseaseData.diseaseName || '';
                elements.editSeverity.value = '';
                elements.editDiseaseDescription.value = '';
            }
        } else {
            elements.diseaseInfoDisplay.style.display = 'block';
            elements.diseaseInfoEdit.style.display = 'none';
            elements.editDiseaseInfoLabel.textContent = '✏️ Edit Info';
        }
    }

    function resetDiseaseInfoEditMode() {
        isDiseaseInfoEditMode = false;
        if (elements.diseaseInfoDisplay) elements.diseaseInfoDisplay.style.display = 'block';
        if (elements.diseaseInfoEdit) elements.diseaseInfoEdit.style.display = 'none';
        if (elements.editDiseaseInfoLabel) elements.editDiseaseInfoLabel.textContent = '✏️ Edit Info';
        if (elements.editDiseaseName) elements.editDiseaseName.value = '';
        if (elements.editSeverity) elements.editSeverity.value = '';
        if (elements.editDiseaseDescription) elements.editDiseaseDescription.value = '';
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

        // Disease Information
        document.getElementById('view-diseaseName').textContent = report.diseaseName || '-';
        document.getElementById('view-diseaseCode').textContent = report.diseaseCode || '-';

        const severityEl = document.getElementById('view-severity');
        if (report.severity) {
            severityEl.innerHTML = `<span class="severity-badge ${report.severity.toLowerCase()}">${report.severity}</span>`;
        } else {
            severityEl.textContent = '-';
        }

        document.getElementById('view-notifiable').textContent = report.isNotifiable ? 'Yes ⚠️' : 'No';

        // Report Details
        document.getElementById('view-reportDate').textContent = report.reportDate
            ? new Date(report.reportDate).toLocaleDateString()
            : '-';

        const statusEl = document.getElementById('view-status');
        if (report.isConfirmed) {
            statusEl.innerHTML = '<span class="status-badge confirmed">Confirmed</span>';
        } else {
            statusEl.innerHTML = '<span class="status-badge pending">Pending</span>';
        }

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
            elements.farmSelect.appendChild(option);
        });
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
    }

    async function onAnimalTypeChange() {
        const animalTypeId = elements.animalTypeSelect.value;

        // Reset disease dropdown
        elements.diseaseSelect.innerHTML = '<option value="">Loading diseases...</option>';
        elements.diseaseInfoSection.style.display = 'none';

        if (!animalTypeId) {
            elements.diseaseSelect.innerHTML = '<option value="">Select animal type first...</option>';
            elements.diseaseSelect.disabled = true;
            currentAnimalCount = null;
            updateAffectedCountMax();
            return;
        }

        // Store the registered animal count for validation
        const selectedOption = elements.animalTypeSelect.options[elements.animalTypeSelect.selectedIndex];
        currentAnimalCount = parseInt(selectedOption.dataset.count) || null;
        updateAffectedCountMax();

        try {
            const diseases = await fetchAPI(`/api/vet/diseases?animalTypeId=${animalTypeId}`);

            elements.diseaseSelect.innerHTML = '<option value="">Select disease...</option>';

            if (diseases.length === 0) {
                elements.diseaseSelect.innerHTML = '<option value="">No diseases registered for this animal type</option>';
                elements.diseaseSelect.disabled = true;
                return;
            }

            elements.diseaseSelect.disabled = false;

            diseases.forEach(disease => {
                const option = document.createElement('option');
                option.value = disease.id;
                option.textContent = disease.diseaseName;
                option.dataset.disease = JSON.stringify(disease);
                elements.diseaseSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading diseases:', error);
            elements.diseaseSelect.innerHTML = '<option value="">Error loading diseases</option>';
            elements.diseaseSelect.disabled = true;
        }
    }

    function onDiseaseChange() {
        const diseaseId = elements.diseaseSelect.value;

        if (!diseaseId) {
            elements.diseaseInfoSection.style.display = 'none';
            currentDiseaseData = null;
            return;
        }

        const selectedOption = elements.diseaseSelect.options[elements.diseaseSelect.selectedIndex];
        const disease = JSON.parse(selectedOption.dataset.disease || '{}');
        currentDiseaseData = disease;

        // Populate disease info display
        elements.displayDiseaseName.textContent = disease.diseaseName || '-';
        elements.displayDiseaseCode.textContent = disease.diseaseCode || '-';

        // Severity badge
        const severity = disease.severity?.toLowerCase() || '';
        elements.displaySeverity.textContent = disease.severity || '-';
        elements.displaySeverity.className = 'info-value severity-badge ' + severity;

        elements.displayNotifiable.textContent = disease.isNotifiable ? 'Yes ⚠️' : 'No';
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
            diseaseId: elements.diseaseSelect.value,
            reportDate: elements.reportDate.value,
            affectedCount: document.getElementById('affectedCount').value ? parseInt(document.getElementById('affectedCount').value) : null,
            symptoms: elements.symptoms.value,
            diagnosis: document.getElementById('diagnosis').value,
            treatment: elements.treatment.value,
            outcome: document.getElementById('outcome').value,
            notes: document.getElementById('notes').value,
            clearImage: editMode && clearExistingImage // Include flag to clear existing image
        };

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
    // Reports table
    // ========================================

    async function loadReports() {
        showLoadingState();

        try {
            const reports = await fetchAPI('/api/vet/disease-reports');

            if (reports.length === 0) {
                showEmptyState();
            } else {
                renderReportsTable(reports);
                showReportsTable();
            }
        } catch (error) {
            console.error('Error loading reports:', error);
            showEmptyState();
        }
    }

    function renderReportsTable(reports) {
        // Clear the reports container and create card grid
        elements.reportsContainer.innerHTML = '';

        // Create cards grid container
        const cardsGrid = document.createElement('div');
        cardsGrid.className = 'reports-cards-grid';

        reports.forEach(report => {
            const card = document.createElement('div');
            card.className = 'report-card';

            // Format date
            const date = new Date(report.reportDate).toLocaleDateString();

            // Severity badge
            const severityClass = report.severity?.toLowerCase() || '';

            // Status badge
            const statusClass = report.isConfirmed ? 'confirmed' : 'pending';
            const statusText = report.isConfirmed ? 'Confirmed' : 'Pending';

            // Image HTML - show actual image or placeholder
            const imageHtml = report.imageUrl
                ? `<img src="${report.imageUrl}" alt="Disease Photo" class="report-card-image">`
                : `<div class="report-card-placeholder"><span>🦠</span><span class="placeholder-text">No Image</span></div>`;

            card.innerHTML = `
                <div class="report-card-media">
                    ${imageHtml}
                </div>
                <div class="report-card-content">
                    <div class="report-card-header">
                        <h4 class="report-card-title">${report.diseaseName || 'Unknown Disease'}</h4>
                        <span class="severity-badge ${severityClass}">${report.severity || '-'}</span>
                    </div>
                    <div class="report-card-meta">
                        <div class="report-card-info">
                            <span class="info-icon">🏠</span>
                            <span>${report.farmName || '-'}</span>
                        </div>
                        <div class="report-card-info">
                            <span class="info-icon">🐄</span>
                            <span>${report.animalTypeName || '-'}</span>
                        </div>
                        <div class="report-card-info">
                            <span class="info-icon">📅</span>
                            <span>${date}</span>
                        </div>
                    </div>
                    <div class="report-card-footer">
                        <span class="status-badge ${statusClass}">${statusText}</span>
                        <button class="btn btn-outline btn-sm view-btn" data-id="${report.id}">
                            👁️ View Details
                        </button>
                    </div>
                </div>
            `;

            // Add click event for view button
            const viewBtn = card.querySelector('.view-btn');
            viewBtn.addEventListener('click', () => openViewModal(report.id));

            cardsGrid.appendChild(card);
        });

        elements.reportsContainer.appendChild(cardsGrid);
    }

    function showLoadingState() {
        elements.loadingState.style.display = 'block';
        elements.emptyState.style.display = 'none';
        elements.reportsContainer.style.display = 'none';
    }

    function showEmptyState() {
        elements.loadingState.style.display = 'none';
        elements.emptyState.style.display = 'block';
        elements.reportsContainer.style.display = 'none';
    }

    function showReportsTable() {
        elements.loadingState.style.display = 'none';
        elements.emptyState.style.display = 'none';
        elements.reportsContainer.style.display = 'block';
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

