@echo off
echo ================================
echo TodoList Application Setup
echo ================================
echo.

echo Step 1: Checking Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found! Please install JDK 17 or higher.
    echo Download from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
) else (
    echo ✓ Java is installed
)

echo.
echo Step 2: Checking Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven not found! Please install Maven 3.6 or higher.
    echo Download from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
) else (
    echo ✓ Maven is installed
)

echo.
echo Step 3: Checking MySQL...
mysql --version >nul 2>&1
if errorlevel 1 (
    echo WARNING: MySQL command line not found! 
    echo Please ensure MySQL Server 8.0+ is installed and running.
    echo Download from: https://dev.mysql.com/downloads/mysql/
    echo.
    set /p continue="Continue anyway? (y/n): "
    if not "%continue%"=="y" exit /b 1
) else (
    echo ✓ MySQL is available
)

echo.
echo Step 4: Building the application...
call mvn clean compile
if errorlevel 1 (
    echo ERROR: Build failed! Please check the error messages above.
    pause
    exit /b 1
) else (
    echo ✓ Application built successfully
)

echo.
echo Step 5: Creating database (if needed)...
echo Please ensure MySQL is running and you have root access.
echo Database 'todolist_db' will be created automatically by Spring Boot.

echo.
echo ================================
echo Setup Complete!
echo ================================
echo.
echo To run the application:
echo 1. Make sure MySQL Server is running
echo 2. Update application.properties with your MySQL password
echo 3. Run: mvn spring-boot:run
echo 4. Open browser to: http://localhost:8080
echo.
pause
