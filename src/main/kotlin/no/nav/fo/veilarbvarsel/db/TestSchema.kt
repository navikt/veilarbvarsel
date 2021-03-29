package no.nav.fo.veilarbvarsel.db

import org.jetbrains.exposed.dao.IntIdTable

object TestSchema: IntIdTable() {
    val name = varchar("name", 20).uniqueIndex()
    val age = integer("age").default(0)

}