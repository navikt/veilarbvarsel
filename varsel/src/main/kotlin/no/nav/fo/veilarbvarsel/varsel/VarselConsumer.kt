package no.nav.fo.veilarbvarsel.varsel

import no.nav.fo.veilarbvarsel.core.ports.BrukernotifikasjonService
import no.nav.fo.veilarbvarsel.core.utils.Callback
import no.nav.fo.veilarbvarsel.varsel.events.CreateVarselEvent
import no.nav.fo.veilarbvarsel.varsel.events.VarselEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class VarselConsumer(
    val brukernotifikasjonService: BrukernotifikasjonService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["\${topics.dab.varsel}"], groupId = "veilarbvarsel")
    fun receive(event: VarselEvent) {
        when (event) {
            is CreateVarselEvent -> brukernotifikasjonService.sendVarsel(event.toVarsel(), object : Callback {
                override fun onSuccess() {
                    logger.info("Successfully sent create varsel event to Brukernotifikasjon")
                }

                override fun onFailure(exception: Throwable) {
                    logger.error("Create varsel failed while sending event to brukernotifikasjon", exception)
                }
            })
        }
    }

}
