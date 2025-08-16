package com.htth.sigmabotteams.utils

import io.ktor.client.statement.*
import io.ktor.utils.io.*

suspend fun HttpResponse.bodyToString(): String = this.bodyAsChannel().readRemaining().readText()