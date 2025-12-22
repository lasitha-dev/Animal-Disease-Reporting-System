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
    let otherFarms = []; // Farms registered by other vets
    let otherFarmsLoaded = false; // Track if other farms have been loaded
    let activeTab = 'my-farms'; // Current active tab
    let editingFarmId = null; // Track which farm is being edited

    // Map State
    let map = null;
    let marker = null;
    let mapInitialized = false;
    let searchAutocomplete = null;
    let geocoder = null; // For reverse geocoding

    // DOM Elements - My Farms
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

    // DOM Elements - Tab Navigation
    const tabButtons = document.querySelectorAll('.tab-btn');
    const myFarmsTab = document.getElementById('my-farms-tab');
    const otherFarmsTab = document.getElementById('other-farms-tab');

    // DOM Elements - Other Farms
    const otherFarmsGrid = document.getElementById('other-farms-grid');
    const otherFarmsEmptyState = document.getElementById('other-farms-empty-state');
    const otherFarmsLoadingState = document.getElementById('other-farms-loading-state');

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

    // Map Elements
    const locationMapContainer = document.getElementById('locationMap');
    const useMyLocationBtn = document.getElementById('useMyLocationBtn');
    const coordinatesDisplay = document.getElementById('coordinatesDisplay');
    const gpsLatitudeInput = document.getElementById('gpsLatitude');
    const gpsLongitudeInput = document.getElementById('gpsLongitude');
    const locationSearchInput = document.getElementById('locationSearchInput');

    // Address auto-population elements
    const addressField = document.getElementById('address');
    const addressAutoIndicator = document.getElementById('addressAutoIndicator');

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

        // Tab switching
        tabButtons.forEach(btn => {
            btn.addEventListener('click', handleTabSwitch);
        });

        // Form events
        farmTypeSelect?.addEventListener('change', handleFarmTypeChange);
        provinceSelect?.addEventListener('change', handleProvinceChange);
        addAnimalTagBtn?.addEventListener('click', handleAddAnimalTag);
        form?.addEventListener('submit', handleFormSubmit);
        ownerContactInput?.addEventListener('input', handleContactInput);

        // Map controls
        useMyLocationBtn?.addEventListener('click', handleUseMyLocation);

        // Escape key closes modal
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && !modal.classList.contains('hidden')) {
                closeModal();
            }
        });
    }

    /**
     * Handle tab switching
     */
    function handleTabSwitch(e) {
        const tabId = e.currentTarget.dataset.tab;
        if (tabId === activeTab) return;

        // Update active tab state
        activeTab = tabId;

        // Update tab button styles
        tabButtons.forEach(btn => {
            btn.classList.toggle('active', btn.dataset.tab === tabId);
        });

        // Show/hide tab content
        if (tabId === 'my-farms') {
            myFarmsTab?.classList.add('active');
            otherFarmsTab?.classList.remove('active');
        } else {
            myFarmsTab?.classList.remove('active');
            otherFarmsTab?.classList.add('active');

            // Load other farms on first view
            if (!otherFarmsLoaded) {
                loadOtherFarms();
            }
        }
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

        // Attach event listeners to edit/delete buttons
        attachFarmCardEventListeners();
    }

    /**
     * Load other vets' farms from API
     */
    async function loadOtherFarms() {
        showOtherFarmsLoading();

        try {
            const response = await fetch('/api/vet/farms/others', { headers: getHeaders() });

            if (!response.ok) throw new Error('Failed to load other farms');

            otherFarms = await response.json();
            otherFarmsLoaded = true;
            renderOtherFarms();
        } catch (error) {
            console.error('Error loading other farms:', error);
            showOtherFarmsEmpty();
        }
    }

    /**
     * Render other farms grid
     */
    function renderOtherFarms() {
        hideOtherFarmsLoading();

        if (!otherFarms || otherFarms.length === 0) {
            showOtherFarmsEmpty();
            return;
        }

        hideOtherFarmsEmpty();
        otherFarmsGrid.innerHTML = otherFarms.map(farm => createOtherFarmCard(farm)).join('');
    }

    /**
     * Create farm card HTML for other vets' farms (view-only, no edit/delete)
     */
    function createOtherFarmCard(farm) {
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
                <div class="farm-card-registered-by">
                    <span class="vet-icon">🩺</span>
                    <span>Registered by:</span>
                    <span class="vet-name">${escapeHtml(farm.createdByUsername || 'Unknown')}</span>
                </div>
            </div>
        `;
    }

    /**
     * Show/hide states for other farms tab
     */
    function showOtherFarmsLoading() {
        otherFarmsLoadingState.style.display = 'block';
        otherFarmsGrid.style.display = 'none';
        otherFarmsEmptyState.style.display = 'none';
    }

    function hideOtherFarmsLoading() {
        otherFarmsLoadingState.style.display = 'none';
        otherFarmsGrid.style.display = 'grid';
    }

    function showOtherFarmsEmpty() {
        otherFarmsLoadingState.style.display = 'none';
        otherFarmsGrid.style.display = 'none';
        otherFarmsEmptyState.style.display = 'block';
    }

    function hideOtherFarmsEmpty() {
        otherFarmsEmptyState.style.display = 'none';
    }

    /**
     * Attach event listeners to farm card action buttons
     */
    function attachFarmCardEventListeners() {
        // Edit buttons
        farmsGrid.querySelectorAll('.farm-edit-btn').forEach(btn => {
            btn.addEventListener('click', handleEditFarm);
        });

        // Delete buttons
        farmsGrid.querySelectorAll('.farm-delete-btn').forEach(btn => {
            btn.addEventListener('click', handleDeleteFarm);
        });
    }

    /**
     * Handle edit farm button click
     */
    async function handleEditFarm(e) {
        const farmId = e.target.dataset.farmId;
        editingFarmId = farmId;

        // Find the farm in our local state
        const farm = farms.find(f => f.id === farmId);
        if (!farm) return;

        // Open modal and populate form
        openModal();

        // Update modal title
        document.querySelector('.modal-title').textContent = 'Edit Farm';
        document.querySelector('#submitBtn .btn-text').textContent = 'Update Farm';

        // Populate form fields
        document.getElementById('farmName').value = farm.farmName || '';
        document.getElementById('description').value = farm.description || '';
        document.getElementById('ownerName').value = farm.ownerName || '';
        document.getElementById('ownerContact').value = (farm.ownerContact || '').replace('+94', '');
        document.getElementById('address').value = farm.address || '';
        // Store coordinates for map initialization
        gpsLatitudeInput.value = farm.gpsLatitude || '';
        gpsLongitudeInput.value = farm.gpsLongitude || '';

        // Set farm type
        farmTypeSelect.value = farm.farmTypeId || '';

        // Set province and load districts
        provinceSelect.value = farm.province || '';
        if (farm.province) {
            await loadDistrictsForProvince(farm.province);
            districtSelect.value = farm.district || '';
        }

        // Set animal tags
        selectedAnimalTags = (farm.animalTags || []).map(tag => ({
            animalTypeId: tag.animalTypeId,
            animalTypeName: tag.animalTypeName,
            count: tag.count
        }));
        renderSelectedTags();
        populateAnimalTypeDropdown();
    }

    /**
     * Load districts for a province (helper for edit mode)
     */
    async function loadDistrictsForProvince(provinceName) {
        try {
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
        }
    }

    /**
     * Handle delete farm button click
     */
    async function handleDeleteFarm(e) {
        const farmId = e.target.dataset.farmId;
        const farm = farms.find(f => f.id === farmId);

        if (!farm) return;

        // Confirm deletion
        if (!confirm(`Are you sure you want to delete "${farm.farmName}"? This action cannot be undone.`)) {
            return;
        }

        try {
            const response = await fetch(`/api/vet/farms/${farmId}`, {
                method: 'DELETE',
                headers: getHeaders()
            });

            if (!response.ok) {
                throw new Error('Failed to delete farm');
            }

            // Reload farms
            loadFarms();
        } catch (error) {
            console.error('Error deleting farm:', error);
            alert('Failed to delete farm. Please try again.');
        }
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
                <div class="farm-card-actions">
                    <button type="button" class="btn btn-sm btn-outline farm-edit-btn" data-farm-id="${farm.id}">
                        ✏️ Edit
                    </button>
                    <button type="button" class="btn btn-sm btn-danger-outline farm-delete-btn" data-farm-id="${farm.id}">
                        🗑️ Delete
                    </button>
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
        // Don't call resetForm here - it clears data we may have set in handleEditFarm
        // Only reset modal title for new farms
        if (!editingFarmId) {
            resetForm();
            document.querySelector('.modal-title').textContent = 'Register New Farm';
            document.querySelector('#submitBtn .btn-text').textContent = 'Register Farm';
        }
        // Initialize map when modal opens (needs DOM to be visible)
        setTimeout(() => {
            initializeMap();
        }, 100);
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
        editingFarmId = null; // Clear edit mode
        renderSelectedTags();
        populateAnimalTypeDropdown();
        hideMessage();
        clearErrors();
        districtSelect.innerHTML = '<option value="">Select province first...</option>';
        districtSelect.disabled = true;
        setLoading(false);
        // Reset modal title
        document.querySelector('.modal-title').textContent = 'Register New Farm';
        document.querySelector('#submitBtn .btn-text').textContent = 'Register Farm';
        // Reset map
        resetMapMarker();
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
        const isEditing = !!editingFarmId;
        const url = isEditing ? `/api/vet/farms/${editingFarmId}` : '/api/vet/farms';
        const method = isEditing ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    ...getHeaders()
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Failed to ${isEditing ? 'update' : 'register'} farm`);
            }

            showMessage(`Farm ${isEditing ? 'updated' : 'registered'} successfully!`, 'success');

            setTimeout(() => {
                closeModal();
                loadFarms(); // Reload farms grid
            }, 1000);

        } catch (error) {
            console.error(`Error ${isEditing ? 'updating' : 'registering'} farm:`, error);
            showMessage(error.message || `Failed to ${isEditing ? 'update' : 'register'} farm`, 'error');
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

        // GPS location is required
        if (!gpsLatitudeInput.value || !gpsLongitudeInput.value) {
            showFieldError('gpsLocation', 'Please select a location on the map');
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
            gpsLatitude: gpsLatitudeInput.value || null,
            gpsLongitude: gpsLongitudeInput.value || null,
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

    // ========================================
    // MAP PICKER FUNCTIONS
    // ========================================

    /**
     * Initialize Google Map
     */
    function initializeMap() {
        if (!locationMapContainer || !window.google || !window.google.maps) {
            console.warn('Google Maps not loaded yet');
            return;
        }

        // Get config from window.mapConfig (set by Thymeleaf)
        const config = window.mapConfig || {};
        const defaultLat = config.defaultLat || 7.8731;
        const defaultLng = config.defaultLng || 80.7718;
        const defaultZoom = config.defaultZoom || 7;

        // Check if we have existing coordinates (edit mode)
        const existingLat = parseFloat(gpsLatitudeInput?.value);
        const existingLng = parseFloat(gpsLongitudeInput?.value);

        const centerLat = !isNaN(existingLat) ? existingLat : defaultLat;
        const centerLng = !isNaN(existingLng) ? existingLng : defaultLng;
        const zoomLevel = !isNaN(existingLat) ? 15 : defaultZoom;

        // Create map centered on Sri Lanka (or existing location)
        map = new google.maps.Map(locationMapContainer, {
            center: { lat: centerLat, lng: centerLng },
            zoom: zoomLevel,
            mapTypeControl: true,
            mapTypeControlOptions: {
                style: google.maps.MapTypeControlStyle.DROPDOWN_MENU,
                position: google.maps.ControlPosition.TOP_RIGHT
            },
            streetViewControl: false,
            fullscreenControl: true
        });

        // Mark as loaded (removes "Loading..." text)
        locationMapContainer.classList.add('loaded');

        // Initialize Geocoder for reverse geocoding
        geocoder = new google.maps.Geocoder();

        // Add click listener
        map.addListener('click', handleMapClick);

        // Initialize Places Autocomplete for search
        initPlacesAutocomplete();

        // If editing and has coordinates, place marker (skip reverse geocode to preserve existing address)
        if (!isNaN(existingLat) && !isNaN(existingLng)) {
            placeMarker({ lat: existingLat, lng: existingLng }, true);
        }

        mapInitialized = true;
    }

    /**
     * Initialize Places Autocomplete for location search
     */
    function initPlacesAutocomplete() {
        if (!locationSearchInput || !window.google?.maps?.places) {
            console.warn('Places API not available');
            return;
        }

        // Sri Lanka bounds for biasing results
        const sriLankaBounds = new google.maps.LatLngBounds(
            new google.maps.LatLng(5.916, 79.652),  // SW corner
            new google.maps.LatLng(9.835, 81.879)   // NE corner
        );

        searchAutocomplete = new google.maps.places.Autocomplete(locationSearchInput, {
            bounds: sriLankaBounds,
            componentRestrictions: { country: 'lk' },
            fields: ['geometry', 'name', 'formatted_address'],
            strictBounds: false
        });

        // Handle place selection
        searchAutocomplete.addListener('place_changed', handlePlaceSelect);

        // Prevent form submission when pressing Enter in search
        locationSearchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
            }
        });
    }

    /**
     * Handle place selection from autocomplete
     */
    function handlePlaceSelect() {
        const place = searchAutocomplete.getPlace();

        if (!place.geometry || !place.geometry.location) {
            console.warn('Place has no geometry');
            return;
        }

        const location = {
            lat: place.geometry.location.lat(),
            lng: place.geometry.location.lng()
        };

        // Center map on selected place
        map.setCenter(location);
        map.setZoom(15);

        // Place marker (skip reverse geocode since we have formatted_address from Places)
        placeMarker(location, true);

        // Use the formatted_address from Places API directly
        if (place.formatted_address) {
            updateAddressField(place.formatted_address);
        }
    }

    /**
     * Handle map click - place/move marker
     */
    function handleMapClick(event) {
        const location = {
            lat: event.latLng.lat(),
            lng: event.latLng.lng()
        };
        placeMarker(location);
    }

    /**
     * Place or move marker on map
     */
    function placeMarker(location, skipReverseGeocode = false) {
        if (marker) {
            marker.setPosition(location);
        } else {
            marker = new google.maps.Marker({
                position: location,
                map: map,
                draggable: true,
                animation: google.maps.Animation.DROP,
                title: 'Farm Location'
            });

            // Add drag end listener
            marker.addListener('dragend', handleMarkerDrag);
        }

        // Update hidden inputs
        gpsLatitudeInput.value = location.lat.toFixed(8);
        gpsLongitudeInput.value = location.lng.toFixed(8);

        // Update display
        updateCoordinateDisplay(location.lat, location.lng);

        // Reverse geocode to get address (unless skipped, e.g., from Places autocomplete)
        if (!skipReverseGeocode) {
            reverseGeocode(location.lat, location.lng);
        }
    }

    /**
     * Handle marker drag end
     */
    function handleMarkerDrag(event) {
        const location = {
            lat: event.latLng.lat(),
            lng: event.latLng.lng()
        };

        gpsLatitudeInput.value = location.lat.toFixed(8);
        gpsLongitudeInput.value = location.lng.toFixed(8);

        updateCoordinateDisplay(location.lat, location.lng);

        // Reverse geocode to update address
        reverseGeocode(location.lat, location.lng);
    }

    /**
     * Handle "Use My Location" button click
     */
    function handleUseMyLocation() {
        if (!navigator.geolocation) {
            alert('Geolocation is not supported by your browser');
            return;
        }

        // Update button to show loading
        const originalText = useMyLocationBtn.innerHTML;
        useMyLocationBtn.innerHTML = '⏳ Getting location...';
        useMyLocationBtn.disabled = true;

        navigator.geolocation.getCurrentPosition(
            (position) => {
                const location = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };

                // Center map on location
                if (map) {
                    map.setCenter(location);
                    map.setZoom(15);
                }

                // Place marker
                placeMarker(location);

                // Reset button
                useMyLocationBtn.innerHTML = originalText;
                useMyLocationBtn.disabled = false;
            },
            (error) => {
                let message = 'Unable to get your location';
                switch (error.code) {
                    case error.PERMISSION_DENIED:
                        message = 'Location permission denied. Please enable location access.';
                        break;
                    case error.POSITION_UNAVAILABLE:
                        message = 'Location information unavailable.';
                        break;
                    case error.TIMEOUT:
                        message = 'Location request timed out.';
                        break;
                }
                alert(message);

                // Reset button
                useMyLocationBtn.innerHTML = originalText;
                useMyLocationBtn.disabled = false;
            },
            {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 300000
            }
        );
    }

    /**
     * Update coordinate display text
     */
    function updateCoordinateDisplay(lat, lng) {
        if (coordinatesDisplay) {
            coordinatesDisplay.innerHTML = `
                <span class="coord-icon">📍</span>
                <span>Lat: ${lat.toFixed(6)}, Lng: ${lng.toFixed(6)}</span>
            `;
            coordinatesDisplay.classList.add('has-location');
        }
    }

    /**
     * Reverse geocode coordinates to get address
     */
    function reverseGeocode(lat, lng) {
        if (!geocoder) {
            console.warn('Geocoder not initialized');
            return;
        }

        const latlng = { lat: parseFloat(lat), lng: parseFloat(lng) };

        geocoder.geocode({ location: latlng }, (results, status) => {
            if (status === 'OK' && results[0]) {
                updateAddressField(results[0].formatted_address);
            } else {
                console.warn('Geocoder failed:', status);
                // Still show indicator that location was selected
                if (addressAutoIndicator) {
                    addressAutoIndicator.style.display = 'none';
                }
            }
        });
    }

    /**
     * Update address field with auto-populated value
     */
    function updateAddressField(address) {
        if (!addressField) return;

        // Update the address field
        addressField.value = address;
        addressField.classList.add('auto-populated');

        // Show the auto-populated indicator
        if (addressAutoIndicator) {
            addressAutoIndicator.style.display = 'inline-flex';
        }

        // Remove auto-populated class when user manually edits
        addressField.addEventListener('input', function onAddressEdit() {
            addressField.classList.remove('auto-populated');
            // Keep indicator but change text to show it was modified
            if (addressAutoIndicator) {
                addressAutoIndicator.style.display = 'none';
            }
            addressField.removeEventListener('input', onAddressEdit);
        }, { once: true });
    }

    /**
     * Reset address field state
     */
    function resetAddressField() {
        if (addressField) {
            addressField.value = '';
            addressField.classList.remove('auto-populated');
        }
        if (addressAutoIndicator) {
            addressAutoIndicator.style.display = 'none';
        }
    }

    /**
     * Reset map marker (when form is reset)
     */
    function resetMapMarker() {
        if (marker) {
            marker.setMap(null);
            marker = null;
        }

        // Reset hidden inputs
        if (gpsLatitudeInput) gpsLatitudeInput.value = '';
        if (gpsLongitudeInput) gpsLongitudeInput.value = '';

        // Reset search input
        if (locationSearchInput) locationSearchInput.value = '';

        // Reset display
        if (coordinatesDisplay) {
            coordinatesDisplay.innerHTML = '<span>No location selected</span>';
            coordinatesDisplay.classList.remove('has-location');
        }

        // Reset address field state
        resetAddressField();

        // Reset map center if map exists
        if (map && window.mapConfig) {
            map.setCenter({
                lat: window.mapConfig.defaultLat || 7.8731,
                lng: window.mapConfig.defaultLng || 80.7718
            });
            map.setZoom(window.mapConfig.defaultZoom || 7);
        }
    }

    // Initialize when DOM is ready
    document.addEventListener('DOMContentLoaded', init);
})();

/**
 * Global callback for Google Maps API
 * Called when the Google Maps script loads
 */
function initMapCallback() {
    // Maps API is loaded and ready
    // The map will be initialized when modal opens
    console.log('Google Maps API loaded');
}
