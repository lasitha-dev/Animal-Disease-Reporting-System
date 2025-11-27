#!/usr/bin/env python3
"""
Extract district SVG paths from script.js and generate sri-lanka-map.js
"""

import re

# Read the original script.js file
with open('../src/main/resources/static/srilankan-map-svg-master/script.js', 'r', encoding='utf-8') as f:
    content = f.read()

# District mapping: variable_name -> (ENUM_NAME, Display Name)
district_map = {
    'colombo': ('COLOMBO', 'Colombo'),
    'gampaha': ('GAMPAHA', 'Gampaha'),
    'kalutara': ('KALUTARA', 'Kalutara'),
    'mahanuvara': ('KANDY', 'Kandy'),
    'matale': ('MATALE', 'Matale'),
    'nuwaraeliya': ('NUWARA_ELIYA', 'Nuwara Eliya'),
    'galle': ('GALLE', 'Galle'),
    'matara': ('MATARA', 'Matara'),
    'hambantota': ('HAMBANTOTA', 'Hambantota'),
    'jaffna': ('JAFFNA', 'Jaffna'),
    'kilinochchi': ('KILINOCHCHI', 'Kilinochchi'),
    'mannar': ('MANNAR', 'Mannar'),
    'vavuniya': ('VAVUNIYA', 'Vavuniya'),
    'mullaitivu': ('MULLAITIVU', 'Mullaitivu'),
    'batticaloa': ('BATTICALOA', 'Batticaloa'),
    'ampara': ('AMPARA', 'Ampara'),
    'trincomalee': ('TRINCOMALEE', 'Trincomalee'),
    'kurunegala': ('KURUNEGALA', 'Kurunegala'),
    'puttalam': ('PUTTALAM', 'Puttalam'),
    'anuradhapura': ('ANURADHAPURA', 'Anuradhapura'),
    'polonnaruwa': ('POLONNARUWA', 'Polonnaruwa'),
    'badulla': ('BADULLA', 'Badulla'),
    'moneragala': ('MONARAGALA', 'Monaragala'),
    'ratnapura': ('RATNAPURA', 'Ratnapura'),
    'kegalle': ('KEGALLE', 'Kegalle')
}

# Extract all district paths
districts = {}
for var_name, (enum_name, display_name) in district_map.items():
    pattern = rf'var {var_name} = rsr\.path\("([^"]+)"\);'
    match = re.search(pattern, content)
    if match:
        districts[enum_name] = {
            'name': display_name,
            'path': match.group(1)
        }
        print(f"✓ Extracted {display_name} ({enum_name})")
    else:
        print(f"✗ Failed to extract {display_name} ({enum_name})")

# Generate the output JavaScript file
output = """/**
 * Sri Lanka District Map - Interactive SVG Map
 * Maps 25 districts with SVG paths for visualization
 */

const DISTRICT_MAPPING = {
"""

for enum_name in sorted(districts.keys()):
    district = districts[enum_name]
    output += f"    '{enum_name}': {{\n"
    output += f"        name: '{district['name']}',\n"
    output += f"        path: \"{district['path']}\"\n"
    output += f"    }},\n"

output += """};

/**
 * Initialize the interactive district map
 * @param {string} containerId - ID of the container element
 * @param {Function} onDistrictClick - Callback function when district is clicked
 */
function initializeDistrictMap(containerId, onDistrictClick) {
    const container = document.getElementById(containerId);
    if (!container) {
        console.error('Map container not found:', containerId);
        return;
    }

    const paper = Raphael(containerId, 450, 793);
    const districts = [];

    // Create district paths
    for (const [districtKey, districtData] of Object.entries(DISTRICT_MAPPING)) {
        const district = paper.path(districtData.path);
        district.attr({
            fill: '#E5E7EB',
            stroke: '#F7F7F7',
            'stroke-width': 1,
            title: districtData.name
        });
        district.data('district', districtKey);
        district.data('name', districtData.name);

        // Hover effects
        district.hover(
            function() {
                this.attr({ opacity: 0.7, cursor: 'pointer' });
            },
            function() {
                this.attr({ opacity: 1 });
            }
        );

        // Click handler
        if (onDistrictClick) {
            district.click(function() {
                const districtKey = this.data('district');
                const districtName = this.data('name');
                onDistrictClick(districtKey, districtName);
            });
        }

        districts.push(district);
    }

    return { paper, districts };
}

/**
 * Update district colors based on user counts
 * @param {Object} distributionData - Array of {district, displayName, userCount}
 */
function updateDistrictColors(distributionData) {
    // Color scale based on user count
    const getColor = (count) => {
        if (count === 0) return '#E5E7EB';      // Gray
        if (count <= 5) return '#E0F2FE';       // Light blue
        if (count <= 15) return '#38BDF8';      // Medium blue
        return '#0369A1';                        // Dark blue
    };

    distributionData.forEach(item => {
        const districtElements = document.querySelectorAll(`[data-district="${item.district}"]`);
        districtElements.forEach(el => {
            if (el.__data__ && el.__data__.attr) {
                el.__data__.attr({ fill: getColor(item.userCount) });
            }
        });
    });
}
"""

# Write the output file
output_path = '../src/main/resources/static/js/sri-lanka-map.js'
with open(output_path, 'w', encoding='utf-8') as f:
    f.write(output)

print(f"\n✅ Successfully generated {output_path}")
print(f"📊 Total districts: {len(districts)}/25")
