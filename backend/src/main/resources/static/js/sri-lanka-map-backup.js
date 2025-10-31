/**
 * Sri Lanka Province Map
 * Interactive SVG map showing user distribution across provinces
 */

const SriLankaMap = (function() {
    'use strict';

    // SVG dimensions
    const MAP_WIDTH = 800;
    const MAP_HEIGHT = 1000;
    
    // Color scale configuration
    const COLOR_SCALE = {
        noData: '#F3F4F6',
        low: '#E0F2FE',
        mid: '#0EA5E9',
        high: '#0369A1',
        hover: '#FBBF24'
    };

    // Province SVG path data (simplified coordinates)
    // These paths represent the approximate boundaries of Sri Lankan provinces
    const PROVINCE_PATHS = {
        'NORTHERN': 'M400,50 L500,80 L550,150 L520,200 L480,180 L420,150 L380,100 Z',
        'NORTH_WESTERN': 'M320,200 L420,150 L480,180 L480,250 L420,280 L360,260 Z',
        'WESTERN': 'M360,260 L420,280 L450,350 L420,380 L360,360 Z',
        'NORTH_CENTRAL': 'M480,180 L520,200 L580,250 L560,320 L520,300 L480,250 Z',
        'CENTRAL': 'M420,280 L480,250 L520,300 L500,370 L450,350 Z',
        'SABARAGAMUWA': 'M360,360 L420,380 L450,450 L400,480 L350,450 Z',
        'EASTERN': 'M580,250 L650,280 L670,400 L650,500 L600,520 L560,480 L560,320 Z',
        'UVA': 'M500,370 L560,380 L560,480 L520,500 L480,480 L450,450 Z',
        'SOUTHERN': 'M350,450 L400,480 L480,480 L520,500 L500,580 L420,600 L350,560 Z'
    };

    // Province center points for labels
    const PROVINCE_CENTERS = {
        'NORTHERN': [460, 130],
        'NORTH_WESTERN': [400, 230],
        'WESTERN': [400, 330],
        'NORTH_CENTRAL': [530, 260],
        'CENTRAL': [470, 310],
        'SABARAGAMUWA': [400, 430],
        'EASTERN': [610, 380],
        'UVA': [510, 430],
        'SOUTHERN': [430, 530]
    };

    let currentData = [];
    let currentRole = null;
    let svgElement = null;
    let tooltip = null;

    /**
     * Initialize the map in the specified container
     * @param {string} containerId - The ID of the container element
     */
    function init(containerId) {
        const container = document.getElementById(containerId);
        if (!container) {
            console.error('Container not found:', containerId);
            return;
        }

        // Create SVG element
        svgElement = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svgElement.setAttribute('viewBox', `0 0 ${MAP_WIDTH} ${MAP_HEIGHT}`);
        svgElement.setAttribute('class', 'sri-lanka-map-svg');
        
        // Create tooltip
        tooltip = createTooltip();
        container.appendChild(tooltip);
        
        container.appendChild(svgElement);
    }

    /**
     * Create tooltip element
     */
    function createTooltip() {
        const div = document.createElement('div');
        div.className = 'map-tooltip';
        div.style.display = 'none';
        return div;
    }

    /**
     * Render the map with data
     * @param {Array} data - Province distribution data
     * @param {string} role - Optional role filter
     */
    function render(data, role = null) {
        if (!svgElement) {
            console.error('Map not initialized. Call init() first.');
            return;
        }

        currentData = data || [];
        currentRole = role;

        // Clear existing content
        svgElement.innerHTML = '';

        // Calculate color scale
        const { minCount, maxCount } = getDataRange(data);

        // Create a group for all provinces
        const mapGroup = document.createElementNS('http://www.w3.org/2000/svg', 'g');
        
        // Render each province
        Object.keys(PROVINCE_PATHS).forEach(provinceKey => {
            const provinceData = data.find(d => d.province === provinceKey);
            const count = provinceData ? provinceData.userCount : 0;
            
            // Create province path
            const path = createProvincePath(provinceKey, count, minCount, maxCount, provinceData);
            mapGroup.appendChild(path);

            // Add label
            const label = createProvinceLabel(provinceKey, count);
            mapGroup.appendChild(label);
        });

        svgElement.appendChild(mapGroup);
    }

    /**
     * Create a province path element
     */
    function createProvincePath(provinceKey, count, minCount, maxCount, provinceData) {
        const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        path.setAttribute('d', PROVINCE_PATHS[provinceKey]);
        path.setAttribute('class', 'province-path');
        path.setAttribute('data-province', provinceKey);
        
        // Calculate color
        const color = getProvinceColor(count, minCount, maxCount);
        path.setAttribute('fill', color);
        path.setAttribute('stroke', '#CBD5E1');
        path.setAttribute('stroke-width', '2');
        
        // Add interactivity
        path.style.cursor = count > 0 ? 'pointer' : 'default';
        
        path.addEventListener('mouseenter', (e) => handleProvinceHover(e, provinceData));
        path.addEventListener('mousemove', (e) => updateTooltipPosition(e));
        path.addEventListener('mouseleave', hideTooltip);
        
        if (count > 0) {
            path.addEventListener('click', () => handleProvinceClick(provinceData));
        }

        return path;
    }

    /**
     * Create province label
     */
    function createProvinceLabel(provinceKey, count) {
        const center = PROVINCE_CENTERS[provinceKey];
        const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        
        text.setAttribute('x', center[0]);
        text.setAttribute('y', center[1]);
        text.setAttribute('class', 'province-label');
        text.setAttribute('text-anchor', 'middle');
        text.setAttribute('dominant-baseline', 'middle');
        text.setAttribute('pointer-events', 'none');
        text.style.fontSize = '14px';
        text.style.fontWeight = 'bold';
        text.style.fill = count > 0 ? '#1E293B' : '#94A3B8';
        
        text.textContent = count || '0';
        
        return text;
    }

    /**
     * Get data range for color scaling
     */
    function getDataRange(data) {
        if (!data || data.length === 0) {
            return { minCount: 0, maxCount: 0 };
        }

        const counts = data.map(d => d.userCount).filter(c => c > 0);
        return {
            minCount: counts.length > 0 ? Math.min(...counts) : 0,
            maxCount: counts.length > 0 ? Math.max(...counts) : 0
        };
    }

    /**
     * Calculate province color based on user count
     */
    function getProvinceColor(count, minCount, maxCount) {
        if (count === 0) {
            return COLOR_SCALE.noData;
        }

        if (maxCount === minCount) {
            return COLOR_SCALE.mid;
        }

        // Normalize count to 0-1 range
        const normalized = (count - minCount) / (maxCount - minCount);

        // Interpolate between low and high colors
        if (normalized < 0.5) {
            return interpolateColor(COLOR_SCALE.low, COLOR_SCALE.mid, normalized * 2);
        } else {
            return interpolateColor(COLOR_SCALE.mid, COLOR_SCALE.high, (normalized - 0.5) * 2);
        }
    }

    /**
     * Interpolate between two hex colors
     */
    function interpolateColor(color1, color2, factor) {
        const c1 = hexToRgb(color1);
        const c2 = hexToRgb(color2);
        
        const r = Math.round(c1.r + (c2.r - c1.r) * factor);
        const g = Math.round(c1.g + (c2.g - c1.g) * factor);
        const b = Math.round(c1.b + (c2.b - c1.b) * factor);
        
        return rgbToHex(r, g, b);
    }

    /**
     * Convert hex color to RGB
     */
    function hexToRgb(hex) {
        const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
        return result ? {
            r: parseInt(result[1], 16),
            g: parseInt(result[2], 16),
            b: parseInt(result[3], 16)
        } : null;
    }

    /**
     * Convert RGB to hex color
     */
    function rgbToHex(r, g, b) {
        return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
    }

    /**
     * Handle province hover
     */
    function handleProvinceHover(event, provinceData) {
        // Highlight province
        event.target.setAttribute('fill', COLOR_SCALE.hover);
        
        // Show tooltip
        if (provinceData) {
            showTooltip(provinceData, event);
        }
    }

    /**
     * Show tooltip
     */
    function showTooltip(provinceData, event) {
        if (!tooltip) return;

        let content = `<strong>${provinceData.displayName}</strong><br>`;
        content += `<span class="tooltip-count">👥 Total Users: ${provinceData.userCount}</span><br>`;
        
        if (provinceData.districtBreakdown && Object.keys(provinceData.districtBreakdown).length > 0) {
            content += '<div class="tooltip-districts"><strong>Districts:</strong><br>';
            for (const [district, count] of Object.entries(provinceData.districtBreakdown)) {
                content += `• ${district}: ${count}<br>`;
            }
            content += '</div>';
        }
        
        content += '<small style="color: #94A3B8;">Click for details</small>';
        
        tooltip.innerHTML = content;
        tooltip.style.display = 'block';
        updateTooltipPosition(event);
    }

    /**
     * Update tooltip position
     */
    function updateTooltipPosition(event) {
        if (!tooltip) return;
        
        tooltip.style.left = (event.pageX + 15) + 'px';
        tooltip.style.top = (event.pageY + 15) + 'px';
    }

    /**
     * Hide tooltip and restore province color
     */
    function hideTooltip(event) {
        if (tooltip) {
            tooltip.style.display = 'none';
        }

        // Restore original color
        const provinceKey = event.target.getAttribute('data-province');
        const provinceData = currentData.find(d => d.province === provinceKey);
        const count = provinceData ? provinceData.userCount : 0;
        
        const { minCount, maxCount } = getDataRange(currentData);
        const color = getProvinceColor(count, minCount, maxCount);
        event.target.setAttribute('fill', color);
    }

    /**
     * Handle province click - show user list modal
     */
    function handleProvinceClick(provinceData) {
        if (!provinceData || provinceData.userCount === 0) return;

        // Dispatch custom event that dashboard.js will listen to
        const event = new CustomEvent('provinceClicked', {
            detail: {
                province: provinceData.province,
                displayName: provinceData.displayName,
                role: currentRole
            }
        });
        document.dispatchEvent(event);
    }

    // Public API
    return {
        init: init,
        render: render
    };
})();
