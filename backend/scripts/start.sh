#!/bin/bash

# =====================================================
# Animal Disease Reporting System - Start Script
# =====================================================
# This script starts the Spring Boot application
# =====================================================

echo "=========================================="
echo "Animal Disease Reporting System"
echo "Starting Application..."
echo "=========================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven first: https://maven.apache.org/install.html"
    exit 1
fi

# Navigate to backend directory
cd "$(dirname "$0")/.." || exit

# Check if .env file exists
if [ ! -f "../config/.env" ]; then
    echo "WARNING: .env file not found in config directory"
    echo "Using default configuration from application.properties"
    echo "It's recommended to create .env file from .env.example"
fi

# Clean and build the project
echo ""
echo "Building application..."
mvn clean package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful! Starting application..."
    echo ""
    
    # Run the application
    mvn spring-boot:run
else
    echo ""
    echo "ERROR: Build failed. Please check the error messages above."
    exit 1
fi
