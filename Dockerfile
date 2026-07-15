# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml to fetch dependencies and speed up incremental builds
COPY pom.xml .
RUN MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED" mvn dependency:go-offline -B

# Copy source directory and compile the app package
COPY src ./src
RUN MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED" mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy executable fat jar from build stage
COPY --from=build /app/target/leagues-mono-0.0.1-SNAPSHOT.jar app.jar

# Expose server port
EXPOSE 8080

# Execute with required open reflections access flags
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens", "java.base/java.util=ALL-UNNAMED", "-jar", "app.jar"]
