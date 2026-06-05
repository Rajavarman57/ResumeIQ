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

## Run Backend

Maven is required.

```bash
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080/api`.

Default login:

- Email: `admin@resumeiq.local`
- Password: `admin123`

For MySQL, create a database named `resumeiq` and update `backend/src/main/resources/application.properties`.

## Run Frontend

The frontend is dependency-free at install time and can be served by Node:

```bash
cd frontend
node server.js
```

Open `http://localhost:5173`.

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
