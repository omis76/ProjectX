package com.onkar.projectx.viewmodels

import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.ApiClient
import com.onkar.projectx.data.AuthState
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(private val dataStore: DataStoreManager) : ViewModel() {
    var a = 0
    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState

    private val _startScreen = MutableStateFlow<Screen?>(null)
    val startScreen: StateFlow<Screen?> = _startScreen

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            if (_startScreen.value == null) {
                checkStartScreen()
            }
        }
    }

    suspend fun checkStartScreen() {
        val startScreen = when {
            dataStore.isFirstLaunch() -> Screen.Splash
            !dataStore.isLoggedIn() -> Screen.Login
            else -> Screen.Home
        }
        _startScreen.value = startScreen
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.isLoggedIn()
    }

    fun markFirstLaunchDone() {
        viewModelScope.launch {
            dataStore.setFirstLaunch(false)
        }
    }

    fun markLoggedIn() {
        viewModelScope.launch {
            dataStore.setLoggedIn(true)
        }
    }

    fun onMobileChanged(value: String) {
        _uiState.value = _uiState.value.copy(mobile = value, mobileError = null)
    }

    fun sendOTP(onSuccess: () -> Unit) {
        val mobile = _uiState.value.mobile
        if (mobile.length != 10) {
            _uiState.value = _uiState.value.copy(mobileError = "Invalid number")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val response = ApiClient.homeApi.requestOTP(mapOf("phone" to mobile))
                if (response.error.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            mobileError = response.error
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        mobileError = e.localizedMessage ?: "something went wrong"
                    )
            }
        }
    }

    fun onOtpChanged(value: String) {
        _uiState.value = _uiState.value.copy(otp = value, otpError = null)
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val response = ApiClient.homeApi.verifyOTP(
                    mapOf(
                        "phone" to _uiState.value.mobile,
                        "otp" to _uiState.value.otp
                    )
                )
                if (response.error.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    dataStore.setToken(response.token)
                    onSuccess()
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            otpError = response.error
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        otpError = e.localizedMessage ?: "something went wrong"
                    )
            }
        }
    }

    fun resendOtp() {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in 30 downTo 0) {
                _uiState.value = _uiState.value.copy(timer = i)
                delay(1000)
            }
        }
    }

    suspend fun isFirstTime(): Boolean {
        return dataStore.isFirstLaunch()
    }
}