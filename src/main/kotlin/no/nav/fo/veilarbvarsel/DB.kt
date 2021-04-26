package no.nav.fo.veilarbvarsel

import no.nav.fo.veilarbvarsel.domain.VarselDAO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

object DB {
    private val host: String
    private val port: Int
    private val dbName: String
    private val dbUser: String
    private val dbPassword: String

    init {
        val dbUrl = System.getenv("DATABASE_URL")
        if (dbUrl != null) {
            val dbUri = URI(dbUrl)
            host = dbUri.host
            port = dbUri.port
            dbName = dbUri.path.substring(1)

            val userInfo = dbUri.userInfo.split(":")
            dbUser = userInfo[0]
            dbPassword = userInfo[1]
        } else {
            host = System.getenv("DB_HOST") ?: "localhost"
            port = System.getenv("DB_PORT")?.toInt() ?: 5100
            dbName = System.getenv("DB_NAME") ?: "veilarbvarsel"
            dbUser = System.getenv("DB_USER") ?: "postgres"
            dbPassword = System.getenv("DB_PASSWORD") ?: "password"
        }
    }

    fun setupSchemas() {
        transaction {
            //FIXME Remove the drop
            SchemaUtils.drop(VarselDAO)
            SchemaUtils.create(VarselDAO)
        }
    }

    fun connect(): Database {
        return Database.connect(
            "jdbc:postgresql://$host:$port/$dbName",
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
    }
}