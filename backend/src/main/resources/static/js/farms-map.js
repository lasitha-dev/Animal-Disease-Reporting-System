/**
 * Farm Map JavaScript
 * Handles Google Maps initialization, farm markers, disease markers, and info windows
 */

// Global variables
let map = null;
let farmMarkers = [];
let infoWindow = null;
let allFarms = [];
let diseaseData = [];
let animalTypesWithReports = [];
let diseasesWithReports = [];
let provincesWithFarms = [];
let selectedAnimalTypeIds = [];
let selectedDiseaseIds = [];
let selectedProvince = '';

/**
 * Initialize the farm map (called by Google Maps API callback)
 */
function initFarmMap() {
    console.log('Initializing farm map...');

    const mapContainer = document.getElementById('farmMap');
    if (!mapContainer) {
        console.error('Map container not found');
        return;
    }

    // Initialize map centered on Sri Lanka
    const config = window.mapConfig || {};
    map = new google.maps.Map(mapContainer, {
        center: {
            lat: config.defaultLat || 7.8731,
            lng: config.defaultLng || 80.7718
        },
        zoom: config.defaultZoom || 7,
        mapTypeControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
            position: google.maps.ControlPosition.TOP_RIGHT
        },
        streetViewControl: false,
        fullscreenControl: true,
        zoomControl: true,
        zoomControlOptions: {
            position: google.maps.ControlPosition.RIGHT_CENTER
        },
        styles: [
            {
                featureType: 'poi',
                elementType: 'labels',
                stylers: [{ visibility: 'off' }]
            }
        ]
    });

    // Create single info window instance (reused for all markers)
    infoWindow = new google.maps.InfoWindow();

    // Load initial data
    loadProvincesWithFarms();
    loadAllFarms();
    loadAnimalTypesWithReports();

    // Setup filter listeners
    setupFilterListeners();
}

/**
 * Load provinces that have registered farms
 */
async function loadProvincesWithFarms() {
    try {
        const response = await fetch('/api/vet/farms/provinces', {
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin'
        });

        if (!response.ok) throw new Error('Failed to fetch provinces');

        provincesWithFarms = await response.json();
        console.log(`Loaded ${provincesWithFarms.length} provinces with farms`);

        populateProvinceDropdown();
    } catch (error) {
        console.error('Error loading provinces:', error);
        provincesWithFarms = [];
    }
}

/**
 * Populate province dropdown
 */
function populateProvinceDropdown() {
    const select = document.getElementById('provinceSelect');
    if (!select) return;

    // Keep the "All Provinces" option
    select.innerHTML = '<option value="">All Provinces</option>';

    provincesWithFarms.forEach(p => {
        const option = document.createElement('option');
        option.value = p.code;
        option.textContent = p.displayName;
        select.appendChild(option);
    });
    // Refresh CustomSelect to reflect new options
    if (window.CustomSelect) {
        CustomSelect.refresh('provinceSelect');
    }
}

/**
 * Load all farms from API
 */
async function loadAllFarms() {
    const loadingOverlay = document.getElementById('mapLoading');
    const emptyState = document.getElementById('mapEmptyState');

    try {
        const [myFarmsResponse, otherFarmsResponse] = await Promise.all([
            fetch('/api/vet/farms', {
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            }),
            fetch('/api/vet/farms/others', {
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin'
            })
        ]);

        if (!myFarmsResponse.ok || !otherFarmsResponse.ok) {
            throw new Error('Failed to fetch farms');
        }

        const myFarms = await myFarmsResponse.json();
        const otherFarms = await otherFarmsResponse.json();

        allFarms = [...myFarms, ...otherFarms];
        console.log(`Loaded ${allFarms.length} farms total`);

        const farmsWithLocation = allFarms.filter(
            farm => farm.gpsLatitude && farm.gpsLongitude
        );

        updateFarmCount(farmsWithLocation.length);

        if (farmsWithLocation.length === 0) {
            loadingOverlay.style.display = 'none';
            emptyState.style.display = 'block';
            return;
        }

        createMapMarkers();
        loadingOverlay.style.display = 'none';
        fitMapToMarkers();

    } catch (error) {
        console.error('Error loading farms:', error);
        loadingOverlay.style.display = 'none';
        emptyState.querySelector('h3').textContent = 'Error Loading Data';
        emptyState.querySelector('p').textContent = 'Failed to load farm data. Please refresh the page.';
        emptyState.style.display = 'block';
    }
}

