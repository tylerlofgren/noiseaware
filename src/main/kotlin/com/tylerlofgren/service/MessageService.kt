package com.tylerlofgren.service

import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.Message
import com.tylerlofgren.domain.QueryResult
import javax.inject.Singleton

@Singleton
class MessageService {
    fun queryMessages(queryType: QueryType, messages: List<Message>): QueryResult {
        return QueryResult(
                when (queryType) {
                    QueryType.MAX_TIME_GAP -> getMaxTimeGap(messages)
                    QueryType.TOTAL_VOLUME -> messages.sumOf { it.volume }.toLong()
                    QueryType.MAX_TEMPERATURE -> {
                        if(messages.isEmpty()) {
                            0
                        } else {
                            messages.maxOf { it.temperature }.toLong()
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
            val currentGap = message.timestamp - chronologicalMessages[i - 1].timestamp
            if (currentGap > maxTimeGap) {
                maxTimeGap = currentGap
            }
        }

        return maxTimeGap
    }

    private fun getWeightedAvgTemp(messages: List<Message>): Int {
        if(messages.isEmpty()) {
            return 0
        }
       return messages.sumOf { it.volume * it.temperature }.div(messages.sumOf { it.volume })
    }
}
