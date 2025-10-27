#!/bin/bash

# =====================================================
# Animal Disease Reporting System - Stop Script
# =====================================================
# This script stops the running Spring Boot application
# =====================================================

echo "=========================================="
echo "Animal Disease Reporting System"
echo "Stopping Application..."
echo "=========================================="

# Find process running on port 8080
PID=$(lsof -ti:8080)

if [ -z "$PID" ]; then
    echo "No application found running on port 8080"
else
    echo "Stopping process $PID..."
    kill -9 $PID
    echo "Application stopped successfully"
fi

echo "=========================================="
