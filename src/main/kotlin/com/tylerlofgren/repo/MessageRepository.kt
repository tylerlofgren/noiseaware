package com.tylerlofgren.repo

import com.tylerlofgren.domain.Message
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface MessageRepository : CrudRepository<Message, Long> {
    fun findBySymbol(symbol: String): List<Message>
    fun save(message: Message): Message
}
