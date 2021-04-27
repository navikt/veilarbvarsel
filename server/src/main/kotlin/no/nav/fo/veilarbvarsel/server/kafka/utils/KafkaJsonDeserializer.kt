package com.evensberget.trader.common.kafka.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Deserializer

class KafkaJsonDeserializer<V> : Deserializer<V> {

    val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        registerModule(KotlinModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    override fun deserialize(string: String?, bytes: ByteArray): V {
        val data = objectMapper.readValue(bytes, object: TypeReference<V>(){})

        return data
    }

    inline fun<reified V> getJson(bytes: ByteArray?): V {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        return objectMapper.readValue(bytes, V::class.java)
    }

    inline fun<reified V> ByteArray.toObject(): V {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        return objectMapper.readValue(this, V::class.java)
    }

}