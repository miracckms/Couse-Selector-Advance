#!/bin/sh

# Convert Render's DATABASE_URL to Spring Boot JDBC format
if [ -n "$DATABASE_URL" ]; then
  # Render provides: postgres://user:password@host:port/dbname
  # Spring needs separate: jdbc:postgresql://host:port/dbname + username + password

  # Extract user:password part
  USERINFO=$(echo "$DATABASE_URL" | sed 's|.*//\([^@]*\)@.*|\1|')
  DB_USER=$(echo "$USERINFO" | cut -d: -f1)
  DB_PASS=$(echo "$USERINFO" | cut -d: -f2)

  # Extract host:port/dbname part
  HOST_PART=$(echo "$DATABASE_URL" | sed 's|.*@||')

  export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOST_PART}"
  export SPRING_DATASOURCE_USERNAME="$DB_USER"
  export SPRING_DATASOURCE_PASSWORD="$DB_PASS"
fi

exec java -jar app.jar
