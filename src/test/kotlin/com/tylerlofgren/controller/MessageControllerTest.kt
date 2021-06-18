package com.tylerlofgren.controller

import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.*
import com.tylerlofgren.service.MessageService
import com.tylerlofgren.service.MessageServiceImpl
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.kotest.MicronautKotestExtension.getMock
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import org.hibernate.HibernateException
import javax.management.Query


@MicronautTest
class MessageControllerTest(
    private val messageService: MessageService,
    @Client("/") private val client: RxHttpClient
) : StringSpec({

    "GET /messages - HTTP 200" {
        val expected = QueryResult(1)
        val mockService = getMock(messageService)

        forAll(
            row(QueryType.MAX_TIME_GAP.name),
            row(QueryType.MAX_TIME_GAP.name.toLowerCase()),
            row(QueryType.MAX_TEMPERATURE.name),
            row(QueryType.MAX_TEMPERATURE.name.toLowerCase()),
            row(QueryType.TOTAL_VOLUME.name),
            row(QueryType.TOTAL_VOLUME.name.toLowerCase()),
            row(QueryType.WEIGHTED_AVERAGE_TEMPERATURE.name),
            row(QueryType.WEIGHTED_AVERAGE_TEMPERATURE.name.toLowerCase())
        ) { queryType ->
            every { mockService.queryMessages(any(), symbolOne) } returns expected
            val response = shouldNotThrowAny {
                client.toBlocking().exchange<Any, QueryResult>(
                    HttpRequest.GET("/messages?symbol=$symbolOne&queryType=$queryType"),
                    Argument.of(QueryResult::class.java)
                )
            }
            assertSoftly {
                response.body.ifPresentOrElse(
                    { body -> body shouldBe expected },
                    { fail("No body present") }
                )
                response.status shouldBe HttpStatus.OK
            }
        }
    }

    "GET /messages - invalid query params" {
        forAll(
            row("?queryType=${QueryType.MAX_TIME_GAP}"),
            row("?symbol=$symbolOne&queryType=INVALID_QUERY_TYPE"),
            row("")
        ) { queryString ->
            val response = shouldThrow<HttpClientResponseException> {
                client.toBlocking().exchange<Any, QueryResult>(
                    HttpRequest.GET("/messages$queryString")
                )
            }
            response.status shouldBe HttpStatus.BAD_REQUEST
        }
    }

    "GET /messages - error occurs in service" {
        val mockService = getMock(messageService)
        every { mockService.queryMessages(QueryType.MAX_TIME_GAP, symbolOne) } throws HibernateException("test")
        val response = shouldThrow<HttpClientResponseException> {
            client.toBlocking().exchange<Any, QueryResult>(
                HttpRequest.GET("/messages?symbol=$symbolOne&queryType=${QueryType.MAX_TIME_GAP}")
            )
        }
        response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
    }

    "POST /messages - success" {
        val expected = validMessageOne.copy(id = null)
        val mockService = getMock(messageService)
        every { mockService.saveMessage(expected) } returns validMessageOne
        val response = shouldNotThrowAny {
            client.toBlocking().exchange<Any, Message>(
                HttpRequest.POST("/messages", validMessageOne),
                Argument.of(Message::class.java)
            )
        }
        assertSoftly {
            response.status shouldBe HttpStatus.CREATED
            response.body.ifPresentOrElse(
                { body -> body shouldBe expected },
                { fail("No body present") }
            )
        }
    }

    "POST /messages - error occurs in service" {
        val mockService = getMock(messageService)
        every { mockService.queryMessages(QueryType.MAX_TIME_GAP, symbolOne) } throws HibernateException("test")
        val response = shouldThrow<HttpClientResponseException> {
            client.toBlocking().exchange<Any, QueryResult>(
                HttpRequest.POST("/messages", validMessageOne)
            )
        }
        response.status shouldBe HttpStatus.INTERNAL_SERVER_ERROR
    }

}) {
    @MockBean(MessageServiceImpl::class)
    fun messageService(): MessageService {
        return mockk(relaxUnitFun = true)
    }
}
