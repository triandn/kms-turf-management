# syntax=docker/dockerfile:1
FROM openjdk:oraclelinux8
# Working directory
WORKDIR /app
# Copy from your Host(PC, laptop) to container
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Run this inside the image
RUN ./mvnw dependency:go-offline
COPY src ./src

CMD ["./mvnw", "spring-boot:run"]