package com.tylerlofgren.controller

import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.Message
import com.tylerlofgren.domain.QueryResult
import com.tylerlofgren.repo.MessageRepository
import com.tylerlofgren.service.MessageService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.reactivex.Single
import javax.inject.Inject

@Controller
class MessageController(
        @Inject val messageService: MessageService,
        @Inject val messageRepository: MessageRepository
) {
    @Get("/messages", produces = [MediaType.APPLICATION_JSON])
    fun listMessages(@QueryValue symbol: String?, @QueryValue queryType: QueryType?): Single<HttpResponse<QueryResult>> {
        if (queryType == null || symbol == null) {
            return Single.just(HttpResponse.badRequest())
        }

        return Single.just(HttpResponse.ok(messageService.queryMessages(queryType, messageRepository.findBySymbol(symbol)))) //TODO: Handle exceptions
    }

    @Post(value = "/messages", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun postMessages(@Body body: Message): Single<HttpResponse<Message>> {
        messageRepository.save(body)//TODO: Handle exceptions from here. Be sure to act differently if the symbol isn't found
        return Single.just(HttpResponse.ok(body))
    }
}