/**
 * Load disease reports for map
 */
async function loadDiseaseReports() {
    try {
        let url = '/api/vet/disease-reports/map';
        const params = new URLSearchParams();

        if (selectedAnimalTypeIds.length > 0) {
            selectedAnimalTypeIds.forEach(id => params.append('animalTypeIds', id));
        }
        if (selectedDiseaseIds.length > 0) {
            selectedDiseaseIds.forEach(id => params.append('diseaseIds', id));
        }
        if (selectedProvince) {
            params.append('province', selectedProvince);
        }

        if (params.toString()) {
            url += `?${params.toString()}`;
        }

        const response = await fetch(url, {
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin'
        });

        if (!response.ok) throw new Error('Failed to fetch disease reports');

        diseaseData = await response.json();
        console.log(`Loaded ${diseaseData.length} farms with disease reports`);

        const totalDiseases = diseaseData.reduce((sum, farm) => sum + farm.diseases.length, 0);
        updateDiseaseCount(totalDiseases);

        createMapMarkers();

    } catch (error) {
        console.error('Error loading disease reports:', error);
        diseaseData = [];
    }
}

/**
 * Load animal types that have disease reports
 */
async function loadAnimalTypesWithReports() {
    try {
        const response = await fetch('/api/vet/animal-types/with-reports', {
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin'
        });

        if (!response.ok) throw new Error('Failed to fetch animal types');

        animalTypesWithReports = await response.json();
        console.log(`Loaded ${animalTypesWithReports.length} animal types with reports`);

        populateAnimalTypeDropdown();
    } catch (error) {
        console.error('Error loading animal types:', error);
        animalTypesWithReports = [];
    }
}

/**
 * Load diseases that have reports for selected animal types
 */
async function loadDiseasesWithReports() {
    try {
        let url = '/api/vet/diseases/with-reports';

        if (selectedAnimalTypeIds.length > 0) {
            const params = new URLSearchParams();
            selectedAnimalTypeIds.forEach(id => params.append('animalTypeIds', id));
            url += `?${params.toString()}`;
        }

        const response = await fetch(url, {
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin'
        });

        if (!response.ok) throw new Error('Failed to fetch diseases');

        diseasesWithReports = await response.json();
        console.log(`Loaded ${diseasesWithReports.length} diseases with reports`);

        populateDiseaseDropdown();
    } catch (error) {
        console.error('Error loading diseases:', error);
        diseasesWithReports = [];
        populateDiseaseDropdown();
    }
}

/**
 * Populate animal type dropdown
 */
function populateAnimalTypeDropdown() {
    const select = document.getElementById('animalTypeSelect');
    if (!select) return;

    select.innerHTML = '<option value="">All Animal Types</option>';

    animalTypesWithReports.forEach(at => {
        const option = document.createElement('option');
        option.value = at.id;
        option.textContent = at.typeName;
        select.appendChild(option);
    });

    selectedAnimalTypeIds = [];
    // Refresh CustomSelect to reflect new options
    if (window.CustomSelect) {
        CustomSelect.refresh('animalTypeSelect');
    }
}

/**
 * Populate disease dropdown
 */
function populateDiseaseDropdown() {
    const select = document.getElementById('diseaseSelect');
    if (!select) return;

    select.innerHTML = '<option value="">All Diseases</option>';

    diseasesWithReports.forEach(d => {
        const option = document.createElement('option');
        option.value = d.id;
        option.textContent = d.diseaseName + (d.diseaseCode ? ` (${d.diseaseCode})` : '');
        select.appendChild(option);
    });

    selectedDiseaseIds = [];
    // Refresh CustomSelect to reflect new options
    if (window.CustomSelect) {
        CustomSelect.refresh('diseaseSelect');
    }
}



