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
let selectedAnimalTypeIds = new Set();

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

    // Load farms and animal types
    loadAllFarms();
    loadAnimalTypesWithReports();

    // Setup filter listeners
    setupFilterListeners();
}

/**
 * Load all farms from API
 */
async function loadAllFarms() {
    const loadingOverlay = document.getElementById('mapLoading');
    const emptyState = document.getElementById('mapEmptyState');

    try {
        // Fetch current user's farms and other vets' farms
        const [myFarmsResponse, otherFarmsResponse] = await Promise.all([
            fetch('/api/vet/farms', {
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'same-origin'
            }),
            fetch('/api/vet/farms/others', {
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'same-origin'
            })
        ]);

        if (!myFarmsResponse.ok || !otherFarmsResponse.ok) {
            throw new Error('Failed to fetch farms');
        }

        const myFarms = await myFarmsResponse.json();
        const otherFarms = await otherFarmsResponse.json();

        // Combine all farms
        allFarms = [...myFarms, ...otherFarms];

        console.log(`Loaded ${allFarms.length} farms total`);

        // Filter farms with valid GPS coordinates
        const farmsWithLocation = allFarms.filter(
            farm => farm.gpsLatitude && farm.gpsLongitude
        );

        // Update farm count in filter
        updateFarmCount(farmsWithLocation.length);

        if (farmsWithLocation.length === 0) {
            // Show empty state
            loadingOverlay.style.display = 'none';
            emptyState.style.display = 'block';
            return;
        }

        // Create markers for farms
        createMapMarkers();

        // Hide loading overlay
        loadingOverlay.style.display = 'none';

        // Fit map to show all markers
        fitMapToMarkers();

    } catch (error) {
        console.error('Error loading farms:', error);
        loadingOverlay.style.display = 'none';

        // Show error in empty state
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

        // Add animal type filter if any are selected
        if (selectedAnimalTypeIds.size > 0) {
            const idsParam = Array.from(selectedAnimalTypeIds).join(',');
            url += `?animalTypeIds=${idsParam}`;
        }

        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'same-origin'
        });

        if (!response.ok) {
            throw new Error('Failed to fetch disease reports');
        }

        diseaseData = await response.json();
        console.log(`Loaded ${diseaseData.length} farms with disease reports`);

        // Update disease count
        const totalDiseases = diseaseData.reduce((sum, farm) => sum + farm.diseases.length, 0);
        updateDiseaseCount(totalDiseases);

        // Refresh markers
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
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'same-origin'
        });

        if (!response.ok) {
            throw new Error('Failed to fetch animal types');
        }

        animalTypesWithReports = await response.json();
        console.log(`Loaded ${animalTypesWithReports.length} animal types with reports`);

        // Populate the animal type filter checkboxes
        populateAnimalTypeFilters();

    } catch (error) {
        console.error('Error loading animal types:', error);
        animalTypesWithReports = [];
    }
}

/**
 * Populate animal type filter checkboxes
 */
function populateAnimalTypeFilters() {
    const container = document.getElementById('animalTypeFilters');

    if (animalTypesWithReports.length === 0) {
        container.innerHTML = '<div class="no-types-text">No animal types with disease reports</div>';
        return;
    }

    // Select all by default
    selectedAnimalTypeIds = new Set(animalTypesWithReports.map(at => at.id));

    container.innerHTML = animalTypesWithReports.map(at => `
        <label class="animal-type-checkbox">
            <input type="checkbox" value="${at.id}" checked>
            <span class="animal-type-name">${escapeHtml(at.typeName)}</span>
        </label>
    `).join('');

    // Add event listeners to checkboxes
    container.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        checkbox.addEventListener('change', (e) => {
            const id = e.target.value;
            if (e.target.checked) {
                selectedAnimalTypeIds.add(id);
            } else {
                selectedAnimalTypeIds.delete(id);
            }
            // Reload disease data with new filters
            loadDiseaseReports();
        });
    });
}

