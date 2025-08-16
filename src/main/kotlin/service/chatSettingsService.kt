package com.htth.sigmabotteams.service

import com.htth.sigmabotteams.data.CharacterEnum
import com.htth.sigmabotteams.data.ModelEnum
import com.htth.sigmabotteams.dto.ChatSettingsDto

fun initializeChatSettings(): ChatSettingsDto {
    val model = valueFromInput(
        "model",
        ModelEnum.entries.joinToString("\n") { enum -> "${enum.ordinal}: ${enum.value}" },
        ModelEnum.GPT_5.value,
        ModelEnum.entries.map { enum -> enum.ordinal.toString() }.toList(),
    ).let {
        if (!it.isNullOrBlank()) ModelEnum.entries[it.toInt()]
        else ModelEnum.GPT_5
    }

    val language = run {
        print("Select language, default is english: ")
        val input = readlnOrNull()

        if (input.isNullOrBlank()) "english" else input
    }

    val character = valueFromInput(
        "character",
        CharacterEnum.entries.joinToString("\n") { enum -> "${enum.ordinal}: ${enum.value} - ${enum.description}" },
        CharacterEnum.DEFAULT_BOT.value,
        CharacterEnum.entries.map { enum -> enum.ordinal.toString() }.toList(),
    ).let {
        if (!it.isNullOrBlank()) CharacterEnum.entries[it.toInt()]
        else CharacterEnum.DEFAULT_BOT
    }

    return ChatSettingsDto(
        model,
        character,
        language
    )
}

private fun valueFromInput(
    type: String,
    text: String,
    defaultValue: String,
    validValues: List<String>
): String? {
    print("Select $type, default is $defaultValue:\n$text: ")
    val input = readlnOrNull()

    if (!validValues.contains(input)) {
        println("Invalid $type selected, using '$defaultValue'")
    }
    return input
}
