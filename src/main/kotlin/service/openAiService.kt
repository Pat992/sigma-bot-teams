package com.htth.sigmabotteams.service

import com.htth.sigmabotteams.dto.*
import com.htth.sigmabotteams.infrastructure.postRequest
import com.htth.sigmabotteams.utils.bodyToString
import com.htth.sigmabotteams.utils.getContent
import com.htth.sigmabotteams.utils.toNullableArray
import com.htth.sigmabotteams.utils.toNullableObject
import io.ktor.client.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

suspend fun sendOpenAiResponseRequest(
    httpClient: HttpClient,
    messages: List<MessageDto>,
    token: String,
    chatDto: ChatDto,
    chatSettingsDto: ChatSettingsDto,
    me: MemberDto
): String = postRequest(
    url = "https://api.openai.com/v1/responses",
    headers = StringValues.build { },
    token = token,
    body = messages.toOpenaiRequest(chatSettingsDto.character, me, chatDto, chatSettingsDto.language),
    client = httpClient
).let {
    (Json
        .decodeFromString<JsonObject?>(it.bodyToString())
        ?.get("output")
        ?.toNullableArray()
        ?.lastOrNull { item ->
            val obj = item.toNullableObject()
            obj?.contains("content") == true
        }
        ?.toNullableObject()
        ?.get("content")
        ?.toNullableArray()
        ?.firstOrNull { item ->
            val obj = item.toNullableObject()
            obj?.get("type").getContent() == "output_text"
        }
        ?.toNullableObject()
        ?.get("text")
        ?.getContent() ?: "") as String
}