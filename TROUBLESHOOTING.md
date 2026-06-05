# ResumeIQ - Troubleshooting & Common Issues

## ✅ Verification Checklist

Before running the project, verify:

- [ ] Java 23+ installed: `java -version`
- [ ] Node.js 16+ installed: `node --version`
- [ ] Maven 3.8+ installed or available: `mvn --version`
- [ ] Port 8080 available (for backend)
- [ ] Port 5173 available (for frontend)
- [ ] Write permissions in project directory

## 🐛 Common Issues & Solutions

### 1. Maven Not Found / "mvn is not recognized"

**Symptoms:**
```
'mvn' is not recognized as the name of a cmdlet, function, script file, or operable program
```

**Solutions:**

**Option A: Install Maven** (Recommended)
1. Download from: https://maven.apache.org/download.cgi
2. Extract to a folder (e.g., `C:\Tools\maven-3.9.0`)
3. Add to system PATH:
   - Open Environment Variables (Win+X → System)
   - Edit system PATH variable
   - Add Maven bin folder: `C:\Tools\maven-3.9.0\bin`
4. Verify: `mvn --version`

**Option B: Use Maven Wrapper** (Windows Users)
```bash
cd backend
# Maven will auto-download on first run
mvn clean spring-boot:run
```

**Option C: Use the Setup Scripts**
```bash
# Double-click setup.cmd in project root
# OR
powershell -ExecutionPolicy Bypass -File setup.ps1
```

---

### 2. Java Version Mismatch

**Symptoms:**
```
[ERROR] Fatal error compiling: invalid flag: -release
[ERROR] 1 error
```

**Cause:** Project requires Java 23, but you have Java 8/11/17

**Solution:**
1. Install Java 23: https://www.oracle.com/java/technologies/downloads/
2. Set JAVA_HOME:
   ```bash
   # Windows - Set environment variable
   set JAVA_HOME=C:\Program Files\Java\jdk-23
   ```
   ```bash
   # macOS/Linux
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-23.jdk/Contents/Home
   ```
3. Verify: `java -version`
4. Restart terminals and try again

**Alternative:** Update `backend/pom.xml` to use your Java version:
```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

---

### 3. Port Already in Use

**Symptoms:**
```
Address already in use: bind
```

**Backend (Port 8080):**

Option 1 - Change port in `backend/src/main/resources/application.properties`:
```properties
server.port=8081
```
Then access: `http://localhost:8081/api`

Option 2 - Kill existing process:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux
lsof -i :8080
kill -9 <PID>
```

**Frontend (Port 5173):**

Option 1 - Change port via environment variable:
```bash
cd frontend
set PORT=5174
node server.js
```

Option 2 - Kill existing process:
```bash
# Windows
netstat -ano | findstr :5173
taskkill /PID <PID> /F

# macOS/Linux
lsof -i :5173
kill -9 <PID>
```

---

### 4. Backend Won't Start - Compilation Errors

**Symptoms:**
```
[ERROR] COMPILATION ERROR
[ERROR] /path/to/file.java:[line] error:
```

**Solutions:**

1. Clean and rebuild:
   ```bash
   cd backend
   mvn clean
   mvn compile
   ```

2. Check for syntax errors in Java files:
   ```bash
   mvn validate
   ```

3. Update dependencies:
   ```bash
   mvn dependency:resolve
   ```

4. Full rebuild:
   ```bash
   mvn clean install -DskipTests
   ```

5. Check logs for specific error and fix the issue

---

### 5. Frontend Not Loading / 404 Errors

**Symptoms:**
```
Not found / Page not loading at http://localhost:5173
```

**Solutions:**

1. Verify frontend server is running:
   ```bash
   cd frontend
   node server.js
   ```

2. Check frontend files exist:
   - `frontend/index.html` ✓
   - `frontend/app.js` ✓
   - `frontend/styles.css` ✓

3. Verify port is correct:
   ```bash
   # Check if on port 5173
   netstat -ano | findstr :5173
   ```

4. Clear browser cache:
   - Ctrl+Shift+Delete (Chrome/Firefox)
   - Cmd+Shift+Delete (macOS)

5. Try different port:
   ```bash
   set PORT=3000 && node server.js
   ```

---

### 6. Database Connection Error

**Symptoms:**
```
java.sql.SQLException: No database is running
```

**If using H2 (Default):**
- Verify `./data/` directory exists in project root
- Check permissions to write in project directory
- Delete `./backend/target/` and restart

**If using MySQL:**
1. Verify MySQL is running
2. Check connection in `application.properties`:
   ```bash
   mysql -u root -p -e "SELECT 1;"
   ```
3. Verify database exists:
   ```sql
   CREATE DATABASE resumeiq;
   SHOW DATABASES;
   ```
4. Update credentials in `application.properties`

---

### 7. Node.js / npm Not Found

**Symptoms:**
```
'node' is not recognized / 'npm' is not found
```

**Solution:**
1. Install Node.js 16+: https://nodejs.org/
2. Verify installation:
   ```bash
   node --version
   npm --version
   ```
3. Restart terminal and try again

---

### 8. Permission Denied (macOS/Linux)

**Symptoms:**
```
Permission denied
```

**Solution:**
1. Make scripts executable:
   ```bash
   chmod +x setup.sh
   ```

2. Run with elevated permissions:
   ```bash
   sudo -E mvn spring-boot:run
   ```

3. Check write permissions:
   ```bash
   ls -la backend/src/main/resources/
   ```

---

### 9. Slow Build / "Downloading Dependencies"

**Cause:** First-time build or Maven needs to download libraries

**Solutions:**

1. Check internet connection (can take 5-10 minutes)

2. Configure Maven central repository in `~/.m2/settings.xml`:
   ```xml
   <mirrors>
       <mirror>
           <id>alimaven</id>
           <mirrorOf>central</mirrorOf>
           <name>Aliyun Maven</name>
           <url>https://maven.aliyun.com/repository/central</url>
       </mirror>
   </mirrors>
   ```

3. Increase Maven heap memory:
   ```bash
   set MAVEN_OPTS=-Xmx1024m
   mvn spring-boot:run
   ```

---

### 10. Cannot Connect Backend from Frontend

**Symptoms:**
- Backend API shows as unreachable
- CORS errors in browser console

**Solutions:**

1. Verify both services are running:
   - Backend: `http://localhost:8080/api`
   - Frontend: `http://localhost:5173`

2. Check API configuration in `frontend/app.js`:
   - Ensure API URL matches backend port (default: 8080)

3. Check Spring Boot CORS configuration in backend:
   - May need to add CORS headers if making cross-origin requests

4. Verify network connectivity:
   ```bash
   curl http://localhost:8080/api
   ```

---

## 🚀 Quick Reset

If everything is broken, try a complete reset:

```bash
# Backend
cd backend
mvn clean
rm -rf target/
rm -rf ./data/
mvn spring-boot:run

# Frontend (in new terminal)
cd frontend
rm -rf node_modules
node server.js
```

---

## 📞 Still Having Issues?

1. Check browser console for errors (F12)
2. Check terminal output for stack traces
3. Verify all prerequisites are installed
4. Review this guide for your specific error
5. Check `SETUP_GUIDE.md` for configuration help

---

## 🔧 Advanced Configuration

### Change Logging Level
Edit `backend/src/main/resources/application.properties`:
```properties
logging.level.root=WARN
logging.level.com.resumeiq=DEBUG
```

### Enable SQL Logging
```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

### Increase Upload File Size
```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

### Use Different Profile
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```
