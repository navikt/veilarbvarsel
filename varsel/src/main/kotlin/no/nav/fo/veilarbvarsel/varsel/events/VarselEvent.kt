package no.nav.fo.veilarbvarsel.varsel.events

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "event")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateVarselEvent::class, name = "CREATE"),
)
abstract class VarselEvent(
    val transactionId: UUID,
    val timestamp: LocalDateTime,
    val event: EventType
)
