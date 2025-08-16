package com.htth.sigmabotteams.dto

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

data class MemberDto(
    val id: String,
    val name: String
)

fun String.toMe(): MemberDto = Json
    .decodeFromString<JsonObject>(this)
    .let {
        MemberDto(
            id = it["id"]?.jsonPrimitive?.content ?: "",
            name = it["displayName"]?.jsonPrimitive?.content ?: ""
        )
    }