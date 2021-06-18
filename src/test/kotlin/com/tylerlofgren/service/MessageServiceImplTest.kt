package com.tylerlofgren.service

import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.*
import com.tylerlofgren.repo.MessageRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hibernate.HibernateException

class MessageServiceImplTest : StringSpec({
    lateinit var service: MessageServiceImpl
    lateinit var messageRepository: MessageRepository

    beforeTest {
        messageRepository = mockk(relaxed = true)
        service = MessageServiceImpl(messageRepository)
    }

    "queryMessages" {
        forAll(
                row(QueryType.MAX_TIME_GAP, listOf(), 0L),
                row(QueryType.MAX_TIME_GAP, listOf(validMessageOne), 0),
                row(QueryType.MAX_TIME_GAP, listOf(validMessageOne, validMessageThree, validMessageTwo, validMessageFour), 20),
                row(QueryType.TOTAL_VOLUME, listOf(), 0L),
                row(QueryType.TOTAL_VOLUME, listOf(validMessageOne), validMessageOne.volume!!.toLong()),
                row(QueryType.TOTAL_VOLUME, listOf(validMessageOne, validMessageTwo, validMessageThree), 60L),
                row(QueryType.MAX_TEMPERATURE, listOf(), 0L),
                row(QueryType.MAX_TEMPERATURE, listOf(validMessageOne), validMessageOne.temperature!!.toLong()),
                row(QueryType.MAX_TEMPERATURE, listOf(validMessageOne, validMessageTwo, validMessageThree, validMessageFour), 40L),
                row(QueryType.WEIGHTED_AVERAGE_TEMPERATURE, listOf(), 0L),
                row(QueryType.WEIGHTED_AVERAGE_TEMPERATURE, listOf(validMessageOne), 10L),
                row(QueryType.WEIGHTED_AVERAGE_TEMPERATURE, listOf(validMessageOne, validMessageTwo, validMessageThree, validMessageFour), 29L)
        ) { queryType: QueryType, messages: List<Message>, expected: Long ->
            every { messageRepository.findBySymbol(symbolOne) } returns messages
            val result = service.queryMessages(queryType, symbolOne)
            result shouldBe QueryResult(expected)
        }
    }

    "saveMessage - success" {
        every { messageRepository.save(any()) } returns validMessageOne
        val result = shouldNotThrowAny { service.saveMessage(validMessageOne) }
        result shouldBe validMessageOne
        verify(exactly = 1) { messageRepository.save(any()) }
    }

    "saveMessage - repo throws exception and it bubbles up" {
        val expected = HibernateException("test")
        every { messageRepository.save(any())} throws expected
        val result = shouldThrow<HibernateException> { service.saveMessage(validMessageOne) }
        result shouldBe expected
    }
})
