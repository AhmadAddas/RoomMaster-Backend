# Use an official Maven image to build the app
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Second stage: build the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/target/*.jar app.jar

# Copy the .env file
COPY src/main/resources/.env .env

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
