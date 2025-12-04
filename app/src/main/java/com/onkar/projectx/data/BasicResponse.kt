package com.onkar.projectx.data

import com.google.gson.annotations.SerializedName

data class BasicResponse(
    val message: String,
    val error: Boolean
)

data class TokenResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("token_type")
    val tokenType: String? = null,
    val error: Boolean = false,
    val message: String? = null
)