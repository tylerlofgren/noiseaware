package com.tylerlofgren.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.Message
import com.tylerlofgren.repo.MessageRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.reactivex.Single
import javax.inject.Inject

@Controller
class MessageController(
        @Inject val messageRepository: MessageRepository,
        @Inject val objectMapper: ObjectMapper
) {
    @Get("/messages", produces = [MediaType.APPLICATION_JSON])
    fun listMessages(@QueryValue symbol: String?, @QueryValue queryType: QueryType?): Single<HttpResponse<String>> {
        if (queryType != null) {
            if(symbol == null) {
                return Single.just(HttpResponse.badRequest())
            }
            return Single.just(HttpResponse.ok(objectMapper.writeValueAsString(
                    when (queryType) {
                        QueryType.MAX_TIME_GAP -> getMaxTimeGap(symbol)
                        QueryType.TOTAL_VOLUME -> getTotalVolume(symbol)
                        QueryType.MAX_TEMPERATURE -> getMaxTemp(symbol)
                        QueryType.WEIGHTED_AVERAGE_TEMPERATURE -> getWeightedAvgTemp(symbol)
                    }
            )))
        } else {
            if(symbol != null) {
                return Single.just(HttpResponse.ok(objectMapper.writeValueAsString(messageRepository.findBySymbol(symbol))))
            }
        }
        return Single.just(HttpResponse.ok(objectMapper.writeValueAsString(messageRepository.findAll().toList())))
    }

    data class QueryResult(val result: Long)

    private fun getMaxTimeGap(symbol: String): QueryResult {
        return QueryResult(1)
    }

    private fun getTotalVolume(symbol: String): QueryResult {
        return QueryResult(1)
    }

    private fun getMaxTemp(symbol: String): QueryResult {
        return QueryResult(1)
    }

    private fun getWeightedAvgTemp(symbol: String): QueryResult {
        return QueryResult(1)
    }

    @Post(value = "/messages", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun postMessages(@Body body: Message): Single<HttpResponse<Message>> {
        messageRepository.save(body)//Handle exceptions from here. Be sure to act differently if the symbol isn't found
        return Single.just(HttpResponse.ok(body))
    }
}
