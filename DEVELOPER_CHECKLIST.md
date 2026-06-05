# ResumeIQ - Developer Onboarding Checklist

Use this checklist to ensure you have everything set up correctly.

## ✅ Pre-Development Setup

### Prerequisites Installation
- [ ] **Java 23+** installed
  - Download: https://www.oracle.com/java/technologies/downloads/
  - Verify: Run `java -version` in terminal
  - Expected: `java version "23" or higher`

- [ ] **Node.js 16+** installed
  - Download: https://nodejs.org/
  - Verify: Run `node --version` in terminal
  - Expected: `v16.0.0` or higher

- [ ] **Maven 3.8+** (optional but recommended)
  - Download: https://maven.apache.org/download.cgi
  - Add to PATH environment variable
  - Verify: Run `mvn --version` in terminal
  - Or let automated setup script handle it

### Environment Setup
- [ ] Clone/extract ResumeIQ project
- [ ] Navigate to project directory
- [ ] Verify these files exist:
  - [ ] `backend/pom.xml`
  - [ ] `frontend/server.js`
  - [ ] `README.md`
  - [ ] `setup.cmd`

## 🚀 Running the Project

### First Time - Automated Setup (Recommended)
- [ ] Double-click `setup.cmd` in project root
- [ ] Wait for both services to start
- [ ] Open http://localhost:5173 in browser
- [ ] Test with credentials:
  - Email: `admin@resumeiq.local`
  - Password: `admin123`

### Manual Setup
- [ ] Open 2 terminal/command prompt windows

**Window 1 - Backend:**
```
cd backend
mvn clean spring-boot:run
```
- [ ] Wait for "Started ResumeIQ in X seconds"
- [ ] Verify: Open http://localhost:8080/api in browser

**Window 2 - Frontend:**
```
cd frontend
node server.js
```
- [ ] Wait for "ResumeIQ frontend: http://localhost:5173"
- [ ] Verify: Open http://localhost:5173 in browser

## 🌐 Verification Tests

### Frontend Tests
- [ ] http://localhost:5173 loads successfully
- [ ] Page displays without 404 errors
- [ ] CSS styling appears correct
- [ ] Can interact with UI elements

### Backend Tests
- [ ] http://localhost:8080/api responds
- [ ] Can make HTTP requests to backend
- [ ] Database connection is working
- [ ] No compilation errors in terminal

### Integration Tests
- [ ] Frontend can call backend API
- [ ] Login functionality works
- [ ] Job creation/management works
- [ ] Resume upload/parsing works

## 📁 Project Structure Verification

Verify you understand the project layout:

```
ResumeIQ/
├── README.md                  ← Updated with quick start
├── SETUP_GUIDE.md            ← Detailed setup instructions
├── TROUBLESHOOTING.md        ← Common issues & solutions
├── SETUP_COMPLETE.md         ← This setup summary
├── .env.example              ← Configuration reference
├── setup.cmd                 ← Windows auto-setup (NEW)
├── setup.ps1                 ← PowerShell setup (NEW)
│
├── backend/                  ← Spring Boot API
│   ├── pom.xml              ← Maven dependencies
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/        ← Java source code
│   │   │   └── resources/   ← Configuration files
│   │   └── test/            ← Test files
│   ├── target/              ← Compiled files (auto-generated)
│   └── data/                ← H2 database files (auto-created)
│
└── frontend/                ← React frontend
    ├── server.js            ← Node.js server
    ├── index.html           ← HTML entry point
    ├── app.js               ← Main app logic
    └── styles.css           ← Styling
```

## 🔧 Configuration Check

### Backend Port
- [ ] Verify port 8080 is available
- [ ] Or change in `backend/src/main/resources/application.properties`
  ```properties
  server.port=8080  # Change if needed
  ```

### Frontend Port
- [ ] Verify port 5173 is available
- [ ] Or change in `frontend/server.js`
  ```javascript
  const port = process.env.PORT || 5173; // Change 5173
  ```

### Database
- [ ] Current: **H2** (in-memory, local development)
- [ ] For MySQL: See `SETUP_GUIDE.md` database section

## 🐛 Troubleshooting Checklist

If something doesn't work, check:

- [ ] All prerequisites installed? Run:
  ```bash
  java -version
  node --version
  mvn --version
  ```

- [ ] Ports available?
  ```bash
  # Windows
  netstat -ano | findstr :8080
  netstat -ano | findstr :5173
  ```

- [ ] Correct directory? Should be in project root:
  ```bash
  cd C:\...\ResumeIQ
  ```

- [ ] Java version correct? (Needs 23+)
  ```bash
  java -version
  ```

- [ ] Maven working? (If installed)
  ```bash
  mvn --version
  ```

- [ ] See `TROUBLESHOOTING.md` for detailed solutions

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | Project overview and quick start |
| `SETUP_GUIDE.md` | Detailed setup and configuration |
| `TROUBLESHOOTING.md` | Common issues and solutions |
| `SETUP_COMPLETE.md` | Setup summary and status |
| `.env.example` | Configuration reference |
| `setup.cmd` | Windows automated setup script |
| `setup.ps1` | PowerShell setup script |

## ✨ You're Ready!

If all checkboxes are complete, you're all set to:
- ✅ Run the project without errors
- ✅ Develop new features
- ✅ Debug issues using troubleshooting guide
- ✅ Configure for different environments
- ✅ Onboard other developers using this checklist

## 💡 Pro Tips

1. **Keep terminals open** while developing (don't close backend/frontend windows)
2. **Use setup.cmd** for fastest startup on Windows
3. **Check logs** in terminal for detailed error messages
4. **Clear browser cache** (Ctrl+Shift+Delete) if UI doesn't update
5. **Refer to documentation** when issues occur

## 🆘 Need Help?

1. Check relevant section in `TROUBLESHOOTING.md`
2. Verify all prerequisites are installed
3. Ensure ports 8080 and 5173 are available
4. Check terminal output for specific error messages
5. Review project configuration files

---

**Last Updated:** 2026-06-05
**Version:** 1.0
**Status:** ✅ Complete and tested
