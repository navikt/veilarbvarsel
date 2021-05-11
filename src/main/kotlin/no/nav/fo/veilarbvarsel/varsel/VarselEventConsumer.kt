package no.nav.fo.veilarbvarsel.varsel

import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.config.kafka.KafkaConsumerWrapper
import org.slf4j.LoggerFactory

class VarselEventConsumer(
    env: KafkaEnvironment,
    systemUser: String,
    topics: List<String>,
    val service: VarselService
) : KafkaConsumerWrapper<String, VarselEvent>(env, systemUser, topics) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun handle(data: VarselEvent) {

        logger.info("[${data.transactionId}] [${data.event}]: $data")

        when (data) {
            is CreateVarselVarselEvent -> service.create(
                transactionId = data.transactionId,
                event = data
            )
            is DoneVarselEvent -> service.done(
                transactionId = data.transactionId,
                system = data.system,
                id = data.id,
                fodselsnummer = data.fodselsnummer,
                groupId = data.groupId
            )
            is VarselCreatedVarselEvent -> logger.info("Handling Varsel Created event $data")
        }

    }
}
