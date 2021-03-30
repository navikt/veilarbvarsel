package no.nav.fo.veilarbvarsel.mq

import com.ibm.msg.client.jms.JmsFactoryFactory
import com.ibm.msg.client.wmq.WMQConstants
import javax.jms.ConnectionFactory

object MQConfiguration {

    val applicationName: String
    val namespace: String
    val mqGatewayHostname: String
    val mqGatewayPort: String
    val mqGatewayName: String
    val mqUserId: String

    val mqChannel: String

    init {
        applicationName = "veilarbvarsel"
        namespace = "local"
        mqGatewayHostname = "localhost"
        mqGatewayPort = "1414"
        mqGatewayName = "QM1"
        mqUserId = "user"

        mqChannel = "DEV.APP.SVRCONN"
    }


    fun connectionFactory(): ConnectionFactory {
        val connectionFactory = JmsFactoryFactory
            .getInstance(WMQConstants.WMQ_PROVIDER)
            .createConnectionFactory()

        connectionFactory.setStringProperty(WMQConstants.WMQ_HOST_NAME, mqGatewayHostname)
        connectionFactory.setStringProperty(WMQConstants.WMQ_PORT, mqGatewayPort)
        connectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, mqChannel)
        connectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT)
        connectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, mqGatewayName)
        connectionFactory.setStringProperty(WMQConstants.USERID, mqUserId)

        return connectionFactory
    }

}