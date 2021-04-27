package no.nav.fo.veilarbvarsel.domain.kafka

enum class UserType {
    BRUKER,
    VEILEDER,
    SYSTEM
}

open class User(
    open val type: UserType
)

data class Bruker(
    val norskIdent: String,
    val aktoerId: String
): User(UserType.BRUKER)

data class Veileder(
    val id: String
): User(UserType.VEILEDER)

data class System(
    val id: String
): User(UserType.SYSTEM)