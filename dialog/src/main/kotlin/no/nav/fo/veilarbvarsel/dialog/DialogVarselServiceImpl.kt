package no.nav.fo.veilarbvarsel.dialog

import no.nav.fo.veilarbvarsel.dialog.domain.Melding
import no.nav.fo.veilarbvarsel.dialog.domain.VarselStatus
import no.nav.fo.veilarbvarsel.dialog.domain.dao.MeldingDAO
import no.nav.fo.veilarbvarsel.dialog.domain.dao.MeldingDAO.asMelding
import no.nav.fo.veilarbvarsel.domain.kafka.Bruker
import no.nav.fo.veilarbvarsel.kafka.BeskjedProducer
import no.nav.fo.veilarbvarsel.kafka.domain.BeskjedRecord
import no.nav.fo.veilarbvarsel.kafka.domain.VarselCallback
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.net.URL
import java.time.LocalDateTime
import java.util.*

interface DialogVarselService {
    fun addMessage(id: UUID, subject: Bruker)
    fun messagesRead(subject: Bruker)
    fun getNotVarsletAndNotRead(): List<Melding>
    fun sendVarsler()
    fun getAll(): List<Melding>
}

class DialogVarselServiceImpl(
    var beskjedProducer: BeskjedProducer
) : DialogVarselService {

    override fun addMessage(id: UUID, subject: Bruker) {
        transaction {
            MeldingDAO.insert { melding ->
                melding[messageId] = id
                melding[recieverNorskIdent] = subject.norskIdent
                melding[varselStatus] = VarselStatus.RECEIVED.name
            }
        }
    }

    override fun messagesRead(subject: Bruker) {
        transaction {
            MeldingDAO.update({
                (MeldingDAO.recieverNorskIdent eq subject.norskIdent)
                    .and(MeldingDAO.read.isNull())
            }) {
                it[varselStatus] = VarselStatus.CANCELED.name
                it[read] = DateTime.now()
            }
        }
    }

    override fun getNotVarsletAndNotRead(): List<Melding> {
        return transaction {
            MeldingDAO.selectAll()
                .andWhere { MeldingDAO.varselStatus eq VarselStatus.RECEIVED.name }
                .andWhere { MeldingDAO.read.isNull() }
                .andWhere { MeldingDAO.sending.isNull() }
                .andWhere { MeldingDAO.sent.isNull() }
                .map { it.asMelding() }
        }
    }

    override fun sendVarsler() {
        val meldingerByUser = getNotVarsletAndNotRead().associateBy { it.receiverNorskIdent }

        meldingerByUser.entries.forEach { pair ->

            val record = BeskjedRecord(
                pair.key,
                "VARSEL",
                "!TBD! Du har en ny beskjed i Dialogverktøyet",
                URL("http://localhost.com/should-be-something-else"),
                4,
                LocalDateTime.now().plusDays(14)
            )

            beskjedProducer.send(record, object : VarselCallback {

                override fun onSuccess(id: UUID) {
                    transaction {
                        MeldingDAO.update ({
                            (MeldingDAO.recieverNorskIdent eq pair.key)
                                .and(MeldingDAO.sending.isNull())
                                .and(MeldingDAO.sent.isNull())
                        }) {
                            it[sending] = DateTime.now()
                            it[varselStatus] = VarselStatus.SENDING.name
                        }
                    }
                }

            })
        }
    }

    override fun getAll(): List<Melding> {
        return transaction {
            MeldingDAO.selectAll()
                .map { it.asMelding() }
        }
    }
}