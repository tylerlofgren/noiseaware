package com.tylerlofgren.service

import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.Message
import com.tylerlofgren.domain.QueryResult
import com.tylerlofgren.repo.MessageRepository
import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@CacheConfig("query")
open class MessageServiceImpl(
        @Inject val messageRepository: MessageRepository
) : MessageService {

    @Cacheable
    override fun queryMessages(queryType: QueryType, symbol: String): QueryResult {
        val messages = messageRepository.findBySymbol(symbol)
        return QueryResult(
                when (queryType) {
                    QueryType.MAX_TIME_GAP -> getMaxTimeGap(messages)
                    QueryType.TOTAL_VOLUME -> messages.sumOf { it.volume!! }.toLong()
                    QueryType.MAX_TEMPERATURE -> {
                        if (messages.isEmpty()) {
                            0
                        } else {
                            messages.maxOf { it.temperature!! }.toLong()
                        }
                    }
                    QueryType.WEIGHTED_AVERAGE_TEMPERATURE -> getWeightedAvgTemp(messages).toLong()
                })
    }

    private fun getMaxTimeGap(messages: List<Message>): Long {
        val chronologicalMessages = messages.sortedBy { it.timestamp }
        if (chronologicalMessages.isEmpty() || chronologicalMessages.size == 1) {
            return 0L
        }

        var maxTimeGap = 0L
        chronologicalMessages.forEachIndexed { i, message ->
            if (i == 0) {
                return@forEachIndexed
            }
            val currentGap = message.timestamp!! - chronologicalMessages[i - 1].timestamp!!
            if (currentGap > maxTimeGap) {
                maxTimeGap = currentGap
            }
        }

        return maxTimeGap
    }

    private fun getWeightedAvgTemp(messages: List<Message>): Int {
        if (messages.isEmpty()) {
            return 0
        }
        return messages.sumOf { it.volume!! * it.temperature!! }.div(messages.sumOf { it.volume!! })
    }

    override fun saveMessage(message: Message): Message {
        return messageRepository.save(message)
    }
}