/**
 * Get filtered farms based on province selection
 */
function getFilteredFarms() {
    let farms = allFarms.filter(farm => farm.gpsLatitude && farm.gpsLongitude);

    if (selectedProvince) {
        farms = farms.filter(farm => farm.province === selectedProvince);
    }

    return farms;
}

/**
 * Create markers on the map
 */
function createMapMarkers() {
    clearMarkers();

    const showFarms = document.getElementById('filterFarms').checked;
    const showDiseases = document.getElementById('filterDiseases').checked;

    if (!showFarms && !showDiseases) return;

    const diseaseByFarmId = new Map();
    if (showDiseases && diseaseData.length > 0) {
        diseaseData.forEach(d => diseaseByFarmId.set(d.farmId, d));
    }

    const filteredFarms = getFilteredFarms();

    // Update farm count after filtering
    updateFarmCount(filteredFarms.length);

    filteredFarms.forEach(farm => {
        const farmDiseases = diseaseByFarmId.get(farm.id);
        const hasDiseases = !!farmDiseases && farmDiseases.diseases.length > 0;

        let shouldShow = false;
        if (showFarms && !showDiseases) {
            shouldShow = true;
        } else if (!showFarms && showDiseases) {
            shouldShow = hasDiseases;
        } else if (showFarms && showDiseases) {
            shouldShow = true;
        }

        if (!shouldShow) return;

        const markerColor = hasDiseases ? '#DC2626' : '#059669';
        const borderColor = hasDiseases ? '#B91C1C' : '#047857';

        const marker = new google.maps.Marker({
            position: {
                lat: parseFloat(farm.gpsLatitude),
                lng: parseFloat(farm.gpsLongitude)
            },
            map: map,
            title: farm.farmName,
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                scale: hasDiseases ? 8 : 7,
                fillColor: markerColor,
                fillOpacity: 1,
                strokeColor: borderColor,
                strokeWeight: 1.5
            },
            farmData: farm,
            diseaseData: farmDiseases
        });

        marker.addListener('click', () => {
            if (hasDiseases && showDiseases) {
                showMergedInfoWindow(marker, farm, farmDiseases);
            } else {
                showFarmInfoWindow(marker, farm);
            }
        });

        farmMarkers.push(marker);
    });

    console.log(`Created ${farmMarkers.length} markers`);
}

/**
 * Show info window for a farm only
 */
function showFarmInfoWindow(marker, farm) {
    const content = generateFarmInfoWindowContent(farm);
    infoWindow.setContent(content);
    infoWindow.open(map, marker);
}

/**
 * Show merged info window for farm with diseases
 */
function showMergedInfoWindow(marker, farm, diseaseInfo) {
    const content = generateMergedInfoWindowContent(farm, diseaseInfo);
    infoWindow.setContent(content);
    infoWindow.open(map, marker);
}

/**
 * Generate HTML content for farm-only info window
 */
