# Stage 1: Build the JAR
FROM maven:3.8.6-jdk-11-slim AS builder
COPY pom.xml .
COPY src/ src/
RUN mvn clean package

# Stage 2: Create the runtime image
FROM openjdk:11-jdk-slim-buster
COPY --from=builder target/*.jar app.jar
EXPOSE 9095
CMD ["java", "-jar", "app.jar"]