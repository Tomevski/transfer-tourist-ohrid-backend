# syntax=docker/dockerfile:1

# ---- Build stage: compile and package the executable jar ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
# Resolve dependencies first so this layer is cached unless pom.xml changes.
COPY pom.xml .
RUN mvn -B dependency:go-offline
# Then build. Tests run in CI (not in the image build) to keep the image fast.
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Runtime stage: slim JRE + the jar, running as a non-root user ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --system spring && useradd --system --gid spring spring
COPY --from=build /build/target/*.jar app.jar
USER spring
EXPOSE 8080
# Profile, DB and SMTP come from the environment (see application-prod.yml).
ENTRYPOINT ["java", "-jar", "app.jar"]
