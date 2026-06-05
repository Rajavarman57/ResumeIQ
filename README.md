# ResumeIQ

ResumeIQ is an AI-assisted recruitment and talent matching platform. It parses resumes, extracts skills, ranks candidates against multiple job roles, suggests alternative roles, and supports applicant workflow states such as selected, under review, waiting list, and rejected.

## Tech Stack

- Frontend: React, Bootstrap, HTML, CSS, JavaScript
- Backend: Java Spring Boot, Spring Security, Spring Data JPA
- Database: MySQL for production, H2 for local demo
- Resume parsing: Apache PDFBox for PDF, Apache POI for DOCX

## Project Structure

```text
backend/   Spring Boot REST API
frontend/  React single-page frontend
```

## Prerequisites

- **Java 23+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Node.js 16+** - [Download](https://nodejs.org/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi) (optional if using Maven Wrapper)

Verify installation:
```bash
java -version
node --version
mvn --version
```

## Quick Start (All Platforms)

### Windows - Automated Setup (Recommended)

**Double-click `setup.cmd`** in the project root to start both services automatically.

Or use PowerShell:
```bash
powershell -ExecutionPolicy Bypass -File setup.ps1
```

### Manual Setup - Terminal 1 (Backend)

```bash
cd backend
mvn clean spring-boot:run
```

✅ Backend API: `http://localhost:8080/api`

### Manual Setup - Terminal 2 (Frontend)

```bash
cd frontend
node server.js
```

✅ Frontend: `http://localhost:5173`

## Default Login

```
Email: admin@resumeiq.local
Password: admin123
```

## Database Configuration

### Default: H2 (In-Memory)
- Auto-configured in `backend/src/main/resources/application.properties`
- Perfect for local development and demos
- Data resets on server restart

### Production: MySQL
1. Create database: `CREATE DATABASE resumeiq;`
2. Update `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/resumeiq
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
3. Restart backend

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `mvn` not found | Run `setup.cmd` or install Maven from https://maven.apache.org/ |
| Port 8080 in use | Change `server.port` in `backend/src/main/resources/application.properties` |
| Port 5173 in use | Change `PORT` in `frontend/server.js` |
| Java version mismatch | Install Java 23: https://www.oracle.com/java/technologies/downloads/ |
| Backend won't start | Check Java: `java -version` and Maven: `mvn --version` |

See **SETUP_GUIDE.md** for detailed troubleshooting and configuration.

## Core Features

- Recruiter/admin login
- Job role management
- Resume upload and storage
- PDF/DOCX parsing
- Skill extraction
- Experience and education analysis
- Resume-to-job match score
- Skill gap analysis
- ATS compatibility score
- Resume improvement suggestions
- Keyword recommendations
- Candidate ranking
- Best/least match sorting
- Custom skill filtering
- Alternative job role recommendations
- Cross-role matching and suggested applicants
- Candidate status workflow
- Analytics dashboard
- CSV hiring report export