/**
 * Create markers on the map (merged farm + disease)
 */
function createMapMarkers() {
    // Clear existing markers
    clearMarkers();

    const showFarms = document.getElementById('filterFarms').checked;
    const showDiseases = document.getElementById('filterDiseases').checked;

    if (!showFarms && !showDiseases) {
        return;
    }

    // Build a map of farmId -> disease data for quick lookup
    const diseaseByFarmId = new Map();
    if (showDiseases && diseaseData.length > 0) {
        diseaseData.forEach(d => {
            diseaseByFarmId.set(d.farmId, d);
        });
    }

    // Filter farms with valid GPS coordinates
    const farmsWithLocation = allFarms.filter(
        farm => farm.gpsLatitude && farm.gpsLongitude
    );

    farmsWithLocation.forEach(farm => {
        const farmDiseases = diseaseByFarmId.get(farm.id);
        const hasDiseases = !!farmDiseases && farmDiseases.diseases.length > 0;

        // Determine if we should show this marker
        let shouldShow = false;
        if (showFarms && !showDiseases) {
            // Show all farms
            shouldShow = true;
        } else if (!showFarms && showDiseases) {
            // Only show farms with diseases
            shouldShow = hasDiseases;
        } else if (showFarms && showDiseases) {
            // Show all farms (disease ones will have different color)
            shouldShow = true;
        }

        if (!shouldShow) return;

        // Determine marker color
        const markerColor = hasDiseases ? '#DC2626' : '#059669'; // Red for disease, green for farm
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
                scale: hasDiseases ? 12 : 10,
                fillColor: markerColor,
                fillOpacity: 1,
                strokeColor: borderColor,
                strokeWeight: 2
            },
            // Store data in marker
            farmData: farm,
            diseaseData: farmDiseases
        });

        // Add click listener for info window
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
    // Build animal tags HTML
    let animalsHtml = '';
    if (farm.animalTags && farm.animalTags.length > 0) {
        const tagsHtml = farm.animalTags.map(tag =>
            `<span class="animal-tag">
                ${escapeHtml(tag.animalTypeName)} 
                <span class="count">×${tag.count}</span>
            </span>`
        ).join('');

        animalsHtml = `
            <div class="farm-info-animals">
                <h5>Animals</h5>
                <div class="animal-tags">${tagsHtml}</div>
            </div>
        `;
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
                ${farm.districtDisplayName ? `
                    <div class="farm-info-row">
                        <span class="label">District:</span>
                        <span class="value">${escapeHtml(farm.districtDisplayName)}</span>
                    </div>
                ` : ''}
                ${farm.provinceDisplayName ? `
                    <div class="farm-info-row">
                        <span class="label">Province:</span>
                        <span class="value">${escapeHtml(farm.provinceDisplayName)}</span>
                    </div>
                ` : ''}
                ${farm.address ? `
                    <div class="farm-info-row">
                        <span class="label">Address:</span>
                        <span class="value">${escapeHtml(farm.address)}</span>
                    </div>
                ` : ''}
                ${farm.ownerName ? `
                    <div class="farm-info-row">
                        <span class="label">Owner:</span>
                        <span class="value">${escapeHtml(farm.ownerName)}</span>
                    </div>
                ` : ''}
                ${animalsHtml}
            </div>
        </div>
    `;
}

/**
 * Generate HTML content for merged farm + disease info window
 */
function generateMergedInfoWindowContent(farm, diseaseInfo) {
    // Farm header
    const farmHeader = `
        <div class="farm-info-header disease-header">
            <span class="farm-info-icon">⚠️</span>
            <div class="farm-info-title">
                <h4>${escapeHtml(farm.farmName)}</h4>
                <span class="disease-count-badge">${diseaseInfo.diseases.length} Disease Report${diseaseInfo.diseases.length > 1 ? 's' : ''}</span>
            </div>
        </div>
    `;

    // Disease list
    const diseaseListHtml = diseaseInfo.diseases.map(d => {
        const severityClass = `severity-${(d.severity || 'UNKNOWN').toLowerCase()}`;
        const outcomeText = d.outcome ? d.outcome.replace('_', ' ') : '-';

        // Determine if this report is by the current user
        const currentUsername = window.currentUsername || '';
        const isOtherVetsReport = d.reportedByUsername && d.reportedByUsername !== currentUsername;
        const reportUrl = isOtherVetsReport
            ? `/vet/disease-reporting?tab=others&viewReport=${d.reportId}`
            : `/vet/disease-reporting?viewReport=${d.reportId}`;

        return `
            <div class="disease-card">
                <div class="disease-card-header">
                    <span class="disease-name">${escapeHtml(d.diseaseName)}</span>
                    <span class="severity-badge ${severityClass}">${d.severity || '-'}</span>
                </div>
                <div class="disease-card-body">
                    <div class="disease-detail">
                        <span class="detail-label">Animal:</span>
                        <span class="detail-value">${escapeHtml(d.animalTypeName)}</span>
                    </div>
                    <div class="disease-detail">
                        <span class="detail-label">Affected:</span>
                        <span class="detail-value">${d.affectedCount || '-'}</span>
                    </div>
                    <div class="disease-detail">
                        <span class="detail-label">Date:</span>
                        <span class="detail-value">${d.reportDate || '-'}</span>
                    </div>
                    <div class="disease-detail">
                        <span class="detail-label">Outcome:</span>
                        <span class="detail-value outcome-${(d.outcome || 'unknown').toLowerCase()}">${outcomeText}</span>
                    </div>
                    ${d.isNotifiable ? '<span class="notifiable-badge">⚡ Notifiable</span>' : ''}
                </div>
                <div class="disease-card-footer">
                    <a href="${reportUrl}" class="view-details-link" target="_blank">
                        View Full Report →
                    </a>
                </div>
            </div>
        `;
    }).join('');


    // Location info
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
            <div class="disease-list">
                ${diseaseListHtml}
            </div>
        </div>
    `;
}

