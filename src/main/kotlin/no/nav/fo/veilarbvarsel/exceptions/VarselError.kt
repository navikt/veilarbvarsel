package no.nav.fo.veilarbvarsel.exceptions

import java.lang.RuntimeException

abstract class VarselError(
    errorCode: Int,
    errorMessage: String
): RuntimeException("$errorCode: $errorMessage")