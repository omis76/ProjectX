package com.onkar.projectx.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.HomeApiService
import com.onkar.projectx.data.CustomerAddress
import com.onkar.projectx.data.CustomerProfileResponse
import com.onkar.projectx.data.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: HomeApiService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var customerProfile by mutableStateOf<CustomerProfileResponse?>(null)
        private set

    var nearestAddress by mutableStateOf<CustomerAddress?>(null)
        private set

    var selectedAddress by mutableStateOf<CustomerAddress?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var isLocationDialogVisible by mutableStateOf(false)
        private set

    var hasNoAddresses by mutableStateOf(false)
        private set

    fun fetchCustomerProfile(onNotLoggedIn: () -> Unit = {}, onNoAddresses: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                // Check if user is logged in
                val isLoggedIn = dataStoreManager.isLoggedIn()
                if (!isLoggedIn) {
                    Log.d("ProfileViewModel", "User not logged in")
                    onNotLoggedIn()
                    return@launch
                }
                
                isLoading = true
                error = null
                val token = dataStoreManager.getToken()
                
                // Check if token is valid
                if (token.isEmpty() || token == "null") {
                    Log.d("ProfileViewModel", "Invalid token")
                    onNotLoggedIn()
                    return@launch
                }
                
                Log.d("ProfileViewModel", "Fetching customer profile")
                val response = apiService.getCustomerProfile(token)
                
                if (response.error == true) {
                    error = response.message ?: "Failed to fetch profile"
                    Log.e("ProfileViewModel", "Profile API error: ${response.message}")
                    // If auth error, redirect to login
                    if (response.message?.contains("auth", ignoreCase = true) == true ||
                        response.message?.contains("token", ignoreCase = true) == true) {
                        onNotLoggedIn()
                    }
                } else {
                    customerProfile = response
                    Log.d("ProfileViewModel", "Profile fetched: ${response.addresses?.size} addresses")
                    
                    // Check if user has addresses
                    response.addresses?.let { addresses ->
                        if (addresses.isNotEmpty()) {
                            hasNoAddresses = false
                            nearestAddress = addresses.first()
                            selectedAddress = addresses.first()
                        } else {
                            hasNoAddresses = true
                            Log.d("ProfileViewModel", "User has no saved addresses")
                            onNoAddresses()
                        }
                    } ?: run {
                        hasNoAddresses = true
                        Log.d("ProfileViewModel", "User has no addresses field")
                        onNoAddresses()
                    }
                }
            } catch (e: Exception) {
                error = "Error fetching profile: ${e.localizedMessage}"
                Log.e("ProfileViewModel", "Exception fetching profile", e)
                // Check if it's an auth error
                if (e.message?.contains("401") == true || 
                    e.message?.contains("403") == true ||
                    e.message?.contains("Unauthorized", ignoreCase = true) == true) {
                    onNotLoggedIn()
                }
            } finally {
                isLoading = false
            }
        }
    }


    fun showLocationDialog() {
        isLocationDialogVisible = true
    }

    fun hideLocationDialog() {
        isLocationDialogVisible = false
    }

    fun selectAddress(address: CustomerAddress) {
        selectedAddress = address
        hideLocationDialog()
        Log.d("ProfileViewModel", "Address selected: ${address.addressLine1}, ${address.cityName}")
    }

    fun getAddressText(address: CustomerAddress?): String {
        return address?.let {
            "${it.addressLine1}, ${it.cityName}"
        } ?: "Select location"
    }
}