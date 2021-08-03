package no.nav.fo.veilarbvarsel.application.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class InternalController {

    @GetMapping("/isReady")
    fun isReady() {
    }

    @GetMapping("/isAlive")
    fun isAlive() {
    }

}
