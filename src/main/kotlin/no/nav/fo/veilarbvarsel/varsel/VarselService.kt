package no.nav.fo.veilarbvarsel.varsel

import no.nav.fo.veilarbvarsel.domain.Varsel
import no.nav.fo.veilarbvarsel.domain.VarselDAO
import no.nav.fo.veilarbvarsel.exceptions.VarselCancelationError
import no.nav.fo.veilarbvarsel.exceptions.VarselCreationError
import no.nav.fo.veilarbvarsel.exceptions.VarselInternalServerError
import no.nav.fo.veilarbvarsel.domain.VarselDAO.asVarsel
import no.nav.fo.veilarbvarsel.domain.VarselDAO.status
import no.nav.fo.veilarbvarsel.domain.VarselStatus
import no.nav.fo.veilarbvarsel.domain.VarselType
import no.nav.fo.veilarbvarsel.domain.events.CreateVarselPayload
import no.nav.fo.veilarbvarsel.domain.events.InternalEvent
import no.nav.fo.veilarbvarsel.kafka.KafkaInternalConsumer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

interface VarselService {
    fun add(
        varselId: String,
        type: VarselType,
        fodselsnummer: String,
        groupId: String,
        message: String,
        sikkerhetsnivaa: Int,
        visibleUntil: LocalDateTime?
    )

    fun cancel(varselId: String)
    fun sending(varselId: String)
    fun sent(varselId: String)

    fun getVarsel(varselId: String): Varsel?
    fun getAllNotSendingOrCanceled(olderThan: LocalDateTime): Set<Varsel>
    fun getAll(): Set<Varsel>
}

class VarselServiceImpl : VarselService {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun add(
        varselId: String,
        type: VarselType,
        fodselsnummer: String,
        groupId: String,
        message: String,
        sikkerhetsnivaa: Int,
        visibleUntil: LocalDateTime?
    ) {

        if (getVarsel(varselId) != null) {
            throw VarselCreationError(400, "Varsel with id $varselId already exist.")
        }

        transaction {
            VarselDAO.insert {
                it[VarselDAO.varselId] = varselId
                it[VarselDAO.type] = type.name
                it[VarselDAO.fodselsnummer] = fodselsnummer
                it[VarselDAO.groupId] = groupId
                it[VarselDAO.message] = message
                it[VarselDAO.sikkerhetsnivaa] = sikkerhetsnivaa
                it[VarselDAO.visibleUntil] = visibleUntil?.toDateTime()
                it[status] = VarselStatus.RECEIVED.name
                it[received] = LocalDateTime.now().toDateTime()
            }
        }

        logger.info("Added varsel with id $varselId")
    }

    override fun cancel(varselId: String) {
        val varsel = getVarsel(varselId) ?: throw VarselCancelationError(
            400,
            "Cannot cancel varsel with id $varselId as it does not exist"
        )

        if (varsel.sending != null) throw VarselCancelationError(
            400,
            "Varsel with id $varselId is already sent and cannot be canceled"
        )
        if (varsel.sent != null) throw VarselCancelationError(
            400,
            "Varsel with id $varselId is already sent and cannot be canceled"
        )

        transaction {
            VarselDAO.update({ VarselDAO.id eq varsel.id }) {
                it[status] = VarselStatus.CANCELED.name
                it[canceled] = LocalDateTime.now().toDateTime()
            }
        }
    }

    override fun sending(varselId: String) {
        val varsel = getVarsel(varselId) ?: throw VarselInternalServerError(
            500,
            "Cannot set varsel with id $varselId to sending as it does not exist"
        )

        if (varsel.sending != null) throw VarselInternalServerError(500, "Varsel with id $varselId is already sending")
        if (varsel.sent != null) throw VarselInternalServerError(500, "Varsel with id $varselId is already sent")
        if (varsel.canceled != null) throw VarselInternalServerError(
            500,
            "Cannot set a canceled varsel to sending, varselId: $varselId"
        )

        transaction {
            VarselDAO.update({ VarselDAO.id eq varsel.id }) {
                it[status] = VarselStatus.SENDING.name
                it[sending] = LocalDateTime.now().toDateTime()
            }
        }
    }

    override fun sent(varselId: String) {
        val varsel = getVarsel(varselId) ?: throw VarselInternalServerError(
            500,
            "Cannot set varsel with id $varselId to sent as it does not exist"
        )

        if (varsel.sending == null) throw VarselInternalServerError(
            500,
            "Sending should be set before setting Sent on varsel with id $varselId"
        )
        if (varsel.sent != null) throw VarselInternalServerError(500, "Varsel with id $varselId is already sent")
        if (varsel.canceled != null) throw VarselInternalServerError(
            500,
            "Cannot set a canceled varsel to sent, varselId: $varselId"
        )

        transaction {
            VarselDAO.update({ VarselDAO.id eq varsel.id }) {
                it[status] = VarselStatus.SENT.name
                it[sent] = LocalDateTime.now().toDateTime()
            }
        }
    }

    override fun getVarsel(varselId: String): Varsel? {
        return transaction {
            VarselDAO.select { VarselDAO.varselId eq varselId }
                .toList()
        }.firstOrNull()?.asVarsel()
    }

    override fun getAllNotSendingOrCanceled(olderThan: LocalDateTime): Set<Varsel> {
        return transaction {
            VarselDAO.select { (status eq VarselStatus.RECEIVED.name) }.toSet().stream()
                .map { it.asVarsel() }
                .filter { it.received.isBefore(olderThan) }
                .collect(Collectors.toSet())
        }
    }

    override fun getAll(): Set<Varsel> {
        return transaction {
            VarselDAO.selectAll().toSet().stream()
                .map { it.asVarsel() }
                .collect(Collectors.toSet())
        }
    }
}