package com.tylerlofgren.constant

import com.fasterxml.jackson.annotation.JsonCreator

enum class QueryType {
    MAX_TIME_GAP,
    TOTAL_VOLUME,
    MAX_TEMPERATURE,
    WEIGHTED_AVERAGE_TEMPERATURE;

    companion object {
        @JvmStatic
        @JsonCreator
        fun from(value: String): QueryType? = values().find { it.name == value.toUpperCase() }
    }
}
