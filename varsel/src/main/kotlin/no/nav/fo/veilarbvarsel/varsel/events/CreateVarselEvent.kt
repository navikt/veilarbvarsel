package no.nav.fo.veilarbvarsel.varsel.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.fo.veilarbvarsel.core.domain.Varsel
import no.nav.fo.veilarbvarsel.core.domain.VarselType
import java.net.URL
import java.time.LocalDateTime
import java.util.*

class CreateVarselEvent @JsonCreator constructor(
    @JsonProperty("transactionId") transactionId: UUID,
    @JsonProperty("timestamp") timestamp: LocalDateTime,
    @JsonProperty("system") val system: String,
    @JsonProperty("id") val id: String,
    @JsonProperty("type") val type: VarselType,
    @JsonProperty("fodselsnummer") val fodselsnummer: String,
    @JsonProperty("groupId") val groupId: String,
    @JsonProperty("message") val message: String,
    @JsonProperty("link") val link: String,
    @JsonProperty("sikkerhetsnivaa") val sikkerhetsnivaa: Int,
    @JsonProperty("visibleUntil") val visibleUntil: LocalDateTime? = null,
    @JsonProperty("externalVarsling") val externalVarsling: Boolean
) : VarselEvent(
    transactionId = transactionId,
    timestamp = timestamp,
    event = EventType.CREATE
) {

    fun toVarsel(): Varsel {
        return Varsel(
            system,
            id,
            type,
            fodselsnummer,
            groupId,
            URL(link),
            message,
            sikkerhetsnivaa,
            visibleUntil,
            externalVarsling
        )
    }

}
