/**
 * Custom Select Dropdown Component
 * Provides a mobile-friendly styled dropdown that replaces native select elements
 */

class CustomSelect {
    constructor(selectElement, options = {}) {
        this.selectElement = selectElement;
        this.options = {
            placeholder: options.placeholder || 'Select an option...',
            title: options.title || 'Select Option',
            ...options
        };
        this.isOpen = false;
        this.init();
    }

    init() {
        // Hide the original select
        this.selectElement.style.display = 'none';

        // Create custom select wrapper
        this.wrapper = document.createElement('div');
        this.wrapper.className = 'custom-select-wrapper';

        // Create trigger button
        this.trigger = document.createElement('button');
        this.trigger.type = 'button';
        this.trigger.className = 'custom-select-trigger placeholder';
        this.trigger.innerHTML = `
            <span class="custom-select-value">${this.options.placeholder}</span>
            <svg class="arrow" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 12 12">
                <path fill="currentColor" d="M6 8L1 3h10z"/>
            </svg>
        `;

        // Create options container
        this.optionsContainer = document.createElement('div');
        this.optionsContainer.className = 'custom-select-options';
        this.optionsContainer.innerHTML = `
            <div class="custom-select-options-header">
                <span>${this.options.title}</span>
                <button type="button" class="close-btn" aria-label="Close">×</button>
            </div>
            <div class="custom-select-options-list"></div>
        `;

        // Build options list
        this.buildOptions();

        // Insert into DOM
        this.selectElement.parentNode.insertBefore(this.wrapper, this.selectElement);
        this.wrapper.appendChild(this.trigger);
        this.wrapper.appendChild(this.optionsContainer);
        this.wrapper.appendChild(this.selectElement);

        // Bind events
        this.bindEvents();

        // Set initial value if exists
        this.syncFromSelect();
    }

    buildOptions() {
        const listContainer = this.optionsContainer.querySelector('.custom-select-options-list');
        listContainer.innerHTML = '';

        Array.from(this.selectElement.options).forEach((option, index) => {
            if (index === 0 && option.value === '') {
                // Skip placeholder option
                return;
            }

            const optionEl = document.createElement('div');
            optionEl.className = 'custom-select-option';
            optionEl.dataset.value = option.value;
            optionEl.textContent = option.textContent;
            optionEl.setAttribute('role', 'option');
            optionEl.setAttribute('tabindex', '0');

            if (option.selected && option.value !== '') {
                optionEl.classList.add('selected');
            }

            listContainer.appendChild(optionEl);
        });
    }

