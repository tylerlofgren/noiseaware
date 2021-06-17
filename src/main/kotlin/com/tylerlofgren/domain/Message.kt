package com.tylerlofgren.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class Message(
        @Id
        @JsonIgnore
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long?,
        @Column
        var timestamp: Long,
        @Column
        var symbol: String,
        @Column
        var volume: Int,
        @Column
        var temperature: Int
)
