package no.nav.fo.veilarbvarsel.exceptions

class VarselCreationError(errorCode: Int, errorMessage: String):
    VarselError(errorCode, errorMessage)