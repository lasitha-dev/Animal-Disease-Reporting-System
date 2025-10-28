# Frontend - Animal Disease Reporting System

## 🔹 Future Web Dashboard

This directory is a placeholder for the future web-based dashboard for the Animal Disease Reporting System.

Currently, the application uses **Thymeleaf** as the template engine on the backend for server-side rendering. This frontend directory is reserved for potential future enhancements with a modern JavaScript framework.

## Planned Architecture

- **Framework:** React.js or Vue.js (TBD)
- **Build Tool:** Vite or Webpack
- **State Management:** Redux/Context API or Vuex
- **UI Library:** Material-UI, Ant Design, or Bootstrap
- **Maps Integration:** Leaflet.js or Google Maps API
- **Charts:** Chart.js or Recharts

## Directory Structure

```
frontend/
├── public/              # Static assets
├── src/
│   ├── components/      # Reusable UI components
│   ├── pages/          # Page-level components
│   ├── services/       # API service calls
│   ├── hooks/          # Custom React hooks
│   ├── assets/         # Images, fonts, etc.
│   └── App.jsx         # Main app component
├── package.json        # Node.js dependencies
├── vite.config.js      # Build configuration
└── README.md          # This file
```

## Current Status

⚠️ **Not Yet Implemented**

The current application uses Thymeleaf templates located in:
- `backend/src/main/resources/templates/`

## Future Integration

When implementing this frontend:

1. Update `pom.xml` to serve static frontend build
2. Configure CORS in Spring Boot backend
3. Create REST API endpoints for data access
4. Build and deploy frontend assets to `backend/src/main/resources/static/`

## Development Setup (Future)

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build
```

---

**Note:** This is a placeholder for future development. Focus remains on the Spring Boot + Thymeleaf implementation for now.
