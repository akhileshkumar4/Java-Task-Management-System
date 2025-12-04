#!/bin/bash

echo "================================"
echo "TodoList Application Setup"
echo "================================"
echo

echo "Step 1: Checking Java..."
if command -v java &> /dev/null; then
    echo "✓ Java is installed"
    java -version
else
    echo "ERROR: Java not found! Please install JDK 17 or higher."
    echo "Download from: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

echo
echo "Step 2: Checking Maven..."
if command -v mvn &> /dev/null; then
    echo "✓ Maven is installed"
    mvn -version
else
    echo "ERROR: Maven not found! Please install Maven 3.6 or higher."
    echo "Download from: https://maven.apache.org/download.cgi"
    exit 1
fi

echo
echo "Step 3: Checking MySQL..."
if command -v mysql &> /dev/null; then
    echo "✓ MySQL is available"
    mysql --version
else
    echo "WARNING: MySQL command line not found!"
    echo "Please ensure MySQL Server 8.0+ is installed and running."
    echo "Download from: https://dev.mysql.com/downloads/mysql/"
    echo
    read -p "Continue anyway? (y/n): " continue
    if [[ $continue != "y" ]]; then
        exit 1
    fi
fi

echo
echo "Step 4: Building the application..."
if mvn clean compile; then
    echo "✓ Application built successfully"
else
    echo "ERROR: Build failed! Please check the error messages above."
    exit 1
fi

echo
echo "Step 5: Creating database (if needed)..."
echo "Please ensure MySQL is running and you have root access."
echo "Database 'todolist_db' will be created automatically by Spring Boot."

echo
echo "================================"
echo "Setup Complete!"
echo "================================"
echo
echo "To run the application:"
echo "1. Make sure MySQL Server is running"
echo "2. Update application.properties with your MySQL password"
echo "3. Run: mvn spring-boot:run"
echo "4. Open browser to: http://localhost:8080"
echo
