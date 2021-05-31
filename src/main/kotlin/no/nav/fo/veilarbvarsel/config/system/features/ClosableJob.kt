package no.nav.fo.veilarbvarsel.config.system.features

import java.io.Closeable

interface ClosableJob : Closeable, Runnable
