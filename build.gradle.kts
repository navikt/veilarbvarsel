import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
}

group = "no.nav.fo.veilarbvarsel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

val ktor_version = "1.5.0"
val exposed_version = "0.17.13"
val coroutines_version = "1.4.3"

val postgresql_version = "42.2.18"
val logback_version = "1.2.3"
val kafka_version = "2.7.0"
val jackson_version = "2.12.3"

val junit_version = "5.6.0"

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {

    //Kotlin stuff
    implementation(group="org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutines_version, ext = "pom")

    // KTor stuff
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")

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
    implementation(group = "org.apache.kafka", name = "kafka_2.13", version = kafka_version)

    // Test
    testImplementation(group = "io.ktor", name = "ktor-service-test-host", version = ktor_version)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junit_version)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junit_version)
}