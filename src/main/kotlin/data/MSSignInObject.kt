package com.htth.sigmabotteams.data

object MSSignInObject {
    const val clientId = "14d82eec-204b-4c2f-b7e8-296a70dab67e"
    var port = 8080
    const val grantType = "authorization_code"
    val scopes = listOf(
        "offline_access",
        "openid",
        "profile",
        "Chat.Read",
        "ChannelMessage.Send",
        "ChatMessage.Send",
    )

    fun getSignInUrl() =
        "https://login.microsoftonline.com/organizations/oauth2/v2.0/authorize" +
                "?client_id=$clientId" +
                "&response_type=code" +
                "&redirect_uri=http://localhost:$port" +
                "&scope=${scopes.joinToString("+")}" +
                "&response_mode=form_post"
}