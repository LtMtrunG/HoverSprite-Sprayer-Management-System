FROM jelastic/maven:3.9.5-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
COPY --from=build /target/hoversprite-0.0.1-SNAPSHOT.jar hoversprite.jar
EXPOSE 443
ENTRYPOINT ["java", "-jar", "hoversprite.jar"]