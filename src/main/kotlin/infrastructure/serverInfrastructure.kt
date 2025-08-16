package com.htth.sigmabotteams.infrastructure

import com.htth.sigmabotteams.data.MSSignInObject
import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CompletableDeferred

fun runServer(result: CompletableDeferred<String>) = embeddedServer(
    factory = CIO,
    configure = {
        connectors.add(EngineConnectorBuilder().apply {
            port = MSSignInObject.port
            reuseAddress = true
        })
    }) {
    routing {
        post {
            val responseBody = call.receive<Parameters>()
            if (responseBody.contains("code")) {
                call.respondText("Sign In successful.")
                result.complete(responseBody["code"] ?: "")
            } else {
                println(responseBody)
                call.respondText("Sign In failed.")
                result.complete("")
            }
        }
    }
}.start(wait = false)
