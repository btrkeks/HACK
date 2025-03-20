FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy gradle files for dependency caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon dependencies

# Copy src files
COPY src src

# Build the application
RUN ./gradlew --no-daemon build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy application jar
COPY --from=build /app/build/libs/*.jar app.jar

# Update the database URL to use the service name defined in docker-compose
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/hack

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]