    bindEvents() {
        // Toggle dropdown
        this.trigger.addEventListener('click', (e) => {
            e.preventDefault();
            this.toggle();
        });

        // Close button (mobile)
        const closeBtn = this.optionsContainer.querySelector('.close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.close();
            });
        }

        // Option selection
        this.optionsContainer.addEventListener('click', (e) => {
            const option = e.target.closest('.custom-select-option');
            if (option) {
                this.selectOption(option.dataset.value);
            }
        });

        // Keyboard navigation
        this.optionsContainer.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                const option = e.target.closest('.custom-select-option');
                if (option) {
                    e.preventDefault();
                    this.selectOption(option.dataset.value);
                }
            }
        });

        // Close on outside click
        document.addEventListener('click', (e) => {
            if (!this.wrapper.contains(e.target)) {
                this.close();
            }
        });

        // Close on escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isOpen) {
                this.close();
            }
        });

        // Listen for changes to the original select (for programmatic updates)
        this.selectElement.addEventListener('change', () => {
            this.syncFromSelect();
        });
    }

    toggle() {
        if (this.isOpen) {
            this.close();
        } else {
            this.open();
        }
    }

    open() {
        this.isOpen = true;
        this.wrapper.classList.add('open');
        
        // Create backdrop for mobile
        if (window.innerWidth <= 768) {
            this.createBackdrop();
        }
    }

    close() {
        this.isOpen = false;
        this.wrapper.classList.remove('open');
        this.removeBackdrop();
    }

    createBackdrop() {
        if (this.backdrop) return;
        
        this.backdrop = document.createElement('div');
        this.backdrop.className = 'custom-select-backdrop';
        this.backdrop.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 9998;
        `;
        
        this.backdrop.addEventListener('click', () => this.close());
        document.body.appendChild(this.backdrop);
    }

    removeBackdrop() {
        if (this.backdrop) {
            this.backdrop.remove();
            this.backdrop = null;
        }
    }

    selectOption(value) {
        // Update native select
        this.selectElement.value = value;
        
        // Trigger change event on native select
        const event = new Event('change', { bubbles: true });
        this.selectElement.dispatchEvent(event);

        // Update UI
        this.syncFromSelect();
        this.close();
    }

    syncFromSelect() {
        const selectedOption = this.selectElement.options[this.selectElement.selectedIndex];
        const valueSpan = this.trigger.querySelector('.custom-select-value');

        if (selectedOption && selectedOption.value !== '') {
            valueSpan.textContent = selectedOption.textContent;
            this.trigger.classList.remove('placeholder');
        } else {
            valueSpan.textContent = this.options.placeholder;
            this.trigger.classList.add('placeholder');
        }

        // Update selected state in options list
        const options = this.optionsContainer.querySelectorAll('.custom-select-option');
        options.forEach(opt => {
            opt.classList.toggle('selected', opt.dataset.value === this.selectElement.value);
        });
    }

    // Refresh options (call this when options are dynamically loaded)
    refresh() {
        this.buildOptions();
        this.syncFromSelect();
    }

    // Set value programmatically
    setValue(value) {
        this.selectElement.value = value;
        this.syncFromSelect();
    }

    // Get current value
    getValue() {
        return this.selectElement.value;
    }

    // Destroy and restore original select
    destroy() {
        this.removeBackdrop();
        this.selectElement.style.display = '';
        this.wrapper.parentNode.insertBefore(this.selectElement, this.wrapper);
        this.wrapper.remove();
        
        // Remove from registry
        if (this.selectElement.id && CustomSelect.instances[this.selectElement.id]) {
            delete CustomSelect.instances[this.selectElement.id];
        }
    }
}

// Static registry to store all CustomSelect instances by select element ID
CustomSelect.instances = {};

/**
 * Get CustomSelect instance by select element ID
 * @param {string} selectId - The ID of the original select element
 * @returns {CustomSelect|null} The CustomSelect instance or null
 */
CustomSelect.getInstance = function(selectId) {
    return CustomSelect.instances[selectId] || null;
};

/**
 * Refresh a CustomSelect by select element ID
 * Useful when options are dynamically loaded
 * @param {string} selectId - The ID of the original select element
 */
CustomSelect.refresh = function(selectId) {
    const instance = CustomSelect.getInstance(selectId);
    if (instance) {
        instance.refresh();
    }
};

/**
 * Initialize CustomSelect for a select element
 * @param {HTMLSelectElement|string} selectOrId - Select element or its ID
 * @param {Object} options - CustomSelect options
 * @returns {CustomSelect} The created instance
 */
CustomSelect.init = function(selectOrId, options = {}) {
    const select = typeof selectOrId === 'string' 
        ? document.getElementById(selectOrId) 
        : selectOrId;
    
    if (!select) return null;
    
    // Destroy existing instance if any
    const existingInstance = CustomSelect.getInstance(select.id);
    if (existingInstance) {
        existingInstance.destroy();
    }
    
    // Create new instance
    const instance = new CustomSelect(select, options);
    
    // Register in registry
    if (select.id) {
        CustomSelect.instances[select.id] = instance;
    }
    
    return instance;
};

// Auto-initialize for elements with data-custom-select attribute
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-custom-select]').forEach(select => {
        CustomSelect.init(select, {
            placeholder: select.dataset.placeholder || 'Select an option...',
            title: select.dataset.title || 'Select Option'
        });
    });
});

// Export for use in other scripts
window.CustomSelect = CustomSelect;
