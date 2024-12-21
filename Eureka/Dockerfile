# Base image with JDK 21
FROM openjdk:21-jdk-slim

# Set a working directory
WORKDIR /eureka

# Copy the application JAR file into the container
COPY target/*.jar eureka.jar

# Expose the application port (change this if your app uses a different port)
EXPOSE 8761

# Run the application
CMD ["java", "-jar", "eureka.jar"]
