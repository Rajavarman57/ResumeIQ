# ResumeIQ - Complete Setup Guide

## Prerequisites

Before running the project, ensure you have the following installed:

### 1. **Java 23 or Later**
- Download from: https://www.oracle.com/java/technologies/downloads/
- Verify installation:
  ```bash
  java -version
  ```
- Required for Spring Boot backend

### 2. **Node.js (v16+)**
- Download from: https://nodejs.org/
- Verify installation:
  ```bash
  node --version
  ```
- Required for frontend server

### 3. **Maven (Optional - Auto-installed)**
- If not installed, run `setup.cmd` to auto-configure Maven Wrapper
- Or manually install from: https://maven.apache.org/download.cgi
- Verify installation:
  ```bash
  mvn --version
  ```

## Quick Start (Windows)

### Option 1: Automated Setup (Recommended)
Double-click `setup.cmd` in the project root. This will:
- ✅ Check all prerequisites
- ✅ Start Spring Boot backend on port 8080
- ✅ Start Node frontend on port 5173
- ✅ Open both in separate command windows

### Option 2: Manual Setup

#### Terminal 1 - Start Backend:
```bash
cd backend
mvn clean spring-boot:run
```
Backend will start at: `http://localhost:8080/api`

#### Terminal 2 - Start Frontend:
```bash
cd frontend
node server.js
```
Frontend will start at: `http://localhost:5173`

## Default Login Credentials

```
Email: admin@resumeiq.local
Password: admin123
```

## Database Configuration

### Current: H2 Database (Default)
- In-memory database for local demo
- No configuration needed
- Data is lost when server stops

### Production: MySQL
To use MySQL instead:

1. Create database:
   ```sql
   CREATE DATABASE resumeiq;
   ```

2. Update `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/resumeiq
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. Restart backend

## Troubleshooting

### Maven not found
- Run `setup.cmd` to configure Maven Wrapper, OR
- Install Maven: https://maven.apache.org/download.cgi

### Port 8080 already in use
- Change backend port in `backend/src/main/resources/application.properties`:
  ```properties
  server.port=8081
  ```

### Port 5173 already in use
- Change frontend port in `frontend/server.js`:
  ```javascript
  const PORT = 5174; // Change this
  ```

### Java version mismatch
- Backend requires Java 23 (check `pom.xml`)
- Install compatible version: https://www.oracle.com/java/technologies/downloads/

### Backend won't start
1. Check Java version: `java -version`
2. Check Maven: `mvn --version` (or use `setup.cmd`)
3. View logs in backend terminal for detailed errors

## Project Structure

```
ResumeIQ/
├── frontend/              # React frontend (port 5173)
│   ├── app.js            # Main app component
│   ├── server.js         # Node.js server
│   ├── index.html        # HTML entry point
│   └── styles.css        # Styling
├── backend/              # Spring Boot API (port 8080)
│   ├── src/              # Java source code
│   ├── pom.xml           # Maven dependencies
│   └── target/           # Compiled files
├── setup.cmd             # Windows auto-setup script
└── README.md             # Project documentation
```

## Available APIs

Once backend is running at `http://localhost:8080/api`:

- `POST /auth/login` - User login
- `POST /jobs` - Create job role
- `POST /resumes/upload` - Upload resume
- `GET /candidates` - List candidates
- `POST /matching/score` - Calculate match score

See backend Swagger UI (if enabled) or API documentation for complete endpoint list.

## Tips

- **Keep terminals open**: Don't close the backend/frontend terminal windows while developing
- **Clear cache**: If UI doesn't update, clear browser cache or use Ctrl+Shift+Delete
- **Database reset**: Delete `./backend/target/` and restart to reset H2 database
- **Mobile testing**: Access frontend from another device: `http://<your-ip>:5173`

## Support

For issues:
1. Check logs in both terminal windows
2. Verify all prerequisites are installed
3. Ensure ports 8080 and 5173 are available
4. Check `backend/src/main/resources/application.properties` for configuration
