package no.nav.fo.veilarbvarsel.application.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class InternalController {

    @GetMapping("/isReady")
    fun isReady(){}

    @GetMapping("/isAlive")
    fun isAlive(){}
}
