/**
 * Sri Lanka District Map Module
 * Renders an interactive SVG map of 25 Sri Lankan districts
 * showing user distribution by role using Raphael.js
 * 
 * @requires Raphael.js (loaded from CDN in dashboard.html)
 */
const SriLankaMap = (function() {
    'use strict';

    // Map configuration
    const MAP_CONFIG = {
        width: '449.68774',
        height: '792.54926',
        containerId: 'sri-lanka-map'
    };

    // Color scale for user distribution
    const COLOR_SCALE = {
        noData: '#E5E7EB',      // Gray for districts with no users
        low: '#E0F2FE',          // Light blue for 1-5 users
        medium: '#38BDF8',       // Medium blue for 6-15 users
        high: '#0369A1',         // Dark blue for 16+ users
        stroke: '#F7F7F7',       // White stroke between districts
        hover: '#FBBF24'         // Amber for hover state
    };

    // District data structure (SVG paths defined in initializeDistricts)
    let districts = [];
    let rsr = null; // Raphael surface object
    let currentData = [];
    let currentRole = null;

    /**
     * Map district enum values from backend to display names
     */
    const DISTRICT_NAMES = {
        'COLOMBO': 'Colombo',
        'GAMPAHA': 'Gampaha',
        'KALUTARA': 'Kaḷutara',
        'MAHANUVARA': 'Mahanuvara',
        'MATALE': 'Matale',
        'NUWARA_ELIYA': 'Nuwara Eliya',
        'GALLE': 'Galle',
        'MATARA': 'Matara',
        'HAMBANTOTA': 'Hambantota',
        'JAFFNA': 'Jaffna',
        'KILINOCHCHI': 'Kilinochchi',
        'MANNAR': 'Mannar',
        'VAVUNIYA': 'Vavuniya',
        'MULLAITIVU': 'Mullaitivu',
        'BATTICALOA': 'Batticaloa',
        'AMPARA': 'Ampara',
        'TRINCOMALEE': 'Trincomalee',
        'KURUNEGALA': 'Kurunegala',
        'PUTTALAM': 'Puttalam',
        'ANURADHAPURA': 'Anuradhapura',
        'POLONNARUWA': 'Polonnaruwa',
        'BADULLA': 'Badulla',
        'MONERAGALA': 'Moneragala',
        'RATNAPURA': 'Ratnapura',
        'KEGALLE': 'Kegalle'
    };

    /**
     * Initialize the map with Raphael.js and create all district paths
     */
    function initializeDistricts() {
        const container = document.getElementById(MAP_CONFIG.containerId);
        if (!container) {
            console.error('Map container not found');
            return false;
        }

        // Clear any existing content
        container.innerHTML = '';

        // Create Raphael surface
        rsr = Raphael(MAP_CONFIG.containerId, MAP_CONFIG.width, MAP_CONFIG.height);
        
        if (!rsr) {
            console.error('Failed to create Raphael surface');
            return false;
        }

        districts = [];

        // District SVG paths (accurate coordinates from srilankan-map-svg-master)
        const districtPaths = {
            'COLOMBO': "m 44.487125,578.90075 2.6,0.07 -0.85,3.88 1.17,0.77 3.07,-0.34 -0.63,0.87 0.73,0.82 2.1,-0.67 1.16,0.91 1.25,-1.28 0.88,0.78 1.61,-0.27 1.98,2.33 2.36,-0.89 6.19,1.5 1.55,-1.65 1.46,0.39 2.63,-0.87 -0.11,1.34 1.33,2.96 1.97,0.25 6.04,3.27 0.97,-1.17 1.99,1.06 0.69,-0.62 3.04,-0.14 0.12,-5.37 0.96,-2.75 1.43,-0.5 0.75,-1.82 2.38,-0.53 0.45,-1.67 8.550005,0.81 0.8,-1.88 1.86,0.31 0,0 1.03,0.71 1.08,-0.74 2.05,0.46 1.52,-0.61 2.17,1.17 -0.08,3.22 1.05,1.45 0.04,1.6 -0.74,1.2 -2.38,-0.32 0.42,4.01 0,0 -2.13,1.87 0.27,0.95 -0.97,-0.12 -1.07,0.97 -0.02,1.35 -1.42,1.26 -0.44,3.8 -0.89,1.71 2.61,0.62 0.96,1.06 1.51,3.83 0.07,2.91 -0.84,0.76 -3.79,0.02 0,0 -1.03,-1.15 -2.07,0.88 -1.09,-1.63 -1.87,-0.39 -6.640005,3.79 1.6,4.81 -0.11,1.72 -0.75,0.63 -2.32,-0.39 -2.14,-4.49 -0.06,-1.79 -1.01,-0.55 -0.33,-1.62 -1.82,-0.55 -1.12,0.43 -1.7,3.89 -1.18,1.12 -2.76,-1.03 -1.23,-1.74 -1.19,-0.4 -1.95,2.98 -2.54,0.8 0.42,0.67 -1.42,1.29 -0.84,-0.07 -0.22,1.4 -1.33,0.1 -3.79,2.47 -1.92,-0.54 -2,0.53 -1.61,-0.9 -0.6,2.46 -0.71,0.42 -4.99,-0.1 -0.59,-0.67 0.37,-1.11 -1.15,-1.34 -4.95,-2.33 -0.5,0.79 1.44,1.81 2.07,7.84 -0.24,1.76 0,0 -4.53,-11.03 -3.36,-10.53 -1.16,-10.42 -3.26,-11.53 0.67,-4.48 0.67,2.91 1.51,-3.25 -0.39,-1.23 2.18,-0.78 1.05,-2.5 z",
            
            'GAMPAHA': "m 58.947125,518.95075 0.76,0.35 0.27,-1.25 1.58,-0.24 -0.92,-1.03 0.17,-0.79 1.03,0 0.09,-0.75 1.9,0.97 2.04,-0.69 0.74,-1.57 2.68,-0.66 -1.2,-0.58 3.1,-1.57 0.63,-1.23 1.64,1.19 2.45,-0.58 -0.67,1.05 1.06,0.65 0.28,1.23 1.44,-0.33 1.76,0.72 1.59,3.06 1.27,0.12 4.17,-2.49 0.57,-1.54 3.69,-1.44 4.77,-3.93 4.819995,3.86 -0.999995,1.49 0.479995,0.97 4.54001,3.3 3.03,4.68 1.06,-0.46 1.03,0.97 0,0 0.99,2.55 -3.02,2.18 0.54,2.71 -0.45,5.48 -4.37,0.22 -1.09,1.92 -2.34,-0.95 1.73999,2.55 -0.07,5.43 1.7,4.91 2.41,-2.04 5.68999,-0.15 1.25,0.98 -0.12999,1.68 -1.73,1.25 -1.08,2.92 -1.72,-0.08 -2.35,6.07 -1.9,2.45 2,9.85 1.68,3.05 -0.6,3.41 0,0 -1.86,-0.31 -0.80001,1.88 -8.549995,-0.81 -0.45,1.67 -2.38,0.53 -0.75,1.82 -1.43,0.5 -0.96,2.75 -0.12,5.37 -3.04,0.14 -0.69,0.62 -1.99,-1.06 -0.97,1.17 -6.04,-3.27 -1.97,-0.25 -1.33,-2.96 0.11,-1.34 -2.63,0.87 -1.46,-0.39 -1.55,1.65 -6.19,-1.5 -2.36,0.89 -1.98,-2.33 -1.61,0.27 -0.88,-0.78 -1.25,1.28 -1.16,-0.91 -2.1,0.67 -0.73,-0.82 0.63,-0.87 -3.07,0.34 -1.17,-0.77 0.85,-3.88 -2.6,-0.07 0,0 -0.28,-4.87 -10.3,-40.47 0.39,-1.06 1.34,-0.22 1.01,1.4 -1.51,3.92 1.9,6.17 1.23,1.18 0.06,3.03 1.74,2.13 -0.45,1.68 1.29,0.95 1.4,-0.95 0.73,-2.8 0,1.74 1.01,0.95 0.9,-1.74 -1.12,-9.47 -3.64,-6.33 -2.07,0.95 -0.21,1.15 -0.28,-0.5 -0.62,0.5 0.06,-2.41 -0.84,-1.34 2.18,-3.48 0.62,-9.36 1.51,-0.17 -1.18,-0.38 0,0 1.33,0.1 0.84,-0.79 1.46,1.4 2.88,-2.66 2.55,1.86 1.67,-0.21 0.4,-1.14 1.09,1.05 0.95,-0.95 -0.51,2.67 1.56,-0.46 -0.05,-1.46 1.99,-0.3 0.31,-0.95 3.14,1.67 z",
            
            'KALUTARA': "m 51.097125,631.73075 0.24,-1.76 -2.07,-7.84 -1.44,-1.81 0.5,-0.79 4.95,2.33 1.15,1.34 -0.37,1.11 0.59,0.67 4.99,0.1 0.71,-0.42 0.6,-2.46 1.61,0.9 2,-0.53 1.92,0.54 3.79,-2.47 1.33,-0.1 0.22,-1.4 0.84,0.07 1.42,-1.29 -0.42,-0.67 2.54,-0.8 1.95,-2.98 1.19,0.4 1.23,1.74 2.76,1.03 1.18,-1.12 1.7,-3.89 1.12,-0.43 1.82,0.55 0.33,1.62 1.01,0.55 0.06,1.79 2.14,4.49 2.32,0.39 0.75,-0.63 0.11,-1.72 -1.6,-4.81 6.640005,-3.79 1.87,0.39 1.09,1.63 2.07,-0.88 1.03,1.15 0,0 -0.35,1.11 0.89,2.49 1.91,2.58 0.05,4.52 2.73,5.56 -0.08,0.63 -1.27001,-0.26 -0.01,0.78 2.15,3.33 1.15,3.84 1.28,0.49 1.06,2.1 2.97,1.74 1.52,2.44 0.44,2.18 -0.5,1.7 1.22,2.44 1.03,1 4.42,0.86 4.21999,4.38 2.1,5.16 -0.86,-0.2 -2.97,1.83 2.21,3.46 -0.11,0.84 14.04,19.33 1.51,3.83 0,0 -1.09,-0.56 -1.69,0.3 -2.18,1.67 -1.09,-0.17 -3.73,1.81 -4.24,2.89 -2.36,-0.11 -2.01,0.99 0.59,5.98 0,0 0.5,1.73 0,0 1.22,2.7 -0.74,3.11 -0.9,-0.29 -3.65,-4.55 -1.57,-0.34 -4.65,-4.6 -1.6,-0.74 -4.23,-0.55 -0.7,1.73 -1.04,-0.18 0.05,2.63 -3.34,1 -7.31,-2.51 -4.719995,0.18 -1.56,-0.95 -2.36,-3.26 -4.81,-2.92 -3.41,0.87 -1.82,-2.23 -4.61,-2.32 -2.1,-0.07 -1,-2.33 -1.59,-0.85 -1.7,-0.04 -0.68,1.45 -1.13,0.5 -1.94,-2.71 0,0 -1.96,-1.91 -1.01,-2.24 -0.17,-2.13 1.85,-1.57 0.06,-1.85 -1.4,-3.47 1.01,-0.78 -0.33,-1.79 -4.03,-10.13 -2.86,-9.63 -5.29,-12.73 -3.02,-5.43 -0.28,-1.96 z"
        };

        // Create district paths with Raphael
        Object.keys(districtPaths).forEach((districtKey, index) => {
            const district = rsr.path(districtPaths[districtKey]);
            const displayName = DISTRICT_NAMES[districtKey];
            
            district.attr({
                title: displayName,
                id: `d${index + 1}`,
                fill: COLOR_SCALE.noData,
                stroke: COLOR_SCALE.stroke,
                'stroke-width': '1.01433px',
                'stroke-opacity': '1',
                cursor: 'pointer'
            }).data('district', districtKey);

            // Mouse hover events
            district.mouseover(function() {
                if (this.attr('fill') !== COLOR_SCALE.noData) {
                    this.attr({ fill: COLOR_SCALE.hover });
                }
                showTooltip(this, districtKey);
            });

            district.mouseout(function() {
                hideTooltip();
                const userCount = getUserCountForDistrict(districtKey);
                const color = getColorForCount(userCount);
                this.attr({ fill: color });
            });

            // Click event to show detailed modal
            district.click(function() {
                const districtData = {
                    district: displayName,
                    districtKey: districtKey,
                    userCount: getUserCountForDistrict(districtKey)
                };
                
                // Dispatch custom event for modal handling
                const event = new CustomEvent('districtClicked', { 
                    detail: districtData 
                });
                document.dispatchEvent(event);
            });

            districts.push(district);
        });

        console.log(`Initialized ${districts.length} districts`);
        return true;
    }

    /**
     * Get user count for a specific district from current data
     */
    function getUserCountForDistrict(districtKey) {
        if (!currentData || currentData.length === 0) return 0;
        
        const districtData = currentData.find(d => d.district === districtKey);
        return districtData ? districtData.userCount : 0;
    }

    /**
     * Calculate color based on user count
     */
    function getColorForCount(count) {
        if (count === 0) return COLOR_SCALE.noData;
        if (count <= 5) return COLOR_SCALE.low;
        if (count <= 15) return COLOR_SCALE.medium;
        return COLOR_SCALE.high;
    }

    /**
     * Show tooltip on hover
     */
    function showTooltip(districtElement, districtKey) {
        const displayName = districtElement.attr('title');
        const userCount = getUserCountForDistrict(districtKey);
        const roleText = currentRole === 'ALL' ? 'users' : currentRole + 's';
        
        const tooltip = document.getElementById('map-tooltip');
        if (tooltip) {
            tooltip.textContent = `${displayName}: ${userCount} ${roleText}`;
            tooltip.style.display = 'block';
            
            // Position tooltip near cursor (basic positioning)
            tooltip.style.left = '50%';
            tooltip.style.top = '10px';
        }
    }

    /**
     * Hide tooltip
     */
    function hideTooltip() {
        const tooltip = document.getElementById('map-tooltip');
        if (tooltip) {
            tooltip.style.display = 'none';
        }
    }

    /**
     * Public API: Initialize the map
     */
    function init() {
        // Check if Raphael is loaded
        if (typeof Raphael === 'undefined') {
            console.error('Raphael.js is not loaded. Please include Raphael.js CDN in your HTML.');
            return false;
        }

        return initializeDistricts();
    }

    /**
     * Public API: Render map with new data
     * @param {Array} data - Array of district data objects with district and userCount properties
     * @param {String} role - Current role filter (ADMIN, VETERINARY_OFFICER, or ALL)
     */
    function render(data, role) {
        currentData = data || [];
        currentRole = role || 'ALL';

        console.log(`Rendering map with ${currentData.length} districts for role: ${currentRole}`);

        // Update each district's color based on user count
        districts.forEach(district => {
            const districtKey = district.data('district');
            const userCount = getUserCountForDistrict(districtKey);
            const color = getColorForCount(userCount);
            
            district.attr({ fill: color });
        });
    }

    /**
     * Public API: Clear the map
     */
    function clear() {
        districts.forEach(district => {
            district.attr({ fill: COLOR_SCALE.noData });
        });
        currentData = [];
        currentRole = null;
    }

    // Public API
    return {
        init: init,
        render: render,
        clear: clear
    };
})();
