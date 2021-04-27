package no.nav.fo.veilarbvarsel.server

import no.nav.fo.veilarbvarsel.server.domain.Varsel
import no.nav.fo.veilarbvarsel.server.domain.VarselStatus
import no.nav.fo.veilarbvarsel.server.domain.dao.VarselDAO
import no.nav.fo.veilarbvarsel.server.domain.dao.VarselEventDAO
import no.nav.fo.veilarbvarsel.server.exceptions.VarselCannotBeCanceledException
import no.nav.fo.veilarbvarsel.server.exceptions.VarselDoesNotExistException
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime


interface VarselService {
    fun add(varsel: Varsel)

    fun cancel(id: String)

    fun getVarsel(id: String): Varsel
    fun getAll(): Set<Varsel>
}

class VarselServiceImpl : VarselService {

    override fun add(varsel: Varsel) {
        transaction {
            val id = VarselDAO.insertAndGetId {
                it[varselId] = varsel.varselId
                it[type] = varsel.type.name
                it[fodselsnummer] = varsel.fodselsnummer
                it[groupId] = varsel.groupId
                it[message] = varsel.message
                it[sikkerhetsnivaa] = varsel.sikkerhetsnivaa
                it[visibleUntil] = varsel.visibleUntil?.toDateTime()
            }

            VarselEventDAO.insert {
                it[varselId] = id
                it[event] = VarselStatus.RECEIVED.name
                it[timestamp] = DateTime.now().toDateTime()
            }
        }
    }

    override fun cancel(id: String) {
        val varsel = getVarsel(id)

        if(varsel.isCanceled()) {
            throw VarselCannotBeCanceledException("Varsel with id $id is already canceled")
        } else if (varsel.isSendingOrSent()) {
            throw VarselCannotBeCanceledException("Varsel with id $id is sending/sent and cannot be canceled")
        }

        transaction {
            VarselEventDAO.insert {
                it[varselId] = EntityID(varsel.id, VarselDAO)
                it[event] = VarselStatus.CANCELED.name
                it[timestamp] = DateTime.now().toDateTime()
            }
        }

    }

    override fun getAll(): Set<Varsel> {
        return VarselDAO.asVarsler(transaction {
            VarselDAO.innerJoin(VarselEventDAO).selectAll()
                .toList()
        }).toSet()
    }

    override fun getVarsel(id: String): Varsel {
        return VarselDAO.asVarsler(transaction {
            VarselDAO.innerJoin(VarselEventDAO).selectAll()
                .andWhere { VarselDAO.varselId eq id }
                .toList()
        }).firstOrNull() ?: throw VarselDoesNotExistException("Varsel with id $id does not exist")
    }
}