function generateFarmInfoWindowContent(farm) {
    let animalsHtml = '';
    if (farm.animalTags && farm.animalTags.length > 0) {
        const tagsHtml = farm.animalTags.map(tag =>
            `<span class="animal-tag">${escapeHtml(tag.animalTypeName)} <span class="count">×${tag.count}</span></span>`
        ).join('');
        animalsHtml = `<div class="farm-info-animals"><h5>Animals</h5><div class="animal-tags">${tagsHtml}</div></div>`;
    }

    return `
        <div class="farm-info-window">
            <div class="farm-info-header">
                <span class="farm-info-icon">🏠</span>
                <div class="farm-info-title">
                    <h4>${escapeHtml(farm.farmName)}</h4>
                    ${farm.farmTypeName ? `<span class="farm-info-type">${escapeHtml(farm.farmTypeName)}</span>` : ''}
                </div>
            </div>
            <div class="farm-info-body">
                ${farm.districtDisplayName ? `<div class="farm-info-row"><span class="label">District:</span><span class="value">${escapeHtml(farm.districtDisplayName)}</span></div>` : ''}
                ${farm.provinceDisplayName ? `<div class="farm-info-row"><span class="label">Province:</span><span class="value">${escapeHtml(farm.provinceDisplayName)}</span></div>` : ''}
                ${farm.address ? `<div class="farm-info-row"><span class="label">Address:</span><span class="value">${escapeHtml(farm.address)}</span></div>` : ''}
                ${farm.ownerName ? `<div class="farm-info-row"><span class="label">Owner:</span><span class="value">${escapeHtml(farm.ownerName)}</span></div>` : ''}
                ${animalsHtml}
            </div>
        </div>
    `;
}

/**
 * Generate HTML content for merged farm + disease info window
 */
function generateMergedInfoWindowContent(farm, diseaseInfo) {
    const farmHeader = `
        <div class="farm-info-header disease-header">
            <span class="farm-info-icon">⚠️</span>
            <div class="farm-info-title">
                <h4>${escapeHtml(farm.farmName)}</h4>
                <span class="disease-count-badge">${diseaseInfo.diseases.length} Disease Report${diseaseInfo.diseases.length > 1 ? 's' : ''}</span>
            </div>
        </div>
    `;

    const diseaseListHtml = diseaseInfo.diseases.map(d => {
        // Use effective values (which account for vet overrides) if available, fallback to original
        const displayName = d.effectiveDiseaseName || d.diseaseName;
        const displaySeverity = d.effectiveSeverity || d.severity || 'UNKNOWN';
        const displayNotifiable = d.effectiveNotifiable !== undefined ? d.effectiveNotifiable : d.isNotifiable;
        
        const severityClass = `severity-${displaySeverity.toLowerCase()}`;
        const outcomeText = d.outcome ? d.outcome.replace('_', ' ') : '-';
        const currentUsername = window.currentUsername || '';
        const isOtherVetsReport = d.reportedByUsername && d.reportedByUsername !== currentUsername;
        const reportUrl = isOtherVetsReport
            ? `/vet/disease-reporting?tab=others&viewReport=${d.reportId}`
            : `/vet/disease-reporting?viewReport=${d.reportId}`;

        return `
            <div class="disease-card">
                <div class="disease-card-header">
                    <span class="disease-name">${escapeHtml(displayName)}</span>
                    <span class="severity-badge ${severityClass}">${displaySeverity || '-'}</span>
                </div>
                <div class="disease-card-body">
                    <div class="disease-detail"><span class="detail-label">Animal:</span><span class="detail-value">${escapeHtml(d.animalTypeName)}</span></div>
                    <div class="disease-detail"><span class="detail-label">Affected:</span><span class="detail-value">${d.affectedCount || '-'}</span></div>
                    <div class="disease-detail"><span class="detail-label">Date:</span><span class="detail-value">${d.reportDate || '-'}</span></div>
                    <div class="disease-detail"><span class="detail-label">Outcome:</span><span class="detail-value outcome-${(d.outcome || 'unknown').toLowerCase()}">${outcomeText}</span></div>
                    ${displayNotifiable ? '<span class="notifiable-badge">⚡ Notifiable</span>' : ''}
                </div>
                <div class="disease-card-footer">
                    <a href="${reportUrl}" class="view-details-link" target="_blank">View Full Report →</a>
                </div>
            </div>
        `;
    }).join('');

    const locationHtml = `
        <div class="location-info">
            ${diseaseInfo.districtDisplayName ? `<span>📍 ${escapeHtml(diseaseInfo.districtDisplayName)}</span>` : ''}
            ${diseaseInfo.provinceDisplayName ? `<span>, ${escapeHtml(diseaseInfo.provinceDisplayName)}</span>` : ''}
        </div>
    `;

    return `
        <div class="farm-info-window disease-info-window">
            ${farmHeader}
            ${locationHtml}
            <div class="disease-list">${diseaseListHtml}</div>
        </div>
    `;
}

