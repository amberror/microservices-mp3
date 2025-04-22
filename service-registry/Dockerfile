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
COPY --from=builder /app/target/*.jar service-registry.jar
EXPOSE 8761
CMD ["java", "-jar", "service-registry.jar"]