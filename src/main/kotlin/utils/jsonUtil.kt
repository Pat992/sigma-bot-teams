package com.htth.sigmabotteams.utils

import kotlinx.serialization.json.*

fun JsonElement?.toNullableObject(): JsonObject? = try {
    this?.jsonObject
} catch (_: Exception) {
    null
}

fun JsonElement?.toNullableArray(): JsonArray? = try {
    this?.jsonArray
} catch (_: Exception) {
    null
}

fun JsonElement?.getContent(): Any? =
    when (this) {
        is JsonArray -> this.toString()
        is JsonNull -> null
        else -> this?.jsonPrimitive?.content
    }