/**
 * Fit map bounds to show all markers
 */
function fitMapToMarkers() {
    if (farmMarkers.length === 0) return;

    const bounds = new google.maps.LatLngBounds();
    farmMarkers.forEach(marker => bounds.extend(marker.getPosition()));
    map.fitBounds(bounds);

    google.maps.event.addListenerOnce(map, 'bounds_changed', () => {
        if (map.getZoom() > 15) map.setZoom(15);
    });
}

/**
 * Clear all markers from the map
 */
function clearMarkers() {
    farmMarkers.forEach(marker => marker.setMap(null));
    farmMarkers = [];
}

/**
 * Update farm count
 */
function updateFarmCount(count) {
    const el = document.getElementById('farmCount');
    if (el) el.textContent = count;
}

/**
 * Update disease count
 */
function updateDiseaseCount(count) {
    const el = document.getElementById('diseaseCount');
    if (el) el.textContent = count;
}

/**
 * Setup filter listeners
 */
function setupFilterListeners() {
    const farmFilter = document.getElementById('filterFarms');
    const diseaseFilter = document.getElementById('filterDiseases');
    const provinceSection = document.getElementById('provinceFilterSection');
    const provinceSelect = document.getElementById('provinceSelect');
    const animalTypeSection = document.getElementById('animalTypeFilterSection');
    const diseaseSection = document.getElementById('diseaseFilterSection');
    const animalTypeSelect = document.getElementById('animalTypeSelect');
    const diseaseSelect = document.getElementById('diseaseSelect');

    // Farm filter toggle
    if (farmFilter) {
        farmFilter.addEventListener('change', (e) => {
            if (provinceSection) {
                provinceSection.style.display = e.target.checked ? 'flex' : 'none';
            }
            if (!e.target.checked) {
                selectedProvince = '';
                if (provinceSelect) provinceSelect.value = '';
            }
            createMapMarkers();
            fitMapToMarkers();
        });
    }

    // Disease filter toggle
    if (diseaseFilter) {
        diseaseFilter.addEventListener('change', (e) => {
            if (e.target.checked) {
                if (animalTypeSection) animalTypeSection.style.display = 'flex';
                if (diseaseSection) diseaseSection.style.display = 'flex';
                loadDiseasesWithReports();
                loadDiseaseReports();
            } else {
                if (animalTypeSection) animalTypeSection.style.display = 'none';
                if (diseaseSection) diseaseSection.style.display = 'none';
                diseaseData = [];
                selectedAnimalTypeIds = [];
                selectedDiseaseIds = [];
                updateDiseaseCount(0);
                createMapMarkers();
            }
        });
    }

    // Province select
    if (provinceSelect) {
        provinceSelect.addEventListener('change', () => {
            selectedProvince = provinceSelect.value;
            console.log('Selected province:', selectedProvince);

            // Reload disease reports with province filter if diseases are enabled
            if (diseaseFilter && diseaseFilter.checked) {
                loadDiseaseReports();
            } else {
                createMapMarkers();
                fitMapToMarkers();
            }
        });
    }

    // Animal type select
    if (animalTypeSelect) {
        animalTypeSelect.addEventListener('change', () => {
            selectedAnimalTypeIds = animalTypeSelect.value ? [animalTypeSelect.value] : [];
            console.log('Selected animal type:', selectedAnimalTypeIds);
            loadDiseasesWithReports();
            loadDiseaseReports();
        });
    }

    // Disease select
    if (diseaseSelect) {
        diseaseSelect.addEventListener('change', () => {
            selectedDiseaseIds = diseaseSelect.value ? [diseaseSelect.value] : [];
            console.log('Selected disease:', selectedDiseaseIds);
            loadDiseaseReports();
        });
    }
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Make initFarmMap available globally for Google Maps callback
window.initFarmMap = initFarmMap;
