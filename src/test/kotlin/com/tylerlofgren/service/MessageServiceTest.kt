package com.tylerlofgren.service

import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.Message
import com.tylerlofgren.domain.QueryResult
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class MessageServiceTest : StringSpec({

    val validMessageOne = Message(1, 10, "aaa", 10, 10)
    val validMessageTwo = Message(2, 20, "bbb", 20, 20)
    val validMessageThree = Message(3, 40, "ccc", 30, 30)
    val validMessageFour = Message(3, 50, "ddd", 31, 40)

    lateinit var service: MessageService

    beforeTest {
        service = MessageService()
    }

    "queryMessages" {
        forAll(
                row(QueryType.MAX_TIME_GAP, listOf(), 0L),
                row(QueryType.MAX_TIME_GAP, listOf(validMessageOne), 0),
                row(QueryType.MAX_TIME_GAP, listOf(validMessageOne, validMessageThree, validMessageTwo, validMessageFour), 20),
                row(QueryType.TOTAL_VOLUME, listOf(), 0L),
                row(QueryType.TOTAL_VOLUME, listOf(validMessageOne), validMessageOne.volume.toLong()),
                row(QueryType.TOTAL_VOLUME, listOf(validMessageOne, validMessageTwo, validMessageThree), 60L),
                row(QueryType.MAX_TEMPERATURE, listOf(), 0L),
                row(QueryType.MAX_TEMPERATURE, listOf(validMessageOne), validMessageOne.temperature.toLong()),
                row(QueryType.MAX_TEMPERATURE, listOf(validMessageOne, validMessageTwo, validMessageThree, validMessageFour), 40L),
                row(QueryType.WEIGHTED_AVERAGE_TEMPERATURE, listOf(), 0L),
                row(QueryType.WEIGHTED_AVERAGE_TEMPERATURE, listOf(validMessageOne), 10L),
                row(QueryType.WEIGHTED_AVERAGE_TEMPERATURE, listOf(validMessageOne, validMessageTwo, validMessageThree, validMessageFour), 29L)
        ) { queryType: QueryType, messages: List<Message>, expected: Long ->
            val result = service.queryMessages(queryType, messages)
            result shouldBe QueryResult(expected)
        }
    }
})
