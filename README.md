# Course Selector - Yeditepe University

A modern, full-stack web application for Yeditepe University students to plan academic schedules, calculate grades, and track course quotas.

## Features

- **Schedule Planner** - Auto/Manual mode schedule generation with conflict detection
- **Grade Calculator** - Per-course grade calculation with weighted components
- **Quota Tracker** - Real-time course quota monitoring via Yeditepe API
- **Weekly Schedule View** - Visual weekly timetable
- **User Preferences** - Persistent settings, tab customization, theme & language
- **Dark/Light Mode** - Premium UI with glassmorphism design
- **Authentication** - JWT + Refresh Token based auth system

## Tech Stack

| Layer    | Technology                          |
|----------|-------------------------------------|
| Frontend | Vue.js 3, Tailwind CSS, Vite        |
| Backend  | Spring Boot 2.7, Spring Security    |
| Database | MySQL 8                             |
| API      | RESTful + WebSocket                 |

## Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- MySQL 8+

### 1. Database Setup

```sql
CREATE DATABASE course_selector_db;
```

### 2. Backend Configuration

```bash
cp backend/src/main/resources/application.properties.example backend/src/main/resources/application.properties
```

Edit `application.properties` with your database credentials and API keys.

### 3. Run Backend

```bash
cd backend
./mvnw spring-boot:run
```

### 4. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173)

## Project Structure

```
├── backend/
│   ├── src/main/java/com/yeditepe/courseselector/
│   │   ├── config/          # Security, CORS, WebSocket config
│   │   ├── controller/      # REST API endpoints
│   │   ├── dto/             # Data transfer objects
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data access layer
│   │   └── service/         # Business logic
│   └── src/main/resources/
│       └── application.properties.example
├── frontend/
│   ├── src/
│   │   ├── api/             # API client (Axios)
│   │   ├── components/      # Vue components
│   │   ├── composables/     # Vue composables
│   │   ├── locales/         # i18n (TR/EN)
│   │   ├── App.vue          # Main application
│   │   └── AppWrapper.vue   # Auth wrapper
│   └── vite.config.js
└── database/
    ├── schema.sql
    └── mock_data.sql
```

## License

This project was developed as part of CSE344 Software Engineering course at Yeditepe University.
