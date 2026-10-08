FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY gym-crm-service/pom.xml gym-crm-service/pom.xml
COPY trainer-workload-service/pom.xml trainer-workload-service/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
RUN mvn -B -q -pl discovery-server -am dependency:go-offline
COPY discovery-server/src discovery-server/src
RUN mvn -B -q -pl discovery-server -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /workspace/discovery-server/target/discovery-server-*.jar app.jar
USER app
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
