# Base image with JDK 21
FROM openjdk:21-jdk-slim

# Set a working directory
WORKDIR /config-server

#COPY ../config-properties/* config-files
# Copy the application JAR file into the container
COPY target/*.jar config-server.jar

# Expose the application port (change this if your app uses a different port)
EXPOSE 8888

# Run the application
CMD ["java", "-jar", "config-server.jar"]
