package com.application.core.data.local.mapper

/**
 * Base DataMapper interface
 */
interface DataMapper<FROM, TO> {
    fun mapTo(value: TO): FROM
}