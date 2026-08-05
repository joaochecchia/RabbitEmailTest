FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre

LABEL maintainer="jjgabriel10@outlook.com"

WORKDIR /app

COPY --from=build /app/target/*.jar /app/backend-docker.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/backend-docker.jar"]
