package com.htth.sigmabotteams.dto

import com.htth.sigmabotteams.data.CharacterEnum
import com.htth.sigmabotteams.data.ModelEnum

data class ChatSettingsDto(
    val model: ModelEnum,
    val character: CharacterEnum,
    val language: String
)