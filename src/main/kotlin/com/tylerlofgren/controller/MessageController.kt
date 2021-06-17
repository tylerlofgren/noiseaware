package com.tylerlofgren.controller

import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.Message
import com.tylerlofgren.domain.QueryResult
import com.tylerlofgren.service.MessageService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.reactivex.Single
import mu.KotlinLogging
import javax.inject.Inject

@Controller
open class MessageController(
        @Inject val messageService: MessageService,
) {

    companion object {
        val log = KotlinLogging.logger {}
    }

    @Get("/messages", produces = [MediaType.APPLICATION_JSON])
    open fun listMessages(@QueryValue symbol: String?, @QueryValue queryType: QueryType?): Single<HttpResponse<QueryResult>> {
        if (queryType == null || symbol == null) {
            return Single.just(HttpResponse.badRequest())
        }

        val result = try {
            messageService.queryMessages(queryType, symbol)
        } catch(e: Exception) {
            log.error(e) { "Error occurred querying for messages" }
            return Single.just(HttpResponse.serverError())
        }

        return Single.just(HttpResponse.ok(result))
    }

    @Post(value = "/messages", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun postMessages(@Body body: Message): Single<HttpResponse<Message>> {
        try {
            messageService.saveMessage(body)
        } catch(e: Exception) {
            log.error(e) { "Error occurred saving message"}
            return Single.just(HttpResponse.serverError())
        }
        return Single.just(HttpResponse.created(body))
    }
}
