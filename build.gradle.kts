import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
}

group = "no.nav.fo.veilarbvarsel"
version = "1.0-SNAPSHOT"

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "no.nav.fo.veilarbvarsel.MainKt"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .filter { !it.name.equals("LICENSE") }
            .map { zipTree(it) }
    })
}


repositories {
    mavenCentral()
    jcenter()
    maven("https://packages.confluent.io/maven/")
    maven("https://jitpack.io")
}

val ktor_version = "1.5.4"
val prometheusVersion = "1.6.6"
val exposed_version = "0.17.13"
val coroutines_version = "1.4.3"
val navCommonVersion = "2.2021.02.23_11.33-7091f10a35ba"
val postgresql_version = "42.2.18"
val logback_version = "1.2.3"
val kafka_version = "2.7.0"
val avro_version = "1.8.2"
val confluent_version = "5.0.2"
val jackson_version = "2.12.3"

val junit_version = "5.6.0"

val brukernotifikasjon_version = "1.2021.01.18-11.12-b9c8c40b98d1"
val doknotifikasjonVersion = "1.2020.11.16-09.27-d037b30bb0ea"

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {

    //Kotlin stuff
    implementation(group="org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutines_version, ext = "pom")

    // KTor stuff
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor", "ktor-jackson", ktor_version)
    implementation("io.ktor", "ktor-metrics-micrometer", ktor_version)
    implementation("io.micrometer", "micrometer-registry-prometheus", prometheusVersion)

    // Database stuff
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("org.jetbrains.exposed:exposed:$exposed_version")

    // Jackson
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jackson_version)
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jackson_version)
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = jackson_version)
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-joda", version = jackson_version)

    // Misc
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("no.nav.common", "log", navCommonVersion)

    // Kafka
    implementation(group = "org.apache.kafka", name = "kafka_2.13", version = kafka_version)

    implementation(group = "com.github.navikt", name = "brukernotifikasjon-schemas", version = brukernotifikasjon_version)
    implementation("com.github.navikt", "doknotifikasjon-schemas", doknotifikasjonVersion)
    implementation("org.apache.avro", "avro", avro_version)
    implementation("io.confluent", "kafka-streams-avro-serde", confluent_version)

    // Test
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junit_version)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junit_version)
}