/**
 * Fit map bounds to show all markers
 */
function fitMapToMarkers() {
    if (farmMarkers.length === 0) return;

    const bounds = new google.maps.LatLngBounds();
    farmMarkers.forEach(marker => {
        bounds.extend(marker.getPosition());
    });

    map.fitBounds(bounds);

    // Don't zoom in too much for single marker
    google.maps.event.addListenerOnce(map, 'bounds_changed', () => {
        if (map.getZoom() > 15) {
            map.setZoom(15);
        }
    });
}

/**
 * Clear all markers from the map
 */
function clearMarkers() {
    farmMarkers.forEach(marker => {
        marker.setMap(null);
    });
    farmMarkers = [];
}

/**
 * Update farm count in filter button
 */
function updateFarmCount(count) {
    const countElement = document.getElementById('farmCount');
    if (countElement) {
        countElement.textContent = count;
    }
}

/**
 * Update disease count in filter button
 */
function updateDiseaseCount(count) {
    const countElement = document.getElementById('diseaseCount');
    if (countElement) {
        countElement.textContent = count;
    }
}

/**
 * Setup filter checkbox listeners
 */
function setupFilterListeners() {
    const farmFilter = document.getElementById('filterFarms');
    const diseaseFilter = document.getElementById('filterDiseases');
    const animalTypeContainer = document.getElementById('animalTypeFilterContainer');

    if (farmFilter) {
        farmFilter.addEventListener('change', () => {
            createMapMarkers();
            fitMapToMarkers();
        });
    }

    if (diseaseFilter) {
        diseaseFilter.addEventListener('change', (e) => {
            if (e.target.checked) {
                // Show animal type filter and load disease data
                animalTypeContainer.style.display = 'block';
                loadDiseaseReports();
            } else {
                // Hide animal type filter and clear disease data
                animalTypeContainer.style.display = 'none';
                diseaseData = [];
                updateDiseaseCount(0);
                createMapMarkers();
            }
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
