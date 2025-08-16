package com.htth.sigmabotteams

import com.htth.sigmabotteams.data.MSSignInObject
import com.htth.sigmabotteams.dto.ChatDto
import com.htth.sigmabotteams.dto.getIds
import com.htth.sigmabotteams.dto.getPrintable
import com.htth.sigmabotteams.dto.toMessage
import com.htth.sigmabotteams.infrastructure.getHttpClient
import com.htth.sigmabotteams.service.*
import com.htth.sigmabotteams.utils.printSection
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.*

suspend fun main() {
    var job: Job?
    val sentMessageIds = mutableListOf<String>()
    val httpClient = getHttpClient()
    val dotenv = Dotenv.load()
    val openAiToken = dotenv.get("OPENAI_TOKEN")

    printSection(
        title = "Copy URL to sign-in",
        body = MSSignInObject.getSignInUrl()
    )

    val token = signIn(httpClient)

    val me = getMe(httpClient, token)

    val chatSettings = initializeChatSettings()

    printSection(
        title = "Getting chats.",
        body = "Please wait a moment..."
    )

    val chats = getTeamsChats(httpClient, token)

    printSection(
        title = "Recent Chats",
        body = chats.getPrintable()
    )

    printSection(
        title = "Choose a Chat by its Id",
        body = "Valid values are from 0 to ${chats.size - 1}, scroll up to get more information"
    )

    val selectedChat: ChatDto = run {
        var chosen: ChatDto? = null
        do {
            print("Select chat index: ")
            val input = readlnOrNull()

            val index = input?.toIntOrNull()
            if (index != null && index in chats.indices) {
                chosen = chats[index]
            } else {
                println("❌ Invalid input, try again.\n")
            }
        } while (chosen == null)
        chosen
    }

    coroutineScope {
        job = launch {
            while (isActive) {
                // Get messages
                val messages = getTeamsChatMessages(httpClient, token, selectedChat, me, sentMessageIds)
                messages.getIds(sentMessageIds)

                if (messages.isNotEmpty()) {
                    printSection(
                        title = "Messages found, sending to OpenAI.",
                        body = messages.toMessage()
                    )
                    val res = sendOpenAiResponseRequest(
                        httpClient,
                        messages,
                        openAiToken,
                        selectedChat,
                        chatSettings
                    )

                    printSection(
                        title = "Response from Bot.",
                        body = res
                    )

                    sendTeamsMessage(
                        httpClient,
                        token,
                        selectedChat,
                        res
                    )
                }
                delay(5000)
            }
        }
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down...")
        job?.cancel()
        println("Shutdown complete.")
    })
}