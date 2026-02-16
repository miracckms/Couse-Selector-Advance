# ============================================
# Stage 1: Build Frontend (Vue.js)
# ============================================
FROM node:18-alpine AS frontend-build

WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci --production=false
COPY frontend/ ./
RUN npm run build

# ============================================
# Stage 2: Build Backend (Spring Boot)
# ============================================
FROM maven:3.9-eclipse-temurin-11 AS backend-build

WORKDIR /app

# Copy Maven files first (cache dependencies)
COPY backend/pom.xml backend/pom.xml
COPY backend/.mvn backend/.mvn
COPY backend/mvnw backend/mvnw
RUN cd backend && mvn dependency:go-offline -B

# Copy frontend build into Spring Boot static resources
COPY --from=frontend-build /app/frontend/dist/ backend/src/main/resources/static/

# Copy backend source and build
COPY backend/src backend/src
RUN cd backend && mvn package -DskipTests -B

# ============================================
# Stage 3: Runtime
# ============================================
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

# Copy the built JAR
COPY --from=backend-build /app/backend/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]
