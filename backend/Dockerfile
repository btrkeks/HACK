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
COPY --from=build /app/build/libs/exambyte-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
# Use default port for local, but allow Heroku to override
CMD ["sh", "-c", "java -jar app.jar"]
