package com.tylerlofgren.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.micronaut.core.annotation.Introspected
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Introspected
data class Message(
        @Id
        @JsonIgnore
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long?,
        @field:NotNull
        @Column
        var timestamp: Long?,
        @field:Size(min=3, max = 3)
        @field:NotBlank
        @Column
        var symbol: String?,
        @field:NotNull
        @Column
        var volume: Int?,
        @field:NotNull
        @Column
        var temperature: Int?
)
