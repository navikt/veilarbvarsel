package no.nav.fo.veilarbvarsel

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.fo.veilarbvarsel.db.DB
import no.nav.fo.veilarbvarsel.db.TestSchema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port, module = Application::mainModule)
    server.start();
}

fun Application.mainModule() {
    DB.connect()

    transaction {
        SchemaUtils.create(TestSchema)
    }

    transaction {
        insert("Knuttt", null)
    }

    println("Test")
}

fun insert(name: String, age: Int?): Int {
    val id = transaction {
        TestSchema.insertAndGetId { t ->
            t[TestSchema.name] = name
            if (age != null) {
                t[TestSchema.age] = age
            }
        }
    }

    return id.value
}