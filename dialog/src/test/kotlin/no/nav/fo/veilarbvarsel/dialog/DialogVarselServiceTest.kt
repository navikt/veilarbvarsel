package no.nav.fo.veilarbvarsel.dialog

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.fo.veilarbvarsel.dialog.domain.dao.MeldingDAO
import no.nav.fo.veilarbvarsel.domain.kafka.Bruker
import no.nav.fo.veilarbvarsel.kafka.BeskjedProducerImpl
import no.nav.fo.veilarbvarsel.kafka.KafkaMessageProducer
import no.nav.fo.veilarbvarsel.kafka.KafkaProducerImpl
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

/**
 * For running these tests the docker-compose file should be running on your computer
 */
internal class DialogVarselServiceTest {

    private lateinit var service: DialogVarselService

    private val realFnrOne = "10108003989"

    @BeforeEach
    internal fun setUp() {
        //DB.connect()

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(MeldingDAO)
        }

        DialogVarselConfiguration.initDatabase()

        val kafkaProducer: KafkaMessageProducer<Nokkel, Beskjed> = KafkaProducerImpl("localhost", 9092)
        val beskjedProducer = BeskjedProducerImpl(kafkaProducer)
        service = DialogVarselServiceImpl(beskjedProducer)
    }

    @Test
    @DisplayName("Adding a message then getNotVarslet should return message")
    internal fun addingAMessageShouldStoreToDatabaseAndBeReceivedFromGetMessages() {
        val id = UUID.randomUUID()
        val subject = Bruker("01012112345", "1")

        service.addMessage(id, subject)
        val messages = service.getNotVarsletAndNotRead()

        assertEquals(1, messages.size)
        assertEquals(id, messages.get(0).messageId)
        assertEquals(subject.norskIdent, messages.get(0).receiverNorskIdent)
    }

    @Test
    @DisplayName("Adding a message, then reading it should not make it retun in getNotVarsletAndNotRead")
    internal fun addingMessageThenReadThenGetMessagesShouldNotReturnMessage() {
        val id = UUID.randomUUID()
        val subject = Bruker("01012112345", "1")

        service.addMessage(id, subject)
        service.messagesRead(subject)

        val messages = service.getNotVarsletAndNotRead()

        assertEquals(0, messages.size)
    }

    @Test
    @DisplayName("Adding two messages from same user then read should set both messages to read")
    internal fun addingTwoMessagesFromSameUserThenReadShouldReadBothMessages() {
        val  subject = Bruker("1", "1")
        val idOne = UUID.randomUUID()
        val idTwo = UUID.randomUUID()

        service.addMessage(idOne, subject)
        service.addMessage(idTwo, subject)

        assertEquals(2, service.getNotVarsletAndNotRead().size, "Should be two unread messages")

        service.messagesRead(subject)
        assertEquals(0, service.getNotVarsletAndNotRead().size, "All messages should be read")
    }

    @Test
    @DisplayName("Adding two messages from different users then one reads should return one message")
    internal fun addingTwoMessagedThenReadingOneShouldReturnOne() {
        val idOne = UUID.randomUUID()
        val subjectOne = Bruker("1", "1")

        val idTwo = UUID.randomUUID()
        val subjectTwo = Bruker("2", "2")

        service.addMessage(idOne, subjectOne)
        service.addMessage(idTwo, subjectTwo)

        val messagesPreRead = service.getNotVarsletAndNotRead()
        assertEquals(2, messagesPreRead.size)

        service.messagesRead(subjectOne)
        val messagesPostOneRead = service.getNotVarsletAndNotRead()

        assertEquals(1, messagesPostOneRead.size)
        assertEquals(idTwo, messagesPostOneRead.get(0).messageId)

        service.messagesRead(subjectTwo)

        assertEquals(0, service.getNotVarsletAndNotRead().size)
    }

    @Test
    @DisplayName("Basic test of sending of varsler")
    internal fun sendVarsler() {
        val messageId = UUID.randomUUID()
        val subject = Bruker(realFnrOne, "1")

        service.addMessage(messageId, subject)
        service.sendVarsler()
        Thread.sleep(2000)

        service.getAll()
        println()

    }
}