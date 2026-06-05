@echo off
REM ResumeIQ Setup Script for Windows
REM This script sets up and runs the entire project

echo.
echo ========================================
echo     ResumeIQ Project Setup
echo ========================================
echo.

REM Check for Node.js
echo Checking Node.js installation...
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is not installed. Please install from https://nodejs.org/
    exit /b 1
)
echo [OK] Node.js is installed

REM Check for Java
echo Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed. Please install Java 23 or later.
    exit /b 1
)
echo [OK] Java is installed

REM Check for Maven
echo Checking Maven installation...
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Maven not found. Installing Maven Wrapper...
    cd backend
    powershell -Command "Invoke-WebRequest -Uri 'https://github.com/apache/maven-mvnw/raw/master/mvnw.cmd' -OutFile 'mvnw.cmd'"
    powershell -Command "Invoke-WebRequest -Uri 'https://github.com/apache/maven-mvnw/raw/master/.mvn/wrapper/maven-wrapper.jar' -OutFile '.mvn/wrapper/maven-wrapper.jar'"
    cd ..
)
echo [OK] Maven is available

echo.
echo ========================================
echo   Starting ResumeIQ Services
echo ========================================
echo.

echo Starting Backend (Spring Boot)...
cd backend
start "ResumeIQ Backend" cmd /k mvn clean spring-boot:run
cd ..

echo.
echo Waiting for backend to start (10 seconds)...
timeout /t 10 /nobreak

echo.
echo Starting Frontend (Node Server)...
cd frontend
start "ResumeIQ Frontend" cmd /k node server.js
cd ..

echo.
echo ========================================
echo   ResumeIQ is Starting!
echo ========================================
echo.
echo Frontend: http://localhost:5173
echo Backend API: http://localhost:8080/api
echo.
echo Default Credentials:
echo   Email: admin@resumeiq.local
echo   Password: admin123
echo.
echo Both services are starting in separate windows...
echo.
pause
