package no.nav.fo.veilarbvarsel.varsel

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import java.util.*

enum class EventType {
    CREATE,
    CREATED,
    DONE
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "event")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateVarselVarselEvent::class, name = "CREATE"),
    JsonSubTypes.Type(value = VarselCreatedVarselEvent::class, name = "CREATED"),
    JsonSubTypes.Type(value = DoneVarselEvent::class, name = "DONE")
)
abstract class VarselEvent(
    val transactionId: UUID,
    val timestamp: LocalDateTime,
    val event: EventType
)

class CreateVarselVarselEvent @JsonCreator constructor(
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
) : VarselEvent(transactionId, timestamp, EventType.CREATE)

class VarselCreatedVarselEvent @JsonCreator constructor(
    transactionId: UUID,
    timestamp: LocalDateTime,
    val system: String,
    val id: String
) : VarselEvent(transactionId, timestamp, EventType.CREATED)

class DoneVarselEvent @JsonCreator constructor(
    transactionId: UUID,
    timestamp: LocalDateTime,
    val system: String,
    val id: String,
    val fodselsnummer: String,
    val groupId: String
) : VarselEvent(transactionId, timestamp, EventType.DONE)

