/**
 * Farm Map JavaScript
 * Handles Google Maps initialization, farm markers, and info windows
 */

// Global variables
let map = null;
let markers = [];
let infoWindow = null;
let allFarms = [];

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
    
    // Load farms
    loadAllFarms();
    
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
        createFarmMarkers(farmsWithLocation);
        
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
 * Create markers for all farms
 */
function createFarmMarkers(farms) {
    // Clear existing markers
    clearMarkers();
    
    farms.forEach(farm => {
        const marker = new google.maps.Marker({
            position: { 
                lat: parseFloat(farm.gpsLatitude), 
                lng: parseFloat(farm.gpsLongitude) 
            },
            map: map,
            title: farm.farmName,
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                scale: 10,
                fillColor: '#059669',
                fillOpacity: 1,
                strokeColor: '#047857',
                strokeWeight: 2
            },
            // Store farm data in marker
            farmData: farm
        });
        
        // Add click listener for info window
        marker.addListener('click', () => {
            showFarmInfoWindow(marker, farm);
        });
        
        markers.push(marker);
    });
    
    console.log(`Created ${markers.length} markers`);
}

/**
 * Show info window for a farm
 */
function showFarmInfoWindow(marker, farm) {
    const content = generateInfoWindowContent(farm);
    infoWindow.setContent(content);
    infoWindow.open(map, marker);
}

/**
 * Generate HTML content for info window
 */
function generateInfoWindowContent(farm) {
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
 * Fit map bounds to show all markers
 */
function fitMapToMarkers() {
    if (markers.length === 0) return;
    
    const bounds = new google.maps.LatLngBounds();
    markers.forEach(marker => {
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
    markers.forEach(marker => {
        marker.setMap(null);
    });
    markers = [];
}

/**
 * Show or hide all farm markers
 */
function toggleFarmMarkers(visible) {
    markers.forEach(marker => {
        marker.setVisible(visible);
    });
    
    // Close info window if hiding markers
    if (!visible && infoWindow) {
        infoWindow.close();
    }
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
 * Setup filter checkbox listeners
 */
function setupFilterListeners() {
    const farmFilter = document.getElementById('filterFarms');
    if (farmFilter) {
        farmFilter.addEventListener('change', (e) => {
            toggleFarmMarkers(e.target.checked);
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
