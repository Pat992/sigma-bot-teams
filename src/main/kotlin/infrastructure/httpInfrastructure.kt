package com.htth.sigmabotteams.infrastructure

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.*
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*

suspend fun getRequest(
    url: String,
    headers: StringValues,
    token: String,
    client: HttpClient
): HttpResponse = client.get(url) {
    bearerAuth(token)
    headers { appendAll(headers) }
}

suspend fun postRequest(
    url: String,
    headers: StringValues,
    token: String,
    body: String,
    client: HttpClient
): HttpResponse = client.post(url) {
    contentType(ContentType.Application.Json)
    setBody(body)
    headers { appendAll(headers) }
    bearerAuth(token)
}

suspend fun formRequest(
    url: String,
    headers: StringValues,
    formParameters: Parameters,
    client: HttpClient
): HttpResponse = client.submitForm(url, formParameters = formParameters) {
    headers { appendAll(headers) }
}

fun getHttpClient(): HttpClient = HttpClient {
    install(HttpTimeout) { requestTimeoutMillis = 100000 }
    install(HttpRedirect) { checkHttpMethod = false }
    followRedirects = true
}