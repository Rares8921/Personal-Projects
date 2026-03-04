FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY git-core-lib /app/git-core-lib
RUN mvn -f /app/git-core-lib/pom.xml clean install -DskipTests

# 2. Build la backend
COPY backend/pom.xml /app/backend/
COPY backend/src /app/backend/src

WORKDIR /app/backend
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/backend/target/backend-*.jar /app/backend.jar

RUN mkdir -p /git-storage
ENV GIT_STORAGE_PATH=/git-storage

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/backend.jar"]