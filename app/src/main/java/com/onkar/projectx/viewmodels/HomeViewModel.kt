package com.onkar.projectx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.data.*
import com.onkar.projectx.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val appDataManager: AppDataManager
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeDashboardUiState())
    val homeUiState: StateFlow<HomeDashboardUiState> = _homeUiState

    private val _userUiState = MutableStateFlow(UserDashboardUiState())
    val userUiState: StateFlow<UserDashboardUiState> = _userUiState

    init {
        loadCachedData()
        observeDataChanges()
    }

    private fun loadCachedData() {
        // Load cached data immediately if available
        val cachedBanners = appDataManager.getHomeBanners()
        val cachedSections = appDataManager.getHomeSections()
        
        if (cachedBanners.isNotEmpty() || cachedSections.isNotEmpty()) {
            _homeUiState.value = _homeUiState.value.copy(
                banners = cachedBanners,
                sections = cachedSections
            )
        }

        // Load cached user data
        val defaultAddress = appDataManager.getDefaultAddress()
        if (defaultAddress != null) {
            _userUiState.value = _userUiState.value.copy(
                addressLine = defaultAddress.addressLine1,
                fullAddress = "${defaultAddress.addressLine1}, ${defaultAddress.cityName}"
            )
        }
    }

    private fun observeDataChanges() {
        // Observe changes to cached data
        viewModelScope.launch {
            appDataManager.homeData.collectLatest { homeData ->
                homeData?.let {
                    _homeUiState.value = _homeUiState.value.copy(
                        banners = it.banners,
                        sections = it.items
                    )
                }
            }
        }

        viewModelScope.launch {
            appDataManager.profileData.collectLatest { profile ->
                profile?.addresses?.firstOrNull()?.let { address ->
                    _userUiState.value = _userUiState.value.copy(
                        addressLine = address.addressLine1,
                        fullAddress = "${address.addressLine1}, ${address.cityName}"
                    )
                }
            }
        }
    }

    fun fetchHomeDashboard() {
        viewModelScope.launch {
            homeRepository.getHomeDashboard().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _homeUiState.value = _homeUiState.value.copy(
                            isLoading = true,
                            errorMessage = null,
                            banners = resource.data?.banners ?: _homeUiState.value.banners,
                            sections = resource.data?.items ?: _homeUiState.value.sections
                        )
                    }
                    is Resource.Success -> {
                        _homeUiState.value = _homeUiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            banners = resource.data?.banners ?: emptyList(),
                            sections = resource.data?.items ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _homeUiState.value = _homeUiState.value.copy(
                            isLoading = false,
                            errorMessage = resource.message,
                            banners = resource.data?.banners ?: _homeUiState.value.banners,
                            sections = resource.data?.items ?: _homeUiState.value.sections
                        )
                    }
                }
            }
        }
    }

    fun fetchUserDashboard() {
        viewModelScope.launch {
            homeRepository.getUserDashboard().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _userUiState.value = _userUiState.value.copy(
                            isLoading = true,
                            errorMessage = null,
                            addressLine = resource.data?.address_line1 ?: _userUiState.value.addressLine,
                            fullAddress = resource.data?.address_full ?: _userUiState.value.fullAddress,
                            walletBalance = resource.data?.wallet_balance ?: _userUiState.value.walletBalance
                        )
                    }
                    is Resource.Success -> {
                        _userUiState.value = _userUiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            addressLine = resource.data?.address_line1 ?: "",
                            fullAddress = resource.data?.address_full ?: "",
                            walletBalance = resource.data?.wallet_balance ?: ""
                        )
                    }
                    is Resource.Error -> {
                        _userUiState.value = _userUiState.value.copy(
                            isLoading = false,
                            errorMessage = resource.message,
                            addressLine = resource.data?.address_line1 ?: _userUiState.value.addressLine,
                            fullAddress = resource.data?.address_full ?: _userUiState.value.fullAddress,
                            walletBalance = resource.data?.wallet_balance ?: _userUiState.value.walletBalance
                        )
                    }
                }
            }
        }
    }

    fun refreshHomeDashboard() {
        viewModelScope.launch {
            homeRepository.refreshHomeDashboard().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _homeUiState.value = _homeUiState.value.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        _homeUiState.value = _homeUiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            banners = resource.data?.banners ?: emptyList(),
                            sections = resource.data?.items ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _homeUiState.value = _homeUiState.value.copy(
                            isLoading = false,
                            errorMessage = resource.message
                        )
                    }
                }
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