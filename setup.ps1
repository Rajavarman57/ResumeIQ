#!/usr/bin/env powershell
# ResumeIQ Setup Script for Windows (PowerShell)
# Usage: powershell -ExecutionPolicy Bypass -File setup.ps1

param(
    [switch]$SkipPrerequisiteCheck = $false,
    [switch]$BackendOnly = $false,
    [switch]$FrontendOnly = $false
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "     ResumeIQ Project Setup" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Function to check command exists
function Test-CommandExists {
    param($Command)
    $null = Get-Command $Command -ErrorAction SilentlyContinue
    return $?
}

# Prerequisite checks
if (-not $SkipPrerequisiteCheck) {
    Write-Host "Checking prerequisites..." -ForegroundColor Yellow
    
    $allGood = $true
    
    # Check Node.js
    if (Test-CommandExists node) {
        $nodeVersion = node --version
        Write-Host "[✓] Node.js installed: $nodeVersion" -ForegroundColor Green
    } else {
        Write-Host "[✗] Node.js not found. Install from: https://nodejs.org/" -ForegroundColor Red
        $allGood = $false
    }
    
    # Check Java
    if (Test-CommandExists java) {
        $javaVersion = java -version 2>&1 | Select-Object -First 1
        Write-Host "[✓] Java installed: $javaVersion" -ForegroundColor Green
    } else {
        Write-Host "[✗] Java not found. Install from: https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Red
        $allGood = $false
    }
    
    # Check Maven
    $hasMaven = Test-CommandExists mvn
    $hasMvnw = Test-Path "$ProjectRoot\backend\mvnw.cmd"
    
    if ($hasMaven) {
        $mavenVersion = mvn --version 2>&1 | Select-Object -First 1
        Write-Host "[✓] Maven installed: $mavenVersion" -ForegroundColor Green
    } elseif ($hasMvnw) {
        Write-Host "[✓] Maven Wrapper available" -ForegroundColor Green
    } else {
        Write-Host "[⚠] Maven not found - will use Maven Wrapper" -ForegroundColor Yellow
    }
    
    if (-not $allGood) {
        Write-Host "`n[ERROR] Please install missing prerequisites" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "[✓] All prerequisites satisfied`n" -ForegroundColor Green
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Starting ResumeIQ Services" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Start Backend
if (-not $FrontendOnly) {
    Write-Host "Starting Backend (Spring Boot)..." -ForegroundColor Yellow
    Write-Host "Port: 8080" -ForegroundColor Gray
    
    $backendPath = "$ProjectRoot\backend"
    $process = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/k cd /d `"$backendPath`" && mvn clean spring-boot:run" `
        -PassThru `
        -WindowStyle Normal
    
    Write-Host "Backend process started (PID: $($process.Id))" -ForegroundColor Green
    
    # Wait for backend to initialize
    Write-Host "`nWaiting for backend to start (15 seconds)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 15
}

# Start Frontend
if (-not $BackendOnly) {
    Write-Host "`nStarting Frontend (Node Server)..." -ForegroundColor Yellow
    Write-Host "Port: 5173" -ForegroundColor Gray
    
    $frontendPath = "$ProjectRoot\frontend"
    $process = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/k cd /d `"$frontendPath`" && node server.js" `
        -PassThru `
        -WindowStyle Normal
    
    Write-Host "Frontend process started (PID: $($process.Id))" -ForegroundColor Green
}

# Success message
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   ResumeIQ Services Started!" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

if (-not $BackendOnly) {
    Write-Host "🌐 Frontend: http://localhost:5173" -ForegroundColor Green
}

if (-not $FrontendOnly) {
    Write-Host "🔌 Backend API: http://localhost:8080/api" -ForegroundColor Green
}

Write-Host "`n📋 Default Credentials:" -ForegroundColor Cyan
Write-Host "   Email: admin@resumeiq.local" -ForegroundColor Gray
Write-Host "   Password: admin123" -ForegroundColor Gray

Write-Host "`n⚠️  Both services are running in separate windows." -ForegroundColor Yellow
Write-Host "Keep the command windows open while using the application." -ForegroundColor Yellow
Write-Host ""
