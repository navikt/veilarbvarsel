package no.nav.fo.veilarbvarsel.server.exceptions

import java.lang.RuntimeException

class VarselDoesNotExistException: RuntimeException {
    constructor(message: String) : super(message)
}