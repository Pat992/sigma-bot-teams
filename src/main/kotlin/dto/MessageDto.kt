package com.htth.sigmabotteams.dto

import com.htth.sigmabotteams.data.CharacterEnum
import com.htth.sigmabotteams.data.toInstructions
import com.htth.sigmabotteams.utils.getContent
import com.htth.sigmabotteams.utils.toNullableArray
import com.htth.sigmabotteams.utils.toNullableObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.Jsoup

data class MessageDto(
    val id: String,
    val content: String,
    val member: MemberDto
)

fun String.toMessageDtoList(me: MemberDto, sentMessages: List<String>): List<MessageDto> {
    val messageDtoList = mutableListOf<MessageDto>()

    Json.decodeFromString<JsonObject>(this)["value"]
        ?.toNullableArray()
        ?.forEach {
            val item = it.toNullableObject()
            val messageId = item?.get("id")?.getContent() ?: ""
            val contentBody = item?.get("body")?.toNullableObject()
            val contentType = contentBody?.get("contentType")?.getContent() ?: ""
            val content: String = (contentBody?.get("content")?.getContent() ?: "") as String
            val sender = item?.get("from")?.toNullableObject()?.get("user")
            val senderId: String = (sender?.toNullableObject()?.get("id")?.getContent() ?: "") as String

            if (
                (contentType == "html" || contentType == "text") &&
                senderId != me.id &&
                !sentMessages.contains(messageId)
            ) {
                val contentCleaned = if (contentType == "html") Jsoup.parse(content).text() else content
                if (contentCleaned.isNotBlank()) {
                    messageDtoList.add(
                        MessageDto(
                            id = item?.get("id")?.jsonPrimitive?.content ?: "",
                            content = contentCleaned,
                            member = MemberDto(
                                id = senderId,
                                name = (sender?.toNullableObject()?.get("displayName")?.getContent() ?: "") as String
                            )
                        )
                    )
                }
            }

        }

    return messageDtoList
}

fun List<MessageDto>.getIds(idList: MutableList<String>) = idList.addAll(this.map { it.id })

fun List<MessageDto>.toOpenaiRequest(
    characterEnum: CharacterEnum,
    me: MemberDto,
    chatDto: ChatDto,
    language: String
): String {
    val messages = this
    return Json.encodeToString(
        mapOf(
            "model" to "gpt-5",
            "instructions" to characterEnum.toInstructions(chatDto, me, language),
            "input" to messages.toMessage()
        )
    )
}

fun List<MessageDto>.toMessage(): String = this.joinToString("\n") {
    "${it.member.name}: ${it.content}"
}