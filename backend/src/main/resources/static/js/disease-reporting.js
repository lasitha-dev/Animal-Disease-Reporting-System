/**
 * Disease Reporting Page JavaScript
 * Handles form interactions, cascading dropdowns, image upload, and API calls
 */

(function () {
    'use strict';

    // State
    let farms = [];
    let currentDiseaseData = null;
    let selectedImageFile = null;

    // DOM Elements
    const elements = {
        // Modal
        reportModal: document.getElementById('reportModal'),
        openReportModalBtn: document.getElementById('openReportModal'),
        closeModalBtn: document.getElementById('closeModal'),
        cancelBtn: document.getElementById('cancelBtn'),
        submitBtn: document.getElementById('submitBtn'),

        // Form
        form: document.getElementById('disease-report-form'),
        formMessage: document.getElementById('form-message'),

        // Dropdowns
        farmSelect: document.getElementById('farmId'),
        animalTypeSelect: document.getElementById('animalTypeId'),
        diseaseSelect: document.getElementById('diseaseId'),

        // Disease info display
        diseaseInfoSection: document.getElementById('diseaseInfoSection'),
        displayDiseaseName: document.getElementById('display-diseaseName'),
        displayDiseaseCode: document.getElementById('display-diseaseCode'),
        displaySeverity: document.getElementById('display-severity'),
        displayNotifiable: document.getElementById('display-notifiable'),
        displayDescription: document.getElementById('display-description'),

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

        // Reports table
        reportsTbody: document.getElementById('reports-tbody'),
        loadingState: document.getElementById('loading-state'),
        emptyState: document.getElementById('empty-state'),
        tableContainer: document.getElementById('reports-table-container'),
        emptyStateBtn: document.getElementById('emptyStateReportBtn')
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
    }

    function setupEventListeners() {
        // Modal controls
        elements.openReportModalBtn?.addEventListener('click', openModal);
        elements.closeModalBtn?.addEventListener('click', closeModal);
        elements.cancelBtn?.addEventListener('click', closeModal);
        elements.emptyStateBtn?.addEventListener('click', openModal);
        elements.reportModal?.addEventListener('click', (e) => {
            if (e.target === elements.reportModal) closeModal();
        });

        // Form submission
        elements.form?.addEventListener('submit', handleSubmit);

        // Cascading dropdowns
        elements.farmSelect?.addEventListener('change', onFarmChange);
        elements.animalTypeSelect?.addEventListener('change', onAnimalTypeChange);
        elements.diseaseSelect?.addEventListener('change', onDiseaseChange);

        // Image upload
        elements.imageDropzone?.addEventListener('click', () => elements.imageInput?.click());
        elements.imageInput?.addEventListener('change', handleImageSelect);
        elements.removeImageBtn?.addEventListener('click', removeImage);

        // Drag and drop
        elements.imageDropzone?.addEventListener('dragover', handleDragOver);
        elements.imageDropzone?.addEventListener('dragleave', handleDragLeave);
        elements.imageDropzone?.addEventListener('drop', handleDrop);

        // Escape key to close modal
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && !elements.reportModal?.classList.contains('hidden')) {
                closeModal();
            }
        });
    }

    function setDefaultDate() {
        const today = new Date().toISOString().split('T')[0];
        if (elements.reportDate) {
            elements.reportDate.value = today;
        }
    }

    // Modal functions
    function openModal() {
        elements.reportModal?.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
        resetForm();
    }

    function closeModal() {
        elements.reportModal?.classList.add('hidden');
        document.body.style.overflow = '';
    }

    function resetForm() {
        elements.form?.reset();
        setDefaultDate();
        elements.animalTypeSelect.disabled = true;
        elements.animalTypeSelect.innerHTML = '<option value="">Select farm first...</option>';
        elements.diseaseSelect.disabled = true;
        elements.diseaseSelect.innerHTML = '<option value="">Select animal type first...</option>';
        elements.diseaseInfoSection.style.display = 'none';
        removeImage();
        hideFormMessage();
        currentDiseaseData = null;
    }

    // API Functions
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

        console.log('Farm animals for selected farm:', farmAnimals);

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
            return;
        }

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

        // Auto-fill symptoms and treatment from disease defaults
        if (disease.symptoms) {
            elements.symptoms.value = disease.symptoms;
        }
        if (disease.treatment) {
            elements.treatment.value = disease.treatment;
        }

        elements.diseaseInfoSection.style.display = 'block';
    }

    // Image handling
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
        elements.imageInput.value = '';
        elements.imagePreview.src = '';
        elements.imageDropzone.style.display = 'block';
        elements.imagePreviewContainer.style.display = 'none';
    }

    // Form submission
    async function handleSubmit(e) {
        e.preventDefault();

        // Validate required fields
        if (!elements.farmSelect.value || !elements.animalTypeSelect.value || !elements.diseaseSelect.value) {
            showFormMessage('Please fill in all required fields.', 'error');
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
            notes: document.getElementById('notes').value
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

            const response = await fetch('/api/vet/disease-reports', {
                method: 'POST',
                headers: headers,
                body: formData
            });

            if (!response.ok) {
                const error = await response.json().catch(() => ({ message: 'Failed to submit report' }));
                throw new Error(error.message || 'Failed to submit report');
            }

            const result = await response.json();

            showFormMessage('Disease report submitted successfully!', 'success');

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

    // Reports table
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
        elements.reportsTbody.innerHTML = '';

        reports.forEach(report => {
            const tr = document.createElement('tr');

            // Format date
            const date = new Date(report.reportDate).toLocaleDateString();

            // Severity badge
            const severityClass = report.severity?.toLowerCase() || '';

            // Status badge
            const statusClass = report.isConfirmed ? 'confirmed' : 'pending';
            const statusText = report.isConfirmed ? 'Confirmed' : 'Pending';

            tr.innerHTML = `
                <td>${date}</td>
                <td>${report.farmName || '-'}</td>
                <td>${report.animalTypeName || '-'}</td>
                <td>${report.diseaseName || '-'}</td>
                <td><span class="severity-badge ${severityClass}">${report.severity || '-'}</span></td>
                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                <td>
                    <button class="action-btn view" data-id="${report.id}">View</button>
                </td>
            `;

            elements.reportsTbody.appendChild(tr);
        });
    }

    function showLoadingState() {
        elements.loadingState.style.display = 'block';
        elements.emptyState.style.display = 'none';
        elements.tableContainer.style.display = 'none';
    }

    function showEmptyState() {
        elements.loadingState.style.display = 'none';
        elements.emptyState.style.display = 'block';
        elements.tableContainer.style.display = 'none';
    }

    function showReportsTable() {
        elements.loadingState.style.display = 'none';
        elements.emptyState.style.display = 'none';
        elements.tableContainer.style.display = 'block';
    }

    // Message helpers
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

})();
