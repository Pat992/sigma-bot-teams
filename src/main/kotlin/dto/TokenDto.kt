package com.htth.sigmabotteams.dto

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)

fun String.toTokenDto(): TokenDto = Json
    .decodeFromString<JsonObject>(this)
    .let {
        TokenDto(
            accessToken = it["access_token"]?.jsonPrimitive?.content ?: "",
            refreshToken = it["refresh_token"]?.jsonPrimitive?.content ?: ""
        )
    }