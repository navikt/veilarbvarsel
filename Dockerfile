FROM openjdk:14-slim

WORKDIR '/app'

COPY /build/libs/veilarbvarsel-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]