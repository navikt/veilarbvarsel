FROM openjdk:14-slim

WORKDIR '/app'

COPY /target/veilarbvarsel-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "-Xmx1024m"]
