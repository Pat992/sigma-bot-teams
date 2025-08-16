package com.htth.sigmabotteams.service

import com.htth.sigmabotteams.data.MSSignInObject
import com.htth.sigmabotteams.dto.*
import com.htth.sigmabotteams.infrastructure.formRequest
import com.htth.sigmabotteams.infrastructure.getRequest
import com.htth.sigmabotteams.infrastructure.postRequest
import com.htth.sigmabotteams.infrastructure.runServer
import com.htth.sigmabotteams.utils.bodyToString
import com.htth.sigmabotteams.utils.printSection
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.json.Json

const val graphApiBaseUrl = "https://graph.microsoft.com/v1.0"
suspend fun signIn(httpClient: HttpClient): TokenDto {
    val result = CompletableDeferred<String>()

    val server = runServer(result)

    val code = result.await()

    printSection(
        title = "Signing in",
        body = "Please wait a moment..."
    )

    return formRequest(
        url = "https://login.microsoftonline.com/organizations/oauth2/token",
        headers = StringValues.build { append("Content-Type", "application/x-www-form-urlencoded") },
        client = httpClient,
        formParameters = parameters {
            appendAll(
                "client_id" to MSSignInObject.clientId,
                "code" to code,
                "redirect_uri" to "http://localhost:${MSSignInObject.port}",
                "grant_type" to MSSignInObject.grantType
            )
        }
    )
        .bodyToString()
        .toTokenDto()
        .also {
            server.stop()
            printSection(
                title = "Signing in",
                body = "Sign in completed."
            )
        }
}

suspend fun getMe(httpClient: HttpClient, tokenDto: TokenDto): MemberDto = getRequest(
    url = $$"$$graphApiBaseUrl/me",
    headers = StringValues.build { append("Host", "graph.microsoft.com") },
    token = tokenDto.accessToken,
    client = httpClient
)
    .bodyToString()
    .toMe()

suspend fun getTeamsChats(httpClient: HttpClient, tokenDto: TokenDto): List<ChatDto> = getRequest(
    url = $$"$$graphApiBaseUrl/me/chats?$expand=members&$top=20",
    headers = StringValues.build { append("Host", "graph.microsoft.com") },
    token = tokenDto.accessToken,
    client = httpClient
)
    .bodyToString()
    .toChatDtoList()

suspend fun getTeamsChatMessages(
    httpClient: HttpClient,
    tokenDto: TokenDto,
    chatDto: ChatDto,
    me: MemberDto,
    sentMessages: List<String>
): List<MessageDto> = getRequest(
    url = $$"$$graphApiBaseUrl/me/chats/$${chatDto.id}/messages?$top=20",
    headers = StringValues.build { append("Host", "graph.microsoft.com") },
    token = tokenDto.accessToken,
    client = httpClient
)
    .bodyToString()
    .toMessageDtoList(me, sentMessages)

suspend fun sendTeamsMessage(
    httpClient: HttpClient,
    tokenDto: TokenDto,
    chatDto: ChatDto,
    message: String
) = postRequest(
    url = $$"$$graphApiBaseUrl/chats/$${chatDto.id}/messages",
    headers = StringValues.build { append("Host", "graph.microsoft.com") },
    token = tokenDto.accessToken,
    client = httpClient,
    body = Json.encodeToString(
        mapOf(
            "body" to mapOf(
                "content" to message
            )
        )
    )
)