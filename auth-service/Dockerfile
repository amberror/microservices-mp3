FROM maven:3.9.9-eclipse-temurin-17-alpine AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn -B -e org.apache.maven.plugins:maven-dependency-plugin:3.8.1:go-offline -DexcludeArtifactIds=domain

FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=dependencies /app /app
COPY /src /app/src
RUN mvn -B -e clean install -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine-3.21
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /app/target/*.jar auth-service.jar
EXPOSE 8085
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-jar", "auth-service.jar"]