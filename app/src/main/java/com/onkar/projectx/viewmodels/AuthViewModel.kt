package com.onkar.projectx.viewmodels

import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.data.AuthState
import com.onkar.projectx.data.Resource
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
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
            authRepository.isFirstLaunch() -> Screen.Splash
            !authRepository.isLoggedIn() -> Screen.Login
            else -> Screen.Home
        }
        _startScreen.value = startScreen
    }

    suspend fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun markFirstLaunchDone() {
        viewModelScope.launch {
            authRepository.markFirstLaunchCompleted()
        }
    }

    fun markLoggedIn() {
        viewModelScope.launch {
            authRepository.markUserLoggedIn()
        }
    }

    fun onMobileChanged(value: String) {
        _uiState.value = _uiState.value.copy(mobile = value, mobileError = null)
    }

    fun sendOTP(onSuccess: () -> Unit) {
        val mobile = _uiState.value.mobile
        
        viewModelScope.launch {
            authRepository.sendOTP(mobile).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, mobileError = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, mobileError = null)
                        startTimer() // Start OTP resend timer
                        onSuccess()
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            mobileError = resource.message
                        )
                    }
                }
            }
        }
    }

    fun onOtpChanged(value: String) {
        _uiState.value = _uiState.value.copy(otp = value, otpError = null)
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val mobile = _uiState.value.mobile
        val otp = _uiState.value.otp
        
        viewModelScope.launch {
            authRepository.verifyOTP(mobile, otp).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, otpError = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, otpError = null)
                        onSuccess()
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            otpError = resource.message
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun resendOtp() {
        val mobile = _uiState.value.mobile
        
        viewModelScope.launch {
            authRepository.sendOTP(mobile).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, otpError = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, otpError = null)
                        startTimer() // Start OTP resend timer after successful resend
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            otpError = resource.message
                        )
                    }
                }
            }
        }
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
        return authRepository.isFirstLaunch()
    }
}