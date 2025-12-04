package com.onkar.projectx.data

data class BasicResponse(
    val message: String,
    val error: Boolean
)

data class TokenResponse(
    val message: String,
    val token: String,
    val error: String?
)