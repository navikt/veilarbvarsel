package no.nav.fo.veilarbvarsel.exceptions

class VarselInternalServerError(errorCode: Int, errorMessage: String):
    VarselError(errorCode, errorMessage)