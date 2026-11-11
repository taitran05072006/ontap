FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .

RUN for attempt in 1 2 3 4 5; do \
      mvn -U -B -ntp dependency:go-offline && exit 0; \
      echo "Maven dependency download failed (attempt ${attempt}/5)"; \
      if [ "$attempt" -lt 5 ]; then sleep "$((attempt * 5))"; fi; \
    done; \
    exit 1

COPY src ./src

RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:25-jre

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

