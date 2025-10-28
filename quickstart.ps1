# Quick Start Script for Animal Disease Reporting System
# Run this script to set up and start the application

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Animal Disease Reporting System Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if PostgreSQL is running
Write-Host "Checking PostgreSQL..." -ForegroundColor Yellow
$pgStatus = Get-Service -Name postgresql* -ErrorAction SilentlyContinue
if ($null -eq $pgStatus) {
    Write-Host "WARNING: PostgreSQL service not found. Make sure PostgreSQL is installed and running." -ForegroundColor Red
} else {
    Write-Host "PostgreSQL service status: $($pgStatus.Status)" -ForegroundColor Green
}

Write-Host ""

# Database Setup
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 1: Database Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
$setupDb = Read-Host "Do you want to set up the database? (y/n)"

if ($setupDb -eq "y") {
    Write-Host "Creating database and running migrations..." -ForegroundColor Yellow
    
    # Create database
    Write-Host "Creating database 'adrsdb'..." -ForegroundColor Yellow
    psql -U postgres -c "CREATE DATABASE adrsdb;"
    
    # Run init script
    if (Test-Path "database\init.sql") {
        Write-Host "Running init.sql..." -ForegroundColor Yellow
        psql -U postgres -d adrsdb -f database\init.sql
    }
    
    # Run seed scripts
    Write-Host "Running seed scripts..." -ForegroundColor Yellow
    Get-ChildItem "database\seed\*.sql" | Sort-Object Name | ForEach-Object {
        Write-Host "Running $($_.Name)..." -ForegroundColor Yellow
        psql -U postgres -d adrsdb -f $_.FullName
    }
    
    Write-Host "Database setup complete!" -ForegroundColor Green
}

Write-Host ""

# Backend Setup
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 2: Backend Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Check for .env file
if (-not (Test-Path "config\.env")) {
    Write-Host "Creating .env file from .env.example..." -ForegroundColor Yellow
    if (Test-Path "config\.env.example") {
        Copy-Item "config\.env.example" "config\.env"
        Write-Host "IMPORTANT: Please edit config\.env with your database credentials!" -ForegroundColor Red
        Read-Host "Press Enter to continue after editing .env file" | Out-Null
    }
}

$buildBackend = Read-Host "Do you want to build the backend? (y/n)"

if ($buildBackend -eq "y") {
    Write-Host "Building backend..." -ForegroundColor Yellow
    Set-Location backend
    mvn clean install -DskipTests
    Set-Location ..
    Write-Host "Backend build complete!" -ForegroundColor Green
}

Write-Host ""

# Frontend Setup
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 3: Frontend Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Check for frontend .env file
if (-not (Test-Path "frontend\.env")) {
    Write-Host "Creating frontend .env file from .env.example..." -ForegroundColor Yellow
    if (Test-Path "frontend\.env.example") {
        Copy-Item "frontend\.env.example" "frontend\.env"
    }
}

$installFrontend = Read-Host "Do you want to install frontend dependencies? (y/n)"

if ($installFrontend -eq "y") {
    Write-Host "Installing frontend dependencies..." -ForegroundColor Yellow
    Set-Location frontend
    npm install
    Set-Location ..
    Write-Host "Frontend dependencies installed!" -ForegroundColor Green
}

Write-Host ""

# Start Application
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Step 4: Start Application" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "Setup complete! You can now start the application." -ForegroundColor Green
Write-Host ""
Write-Host "To start the backend:" -ForegroundColor Cyan
Write-Host "  cd backend" -ForegroundColor White
Write-Host "  mvn spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "To start the frontend (in a new terminal):" -ForegroundColor Cyan
Write-Host "  cd frontend" -ForegroundColor White
Write-Host "  npm run dev" -ForegroundColor White
Write-Host ""
Write-Host "Default admin credentials:" -ForegroundColor Yellow
Write-Host "  Username: admin" -ForegroundColor White
Write-Host "  Password: admin123" -ForegroundColor White
Write-Host ""
Write-Host "Access the application at: http://localhost:3000" -ForegroundColor Green
Write-Host "API documentation at: http://localhost:8081/swagger-ui.html" -ForegroundColor Green
Write-Host ""

$startNow = Read-Host "Do you want to start both backend and frontend now? (y/n)"

if ($startNow -eq "y") {
    Write-Host "Starting backend..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend; mvn spring-boot:run"
    
    Start-Sleep -Seconds 2
    
    Write-Host "Starting frontend..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend; npm run dev"
    
    Write-Host ""
    Write-Host "Application is starting! Check the new terminal windows." -ForegroundColor Green
    Write-Host "Backend: http://localhost:8081" -ForegroundColor Cyan
    Write-Host "Frontend: http://localhost:3000" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
