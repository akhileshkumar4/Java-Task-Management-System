# ------------ Stage 1: Build the JAR -------------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy all source code & build project
COPY src ./src
RUN mvn -q package -DskipTests

# ------------ Stage 2: Run Application -------------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render & Docker Cloud port support
ENV PORT=8080
EXPOSE 8080

# Start app
ENTRYPOINT ["java", "-jar", "app.jar"]
