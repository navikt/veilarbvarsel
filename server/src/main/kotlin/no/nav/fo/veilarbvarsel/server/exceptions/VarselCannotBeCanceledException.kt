package no.nav.fo.veilarbvarsel.server.exceptions

import java.lang.RuntimeException

class VarselCannotBeCanceledException: RuntimeException {
    constructor(message: String) : super(message)
}