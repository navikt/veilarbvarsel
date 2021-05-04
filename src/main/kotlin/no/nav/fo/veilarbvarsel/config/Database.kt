package no.nav.fo.veilarbvarsel.config

import org.jetbrains.exposed.sql.Database

class PostgresDatabase(env: DatabaseEnvironment) {

    companion object {
        private lateinit var database: Database
    }

    init {
        database = Database.connect(
                url = "jdbc:postgresql://${env.host}:${env.port}/${env.name}",
                driver = "org.postgresql.Driver",
                user = env.user,
                password = env.password
        )

        setupSchemas()
    }

    //FIXME Remove the drop
    private fun setupSchemas() {
/*
        transaction {
            SchemaUtils.drop(VarselDao)
            SchemaUtils.create(VarselDao)
        }
*/
    }

    fun get(): Database {
        return database
    }
}