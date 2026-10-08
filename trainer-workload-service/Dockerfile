FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY gym-crm-service/pom.xml gym-crm-service/pom.xml
COPY trainer-workload-service/pom.xml trainer-workload-service/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
RUN mvn -B -q -pl trainer-workload-service -am dependency:go-offline
COPY trainer-workload-service/src trainer-workload-service/src
RUN mvn -B -q -pl trainer-workload-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /workspace/trainer-workload-service/target/trainer-workload-service-*.jar app.jar
USER app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
