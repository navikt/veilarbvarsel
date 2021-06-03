FROM docker.pkg.github.com/navikt/pus-nais-java-app/pus-nais-java-app:java11

COPY /target/veilarbvarsel-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
