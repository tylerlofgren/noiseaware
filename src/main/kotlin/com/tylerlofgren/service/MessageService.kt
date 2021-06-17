package com.tylerlofgren.service

import com.tylerlofgren.constant.QueryType
import com.tylerlofgren.domain.Message
import com.tylerlofgren.domain.QueryResult

interface MessageService {
    fun queryMessages(queryType: QueryType, symbol: String): QueryResult
    fun saveMessage(message: Message): Message
}
