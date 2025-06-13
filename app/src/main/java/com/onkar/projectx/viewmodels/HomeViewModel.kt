package com.onkar.projectx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.ApiClient
import com.onkar.projectx.data.*
import com.onkar.projectx.data.DataStoreManager.Companion.TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataStore: DataStoreManager) : ViewModel() {
    var authToken = ""

    private val _homeUiState = MutableStateFlow(HomeDashboardUiState())
    val homeUiState: StateFlow<HomeDashboardUiState> = _homeUiState

    private val _userUiState = MutableStateFlow(UserDashboardUiState())
    val userUiState: StateFlow<UserDashboardUiState> = _userUiState

    init {
        viewModelScope.launch {
            authToken = dataStore.getToken()
        }
    }

    fun fetchHomeDashboard() {
        viewModelScope.launch {
            _homeUiState.value = _homeUiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = ApiClient.homeApi.getHomeData(authToken)
                _homeUiState.value = _homeUiState.value.copy(
                    isLoading = false,
                    banners = response.banners,
                    sections = response.items
                )
            } catch (e: Exception) {
                _homeUiState.value = _homeUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to fetch home data: ${e.localizedMessage}"
                )
            }
        }
    }

    fun fetchUserDashboard() {
        viewModelScope.launch {
            _userUiState.value = _userUiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = ApiClient.homeApi.getUserDashboard(authToken)
                _userUiState.value = _userUiState.value.copy(
                    isLoading = false,
                    addressLine = response.address_line1,
                    fullAddress = response.address_full,
                    walletBalance = response.wallet_balance
                )
            } catch (e: Exception) {
                _userUiState.value = _userUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to fetch user data: ${e.localizedMessage}"
                )
            }
        }
    }
}

data class HomeDashboardUiState(
    val isLoading: Boolean = false,
    val banners: List<Banner> = emptyList(),
    val sections: List<HomeSection> = emptyList(),
    val errorMessage: String? = null
)

data class UserDashboardUiState(
    val isLoading: Boolean = false,
    val addressLine: String = "",
    val fullAddress: String = "",
    val walletBalance: String = "",
    val errorMessage: String? = null
)