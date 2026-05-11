FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
ENV MAVEN_OPTS="-Daether.connector.http.retryHandler.count=10 -Daether.connector.http.connectionMaxTtl=300 -Daether.connector.requestTimeout=120000 -Daether.connector.connectTimeout=60000"
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2/repository \
    n=0; until [ $n -ge 5 ]; do \
      mvn -B dependency:go-offline && exit 0; \
      n=$((n+1)); echo "mvn go-offline attempt $n failed, retrying..."; sleep 5; \
    done; exit 1
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    n=0; until [ $n -ge 3 ]; do \
      mvn -B -DskipTests package && exit 0; \
      n=$((n+1)); echo "mvn package attempt $n failed, retrying..."; sleep 5; \
    done; exit 1

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/notification-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
