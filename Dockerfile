# ─────────────────────────────────────────
# Stage 1 — Build
# ─────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Cache dependency resolution separately from source
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

# ─────────────────────────────────────────
# Stage 2 — Runtime
# ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker \
    INTEGRATIONS_ENABLED=false

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]