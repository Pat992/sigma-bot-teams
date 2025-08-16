package com.htth.sigmabotteams.dto

import kotlinx.serialization.json.*

data class ChatDto(
    val id: String,
    val lastRead: String,
    val chatType: String,
    val members: List<MemberDto>
)

fun String.toChatDtoList(): List<ChatDto> {
    val chatDtoList = mutableListOf<ChatDto>()

    Json.decodeFromString<JsonObject>(this)["value"]
        ?.jsonArray
        ?.forEach {
            val item = it.jsonObject
            val membersDtoList = mutableListOf<MemberDto>()
            item["members"]?.jsonArray?.forEach { mb ->
                val member = mb.jsonObject
                membersDtoList.add(
                    MemberDto(
                        id = member["userId"]?.jsonPrimitive?.content ?: "",
                        name = member["displayName"]?.jsonPrimitive?.content ?: ""
                    )
                )
            }

            chatDtoList.add(
                ChatDto(
                    id = item["id"]?.jsonPrimitive?.content ?: "",
                    lastRead = item["viewpoint"]?.jsonObject["lastMessageReadDateTime"]?.jsonPrimitive?.content ?: "",
                    chatType = item["chatType"]?.jsonPrimitive?.content ?: "",
                    members = membersDtoList
                )
            )
        }
    return chatDtoList
}

fun List<ChatDto>.getPrintable(): String = this.mapIndexed { i, it ->
    val membersStr = it.members
        .mapIndexed { iMb, mb -> if (iMb > 0) ", ${mb.name}" else mb.name }
        .joinToString("")

    """
        Chat Id: $i
        Chat Type: ${it.chatType}
        Last read: ${it.lastRead}
        Members: $membersStr
        -------------------------------------------------------------------
    """.trimIndent()
}.joinToString("\n")