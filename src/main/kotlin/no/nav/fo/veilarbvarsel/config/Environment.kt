package no.nav.fo.veilarbvarsel.config

data class Environment(
    val systemUser: String = getEnvVar("SYSTEM_USER", "VEILARBVARSEL"),

    val kafka: KafkaEnvironment = KafkaEnvironment(),
    val kafkaTopics: KafkaTopics = KafkaTopics(),
)

data class KafkaEnvironment(
    val bootstrapServers: String = getEnvVar("KAFKA_BROKERS_URL", "$host:$port"),
    val schemaRegistryUrl: String = getEnvVar("KAFKA_SCHEMAS_URL", "http://localhost:8081"),
)

data class KafkaTopics(
    val varselIncoming: String = getEnvVar("VARSEL_INCOMING_TOPIC", "privat-fo-varsel-q1"),
    val varselKvitteringOutgoing: String = getEnvVar("VARSEL_KVITTERING_OUTGOING_TOPIC", "privat-fo-varsel-kvittering-q1"),

    val doknotifikasjonBeskjed: String = getEnvVar(
        "KAFKA_DOKNOTIFIKASJON_BESKJED_TOPIC",
        "aapen-brukernotifikasjon-nyBeskjed-v1-testing"
    ),
    val doknotifikasjonOppgave: String = getEnvVar(
        "KAFKA_DOKNOTIFIKASJON_OPPGAVE_TOPIC",
        "aapen-brukernotifikasjon-nyOppgave-v1-testing"
    ),
    val doknotifikasjonDone: String = getEnvVar(
        "KAFKA_DOKNOTIFIKASJON_DONE_TOPIC",
        "aapen-brukernotifikasjon-nyDone-v1-testing"
    )
)

fun getEnvVar(varName: String, default: String?): String {
    return System.getenv(varName)
        ?: default
        ?: throw IllegalArgumentException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")


}
