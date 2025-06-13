package com.onkar.projectx.data




data class AuthState(
    val mobile: String = "",
    val mobileError: String? = null,
    val otp: String = "",
    val otpError: String? = null,
    val isLoading: Boolean = false,
    val timer: Int = 30
)