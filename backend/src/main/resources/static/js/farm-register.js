/**
 * Farm Registration Form JavaScript
 * Handles form interactions, API calls, and form submission
 */
(function() {
    'use strict';

    // State
    let farmTypes = [];
    let animalTypes = [];
    let selectedAnimalTags = []; // Array of { animalTypeId, animalTypeName, count }

    // DOM Elements
    const form = document.getElementById('farm-register-form');
    const farmTypeSelect = document.getElementById('farmTypeId');
    const descriptionField = document.getElementById('description');
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const animalTypeSelect = document.getElementById('animalTypeSelect');
    const animalCountInput = document.getElementById('animalCountInput');
    const addAnimalTagBtn = document.getElementById('addAnimalTagBtn');
    const selectedTagsContainer = document.getElementById('selected-animal-tags');
    const submitBtn = document.getElementById('submitBtn');
    const formMessage = document.getElementById('form-message');
    const ownerContactInput = document.getElementById('ownerContact');

    // CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    /**
     * Initialize the form
     */
    function init() {
        loadFarmTypes();
        loadProvinces();
        loadAnimalTypes();
        setupEventListeners();
    }

    /**
     * Setup event listeners
     */
    function setupEventListeners() {
        // Farm type change - auto-populate description
        farmTypeSelect.addEventListener('change', handleFarmTypeChange);

        // Province change - load districts
        provinceSelect.addEventListener('change', handleProvinceChange);

        // Add animal tag button
        addAnimalTagBtn.addEventListener('click', handleAddAnimalTag);

        // Form submission
        form.addEventListener('submit', handleFormSubmit);

        // Owner contact input - format as user types
        ownerContactInput.addEventListener('input', handleContactInput);
    }

    /**
     * Load farm types from API
     */
    async function loadFarmTypes() {
        try {
            const response = await fetch('/api/vet/farm-types', {
                headers: getHeaders()
            });
            
            if (!response.ok) throw new Error('Failed to load farm types');
            
            farmTypes = await response.json();
            populateFarmTypeDropdown();
        } catch (error) {
            console.error('Error loading farm types:', error);
            showMessage('Failed to load farm types. Please refresh the page.', 'error');
        }
    }

    /**
     * Populate farm type dropdown
     */
    function populateFarmTypeDropdown() {
        farmTypeSelect.innerHTML = '<option value="">Select farm type...</option>';
        farmTypes.forEach(type => {
            const option = document.createElement('option');
            option.value = type.id;
            option.textContent = type.typeName;
            option.dataset.description = type.description || '';
            farmTypeSelect.appendChild(option);
        });
        // Refresh CustomSelect to reflect new options
        if (window.CustomSelect) {
            CustomSelect.refresh('farmTypeId');
        }
    }

    /**
     * Handle farm type selection - auto-populate description
     */
    function handleFarmTypeChange(e) {
        const selectedOption = e.target.options[e.target.selectedIndex];
        const description = selectedOption.dataset.description || '';
        descriptionField.value = description;
    }

    /**
     * Load provinces from API
     */
    async function loadProvinces() {
        try {
            const response = await fetch('/api/locations/provinces', {
                headers: getHeaders()
            });
            
            if (!response.ok) throw new Error('Failed to load provinces');
            
            const provinces = await response.json();
            populateProvinceDropdown(provinces);
        } catch (error) {
            console.error('Error loading provinces:', error);
            showMessage('Failed to load provinces. Please refresh the page.', 'error');
        }
    }

    /**
     * Populate province dropdown
     */
    function populateProvinceDropdown(provinces) {
        provinceSelect.innerHTML = '<option value="">Select province...</option>';
        provinces.forEach(province => {
            const option = document.createElement('option');
            option.value = province.value;
            option.textContent = province.label;
            provinceSelect.appendChild(option);
        });
        // Refresh CustomSelect to reflect new options
        if (window.CustomSelect) {
            CustomSelect.refresh('province');
        }
    }

    /**
     * Handle province change - load districts
     */
    async function handleProvinceChange(e) {
        const provinceName = e.target.value;
        
        if (!provinceName) {
            districtSelect.innerHTML = '<option value="">Select province first...</option>';
            districtSelect.disabled = true;
            // Refresh CustomSelect to reflect disabled state
            if (window.CustomSelect) {
                CustomSelect.refresh('district');
            }
            return;
        }

        try {
            districtSelect.innerHTML = '<option value="">Loading districts...</option>';
            districtSelect.disabled = true;

            const response = await fetch(`/api/locations/districts?provinceName=${provinceName}`, {
                headers: getHeaders()
            });
            
            if (!response.ok) throw new Error('Failed to load districts');
            
            const districts = await response.json();
            populateDistrictDropdown(districts);
        } catch (error) {
            console.error('Error loading districts:', error);
            districtSelect.innerHTML = '<option value="">Failed to load districts</option>';
        }
    }

    /**
     * Populate district dropdown
     */
    function populateDistrictDropdown(districts) {
        districtSelect.innerHTML = '<option value="">Select district...</option>';
        districts.forEach(district => {
            const option = document.createElement('option');
            option.value = district.value;
            option.textContent = district.label;
            districtSelect.appendChild(option);
        });
        districtSelect.disabled = false;
        // Refresh CustomSelect to reflect new options
        if (window.CustomSelect) {
            CustomSelect.refresh('district');
        }
    }

    /**
     * Load animal types from API
     */
    async function loadAnimalTypes() {
        try {
            const response = await fetch('/api/vet/animal-types', {
                headers: getHeaders()
            });
            
            if (!response.ok) throw new Error('Failed to load animal types');
            
            animalTypes = await response.json();
            populateAnimalTypeDropdown();
        } catch (error) {
            console.error('Error loading animal types:', error);
        }
    }

    /**
     * Populate animal type dropdown
     */
    function populateAnimalTypeDropdown() {
        animalTypeSelect.innerHTML = '<option value="">Select animal type to add...</option>';
        animalTypes.forEach(type => {
            // Skip if already selected
            if (selectedAnimalTags.some(tag => tag.animalTypeId === type.id)) {
                return;
            }
            const option = document.createElement('option');
            option.value = type.id;
            option.textContent = type.typeName;
            animalTypeSelect.appendChild(option);
        });
        // Refresh CustomSelect to reflect new options
        if (window.CustomSelect) {
            CustomSelect.refresh('animalTypeSelect');
        }
    }

    /**
     * Handle adding an animal tag
     */
    function handleAddAnimalTag() {
        const animalTypeId = animalTypeSelect.value;
        const count = parseInt(animalCountInput.value, 10);

        if (!animalTypeId) {
            return;
        }

        if (!count || count < 1) {
            animalCountInput.focus();
            return;
        }

        const animalType = animalTypes.find(t => t.id === animalTypeId);
        if (!animalType) return;

        // Add to selected tags
        selectedAnimalTags.push({
            animalTypeId: animalTypeId,
            animalTypeName: animalType.typeName,
            count: count
        });

        // Update UI
        renderSelectedTags();
        populateAnimalTypeDropdown(); // Refresh to remove selected item
        animalCountInput.value = '1';
    }

    /**
     * Render selected animal tags
     */
    function renderSelectedTags() {
        selectedTagsContainer.innerHTML = '';
        
        selectedAnimalTags.forEach((tag, index) => {
            const tagElement = document.createElement('span');
            tagElement.className = 'animal-tag';
            tagElement.innerHTML = `
                🐮 ${tag.animalTypeName}
                <span class="tag-count">${tag.count}</span>
                <button type="button" class="tag-remove" data-index="${index}" aria-label="Remove">✕</button>
            `;
            selectedTagsContainer.appendChild(tagElement);
        });

        // Add click handlers for remove buttons
        selectedTagsContainer.querySelectorAll('.tag-remove').forEach(btn => {
            btn.addEventListener('click', handleRemoveTag);
        });
    }

    /**
     * Handle removing an animal tag
     */
    function handleRemoveTag(e) {
        const index = parseInt(e.target.dataset.index, 10);
        selectedAnimalTags.splice(index, 1);
        renderSelectedTags();
        populateAnimalTypeDropdown(); // Refresh to add removed item back
    }

    /**
     * Handle contact number input
     */
    function handleContactInput(e) {
        // Only allow digits
        e.target.value = e.target.value.replace(/[^0-9]/g, '').substring(0, 9);
    }

    /**
     * Handle form submission
     */
    async function handleFormSubmit(e) {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }

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

            const result = await response.json();
            showMessage('Farm registered successfully! Redirecting...', 'success');
            
            // Redirect to dashboard after success
            setTimeout(() => {
                window.location.href = '/vet/dashboard';
            }, 1500);

        } catch (error) {
            console.error('Error registering farm:', error);
            showMessage(error.message || 'Failed to register farm. Please try again.', 'error');
            setLoading(false);
        }
    }

    /**
     * Validate form
     */
    function validateForm() {
        let isValid = true;
        clearErrors();

        // Farm name
        if (!document.getElementById('farmName').value.trim()) {
            showFieldError('farmName', 'Farm name is required');
            isValid = false;
        }

        // Farm type
        if (!farmTypeSelect.value) {
            showFieldError('farmTypeId', 'Please select a farm type');
            isValid = false;
        }

        // Owner name
        if (!document.getElementById('ownerName').value.trim()) {
            showFieldError('ownerName', 'Owner name is required');
            isValid = false;
        }

        // Province
        if (!provinceSelect.value) {
            showFieldError('province', 'Please select a province');
            isValid = false;
        }

        // District
        if (!districtSelect.value) {
            showFieldError('district', 'Please select a district');
            isValid = false;
        }

        // Address
        if (!document.getElementById('address').value.trim()) {
            showFieldError('address', 'Address is required');
            isValid = false;
        }

        return isValid;
    }

    /**
     * Build form data for API
     */
    function buildFormData() {
        const contactNumber = ownerContactInput.value.trim();
        
        return {
            farmName: document.getElementById('farmName').value.trim(),
            farmTypeId: farmTypeSelect.value,
            description: descriptionField.value.trim(),
            ownerName: document.getElementById('ownerName').value.trim(),
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
     * Get headers for API requests
     */
    function getHeaders() {
        const headers = {};
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }
        return headers;
    }

    /**
     * Show form message
     */
    function showMessage(message, type) {
        formMessage.textContent = message;
        formMessage.className = `alert alert-${type}`;
        formMessage.style.display = 'block';
        formMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    /**
     * Hide form message
     */
    function hideMessage() {
        formMessage.style.display = 'none';
    }

    /**
     * Show field error
     */
    function showFieldError(fieldId, message) {
        const errorSpan = document.getElementById(`${fieldId}-error`);
        if (errorSpan) {
            errorSpan.textContent = message;
            errorSpan.classList.add('visible');
        }
    }

    /**
     * Clear all field errors
     */
    function clearErrors() {
        document.querySelectorAll('.form-error').forEach(el => {
            el.textContent = '';
            el.classList.remove('visible');
        });
    }

    /**
     * Set loading state
     */
    function setLoading(loading) {
        submitBtn.disabled = loading;
        submitBtn.querySelector('.btn-text').style.display = loading ? 'none' : 'inline';
        submitBtn.querySelector('.btn-loading').style.display = loading ? 'inline-flex' : 'none';
    }

    // Initialize when DOM is ready
    document.addEventListener('DOMContentLoaded', init);
})();
