/**
 * Farms Page JavaScript
 * Handles farms grid display, modal interactions, and form submission
 */
(function () {
    'use strict';

    // State
    let farmTypes = [];
    let animalTypes = [];
    let selectedAnimalTags = [];
    let farms = [];

    // DOM Elements
    const farmsGrid = document.getElementById('farms-grid');
    const emptyState = document.getElementById('empty-state');
    const loadingState = document.getElementById('loading-state');
    const modal = document.getElementById('farmModal');
    const form = document.getElementById('farm-register-form');
    const openModalBtn = document.getElementById('openRegisterModal');
    const emptyStateBtn = document.getElementById('emptyStateRegisterBtn');
    const closeModalBtn = document.getElementById('closeModal');
    const cancelBtn = document.getElementById('cancelBtn');
    const submitBtn = document.getElementById('submitBtn');
    const formMessage = document.getElementById('form-message');

    // Form Elements
    const farmTypeSelect = document.getElementById('farmTypeId');
    const descriptionField = document.getElementById('description');
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const animalTypeSelect = document.getElementById('animalTypeSelect');
    const animalCountInput = document.getElementById('animalCountInput');
    const addAnimalTagBtn = document.getElementById('addAnimalTagBtn');
    const selectedTagsContainer = document.getElementById('selected-animal-tags');
    const ownerContactInput = document.getElementById('ownerContact');

    // CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    /**
     * Initialize
     */
    function init() {
        loadFarms();
        loadFarmTypes();
        loadProvinces();
        loadAnimalTypes();
        setupEventListeners();
    }

    /**
     * Setup event listeners
     */
    function setupEventListeners() {
        // Modal controls
        openModalBtn?.addEventListener('click', openModal);
        emptyStateBtn?.addEventListener('click', openModal);
        closeModalBtn?.addEventListener('click', closeModal);
        cancelBtn?.addEventListener('click', closeModal);
        modal?.addEventListener('click', handleModalBackdropClick);

        // Form events
        farmTypeSelect?.addEventListener('change', handleFarmTypeChange);
        provinceSelect?.addEventListener('change', handleProvinceChange);
        addAnimalTagBtn?.addEventListener('click', handleAddAnimalTag);
        form?.addEventListener('submit', handleFormSubmit);
        ownerContactInput?.addEventListener('input', handleContactInput);

        // Escape key closes modal
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && !modal.classList.contains('hidden')) {
                closeModal();
            }
        });
    }

    /**
     * Load farms from API
     */
    async function loadFarms() {
        showLoading();

        try {
            const response = await fetch('/api/vet/farms', { headers: getHeaders() });

            if (!response.ok) throw new Error('Failed to load farms');

            farms = await response.json();
            renderFarms();
        } catch (error) {
            console.error('Error loading farms:', error);
            showEmpty();
        }
    }

    /**
     * Render farms grid
     */
    function renderFarms() {
        hideLoading();

        if (!farms || farms.length === 0) {
            showEmpty();
            return;
        }

        hideEmpty();
        farmsGrid.innerHTML = farms.map(farm => createFarmCard(farm)).join('');
    }

    /**
     * Create farm card HTML
     */
    function createFarmCard(farm) {
        return `
            <div class="farm-card" data-farm-id="${farm.id}">
                <div class="farm-card-header">
                    <h3 class="farm-card-title">${escapeHtml(farm.farmName)}</h3>
                    <span class="farm-card-type">${escapeHtml(farm.farmTypeName || 'Unknown')}</span>
                </div>
                <div class="farm-card-info">
                    ${farm.ownerName ? `
                    <div class="farm-card-info-row">
                        <span class="farm-card-info-icon">👤</span>
                        <span>${escapeHtml(farm.ownerName)}</span>
                    </div>
                    ` : ''}
                    <div class="farm-card-info-row">
                        <span class="farm-card-info-icon">📍</span>
                        <span>${escapeHtml(farm.districtDisplayName || farm.district)}, ${escapeHtml(farm.provinceDisplayName || farm.province)}</span>
                    </div>
                    ${farm.ownerContact ? `
                    <div class="farm-card-info-row">
                        <span class="farm-card-info-icon">📞</span>
                        <span>${escapeHtml(farm.ownerContact)}</span>
                    </div>
                    ` : ''}
                </div>
                <div class="farm-card-stats">
                    <div class="farm-card-stat">
                        <div class="farm-card-stat-value">${farm.totalAnimals || 0}</div>
                        <div class="farm-card-stat-label">Animals</div>
                    </div>
                    <div class="farm-card-stat">
                        <div class="farm-card-stat-value">${farm.animalTags?.length || 0}</div>
                        <div class="farm-card-stat-label">Types</div>
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * Show/hide states
     */
    function showLoading() {
        loadingState.style.display = 'block';
        farmsGrid.style.display = 'none';
        emptyState.style.display = 'none';
    }

    function hideLoading() {
        loadingState.style.display = 'none';
        farmsGrid.style.display = 'grid';
    }

    function showEmpty() {
        loadingState.style.display = 'none';
        farmsGrid.style.display = 'none';
        emptyState.style.display = 'block';
    }

    function hideEmpty() {
        emptyState.style.display = 'none';
    }

    /**
     * Modal controls
     */
    function openModal() {
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
        resetForm();
    }

    function closeModal() {
        modal.classList.add('hidden');
        document.body.style.overflow = '';
        resetForm();
    }

    function handleModalBackdropClick(e) {
        if (e.target === modal) {
            closeModal();
        }
    }

    /**
     * Reset form
     */
    function resetForm() {
        form.reset();
        selectedAnimalTags = [];
        renderSelectedTags();
        populateAnimalTypeDropdown();
        hideMessage();
        clearErrors();
        districtSelect.innerHTML = '<option value="">Select province first...</option>';
        districtSelect.disabled = true;
        setLoading(false);
    }

    /**
     * Load farm types
     */
    async function loadFarmTypes() {
        try {
            const response = await fetch('/api/vet/farm-types', { headers: getHeaders() });
            if (!response.ok) throw new Error('Failed to load farm types');

            farmTypes = await response.json();
            populateFarmTypeDropdown();
        } catch (error) {
            console.error('Error loading farm types:', error);
        }
    }

    function populateFarmTypeDropdown() {
        farmTypeSelect.innerHTML = '<option value="">Select farm type...</option>';
        farmTypes.forEach(type => {
            const option = document.createElement('option');
            option.value = type.id;
            option.textContent = type.typeName;
            option.dataset.description = type.description || '';
            farmTypeSelect.appendChild(option);
        });
    }

    function handleFarmTypeChange(e) {
        const selectedOption = e.target.options[e.target.selectedIndex];
        descriptionField.value = selectedOption.dataset.description || '';
    }

    /**
     * Load provinces
     */
    async function loadProvinces() {
        try {
            const response = await fetch('/api/locations/provinces', { headers: getHeaders() });
            if (!response.ok) throw new Error('Failed to load provinces');

            const provinces = await response.json();
            provinceSelect.innerHTML = '<option value="">Select province...</option>';
            provinces.forEach(p => {
                const option = document.createElement('option');
                option.value = p.value;
                option.textContent = p.label;
                provinceSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error loading provinces:', error);
        }
    }

    async function handleProvinceChange(e) {
        const provinceName = e.target.value;

        if (!provinceName) {
            districtSelect.innerHTML = '<option value="">Select province first...</option>';
            districtSelect.disabled = true;
            return;
        }

        try {
            districtSelect.innerHTML = '<option value="">Loading...</option>';
            districtSelect.disabled = true;

            const response = await fetch(`/api/locations/districts?provinceName=${provinceName}`, { headers: getHeaders() });
            if (!response.ok) throw new Error('Failed to load districts');

            const districts = await response.json();
            districtSelect.innerHTML = '<option value="">Select district...</option>';
            districts.forEach(d => {
                const option = document.createElement('option');
                option.value = d.value;
                option.textContent = d.label;
                districtSelect.appendChild(option);
            });
            districtSelect.disabled = false;
        } catch (error) {
            console.error('Error loading districts:', error);
            districtSelect.innerHTML = '<option value="">Failed to load</option>';
        }
    }

    /**
     * Load animal types
     */
    async function loadAnimalTypes() {
        try {
            const response = await fetch('/api/vet/animal-types', { headers: getHeaders() });
            if (!response.ok) throw new Error('Failed to load animal types');

            animalTypes = await response.json();
            populateAnimalTypeDropdown();
        } catch (error) {
            console.error('Error loading animal types:', error);
        }
    }

    function populateAnimalTypeDropdown() {
        animalTypeSelect.innerHTML = '<option value="">Select animal type...</option>';
        animalTypes.forEach(type => {
            if (selectedAnimalTags.some(tag => tag.animalTypeId === type.id)) return;
            const option = document.createElement('option');
            option.value = type.id;
            option.textContent = type.typeName;
            animalTypeSelect.appendChild(option);
        });
    }

    function handleAddAnimalTag() {
        const animalTypeId = animalTypeSelect.value;
        const count = parseInt(animalCountInput.value, 10);

        if (!animalTypeId || !count || count < 1) return;

        const animalType = animalTypes.find(t => t.id === animalTypeId);
        if (!animalType) return;

        selectedAnimalTags.push({
            animalTypeId: animalTypeId,
            animalTypeName: animalType.typeName,
            count: count
        });

        renderSelectedTags();
        populateAnimalTypeDropdown();
        animalCountInput.value = '1';
    }

    function renderSelectedTags() {
        selectedTagsContainer.innerHTML = '';

        selectedAnimalTags.forEach((tag, index) => {
            const tagElement = document.createElement('span');
            tagElement.className = 'animal-tag';
            tagElement.innerHTML = `
                🐮 ${tag.animalTypeName}
                <span class="tag-count">${tag.count}</span>
                <button type="button" class="tag-remove" data-index="${index}">✕</button>
            `;
            selectedTagsContainer.appendChild(tagElement);
        });

        selectedTagsContainer.querySelectorAll('.tag-remove').forEach(btn => {
            btn.addEventListener('click', handleRemoveTag);
        });
    }

    function handleRemoveTag(e) {
        const index = parseInt(e.target.dataset.index, 10);
        selectedAnimalTags.splice(index, 1);
        renderSelectedTags();
        populateAnimalTypeDropdown();
    }

    function handleContactInput(e) {
        e.target.value = e.target.value.replace(/[^0-9]/g, '').substring(0, 9);
    }

    /**
     * Form submission
     */
    async function handleFormSubmit(e) {
        e.preventDefault();

        if (!validateForm()) return;

        setLoading(true);
        hideMessage();

        const formData = buildFormData();

        try {
            const response = await fetch('/api/vet/farms', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...getHeaders()
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || 'Failed to register farm');
            }

            showMessage('Farm registered successfully!', 'success');

            setTimeout(() => {
                closeModal();
                loadFarms(); // Reload farms grid
            }, 1000);

        } catch (error) {
            console.error('Error registering farm:', error);
            showMessage(error.message || 'Failed to register farm', 'error');
            setLoading(false);
        }
    }

    function validateForm() {
        let isValid = true;
        clearErrors();

        if (!document.getElementById('farmName').value.trim()) {
            showFieldError('farmName', 'Farm name is required');
            isValid = false;
        }

        if (!farmTypeSelect.value) {
            isValid = false;
        }

        if (!provinceSelect.value) {
            isValid = false;
        }

        if (!districtSelect.value) {
            isValid = false;
        }

        if (!document.getElementById('address').value.trim()) {
            isValid = false;
        }

        return isValid;
    }

    function buildFormData() {
        const contactNumber = ownerContactInput.value.trim();

        return {
            farmName: document.getElementById('farmName').value.trim(),
            farmTypeId: farmTypeSelect.value,
            description: descriptionField.value.trim(),
            ownerName: document.getElementById('ownerName').value.trim() || null,
            ownerContact: contactNumber ? `+94${contactNumber}` : null,
            address: document.getElementById('address').value.trim(),
            province: provinceSelect.value,
            district: districtSelect.value,
            gpsLatitude: document.getElementById('gpsLatitude').value || null,
            gpsLongitude: document.getElementById('gpsLongitude').value || null,
            animalTags: selectedAnimalTags.map(tag => ({
                animalTypeId: tag.animalTypeId,
                count: tag.count
            }))
        };
    }

    /**
     * Helpers
     */
    function getHeaders() {
        const headers = {};
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }
        return headers;
    }

    function showMessage(message, type) {
        formMessage.textContent = message;
        formMessage.className = `alert alert-${type}`;
        formMessage.style.display = 'block';
    }

    function hideMessage() {
        formMessage.style.display = 'none';
    }

    function showFieldError(fieldId, message) {
        const errorSpan = document.getElementById(`${fieldId}-error`);
        if (errorSpan) {
            errorSpan.textContent = message;
            errorSpan.classList.add('visible');
        }
    }

    function clearErrors() {
        document.querySelectorAll('.form-error').forEach(el => {
            el.textContent = '';
            el.classList.remove('visible');
        });
    }

    function setLoading(loading) {
        submitBtn.disabled = loading;
        submitBtn.querySelector('.btn-text').style.display = loading ? 'none' : 'inline';
        submitBtn.querySelector('.btn-loading').style.display = loading ? 'inline-flex' : 'none';
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Initialize when DOM is ready
    document.addEventListener('DOMContentLoaded', init);
})();
