/**
 * Responsive JavaScript
 * Handles sidebar toggle, mobile navigation, and responsive behaviors
 */

(function() {
    'use strict';

    // DOM Elements
    const sidebar = document.getElementById('dashboard-sidebar');
    const sidebarOverlay = document.getElementById('sidebar-overlay');
    const openSidebarBtn = document.getElementById('open-sidebar');
    const closeSidebarBtn = document.getElementById('close-sidebar');
    const mobileMenuMore = document.getElementById('mobile-menu-more');

    // Breakpoint for mobile
    const MOBILE_BREAKPOINT = 768;

    /**
     * Check if current viewport is mobile
     */
    function isMobile() {
        return window.innerWidth <= MOBILE_BREAKPOINT;
    }

    /**
     * Open sidebar
     */
    function openSidebar() {
        if (sidebar && sidebarOverlay) {
            sidebar.classList.add('active');
            sidebarOverlay.classList.add('active');
            document.body.style.overflow = 'hidden';
            
            // Focus management for accessibility
            closeSidebarBtn?.focus();
        }
    }

    /**
     * Close sidebar
     */
    function closeSidebar() {
        if (sidebar && sidebarOverlay) {
            sidebar.classList.remove('active');
            sidebarOverlay.classList.remove('active');
            document.body.style.overflow = '';
            
            // Return focus to open button
            openSidebarBtn?.focus();
        }
    }

    /**
     * Toggle sidebar
     */
    function toggleSidebar() {
        if (sidebar?.classList.contains('active')) {
            closeSidebar();
        } else {
            openSidebar();
        }
    }

    /**
     * Handle window resize
     */
    function handleResize() {
        // Close sidebar when switching to desktop
        if (!isMobile() && sidebar?.classList.contains('active')) {
            closeSidebar();
        }
    }

    /**
     * Handle escape key to close sidebar
     */
    function handleKeyDown(e) {
        if (e.key === 'Escape' && sidebar?.classList.contains('active')) {
            closeSidebar();
        }
    }

    /**
     * Initialize responsive table card views
     * Converts tables to card view on mobile
     */
    function initResponsiveTables() {
        if (!isMobile()) return;

        const tables = document.querySelectorAll('.data-table');
        
        tables.forEach(table => {
            const headers = Array.from(table.querySelectorAll('thead th'));
            const rows = table.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const cells = row.querySelectorAll('td');
                cells.forEach((cell, index) => {
                    if (headers[index]) {
                        cell.setAttribute('data-label', headers[index].textContent.trim());
                    }
                });
            });
        });
    }

    /**
     * Handle "More" menu in mobile bottom nav
     */
    function handleMoreMenu() {
        // Open sidebar when "More" is clicked
        openSidebar();
    }

    /**
     * Initialize touch events for better mobile experience
     */
    function initTouchEvents() {
        let touchStartX = 0;
        let touchEndX = 0;
        const swipeThreshold = 100;

        // Swipe from left edge to open sidebar
        document.addEventListener('touchstart', (e) => {
            touchStartX = e.changedTouches[0].screenX;
        }, { passive: true });

        document.addEventListener('touchend', (e) => {
            touchEndX = e.changedTouches[0].screenX;
            handleSwipe();
        }, { passive: true });

        function handleSwipe() {
            const swipeDistance = touchEndX - touchStartX;
            
            // Swipe right from left edge (open sidebar)
            if (swipeDistance > swipeThreshold && touchStartX < 50 && isMobile()) {
                openSidebar();
            }
            
            // Swipe left (close sidebar)
            if (swipeDistance < -swipeThreshold && sidebar?.classList.contains('active')) {
                closeSidebar();
            }
        }
    }

    /**
     * Update viewport height for mobile browsers
     * Fixes 100vh issue on mobile Safari
     */
    function updateViewportHeight() {
        const vh = window.innerHeight * 0.01;
        document.documentElement.style.setProperty('--vh', `${vh}px`);
    }

    /**
     * Debounce function for resize events
     */
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    /**
     * Initialize all responsive behaviors
     */
    function init() {
        // Event listeners for sidebar
        openSidebarBtn?.addEventListener('click', openSidebar);
        closeSidebarBtn?.addEventListener('click', closeSidebar);
        sidebarOverlay?.addEventListener('click', closeSidebar);
        mobileMenuMore?.addEventListener('click', (e) => {
            e.preventDefault();
            handleMoreMenu();
        });

        // Keyboard events
        document.addEventListener('keydown', handleKeyDown);

        // Resize handling
        const debouncedResize = debounce(() => {
            handleResize();
            updateViewportHeight();
        }, 100);
        window.addEventListener('resize', debouncedResize);

        // Initial setup
        updateViewportHeight();
        initResponsiveTables();
        initTouchEvents();

        // Re-init tables on orientation change
        window.addEventListener('orientationchange', () => {
            setTimeout(initResponsiveTables, 100);
        });
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    // Expose functions globally if needed
    window.responsiveUtils = {
        openSidebar,
        closeSidebar,
        toggleSidebar,
        isMobile
    };

})();
