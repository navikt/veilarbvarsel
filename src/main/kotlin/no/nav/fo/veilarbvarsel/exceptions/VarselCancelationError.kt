package no.nav.fo.veilarbvarsel.exceptions

class VarselCancelationError(errorCode: Int, errorMessage: String):
    VarselError(errorCode, errorMessage)