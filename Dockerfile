FROM eclipse-temurin:21-jre
WORKDIR /app

COPY target/backend-0.0.1-SNAPSHOT.jar moneymap-v1.0.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","moneymap-v1.0.jar"]
