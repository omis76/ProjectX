package com.onkar.projectx.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.HomeApiService
import com.onkar.projectx.data.AddressRequest
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.LocationCheckRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationPickerViewModel @Inject constructor(
    private val apiService: HomeApiService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var isSaving by mutableStateOf(false)
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    var isCheckingLocation by mutableStateOf(false)
        private set

    var locationServiceable by mutableStateOf<Boolean?>(null)
        private set

    var locationMessage by mutableStateOf<String?>(null)
        private set

    // Store location check response data for address saving
    private var currentCityId: Int? = null
    private var currentCityName: String? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    fun resetState() {
        saveSuccess = false
        locationServiceable = null
        locationMessage = null
    }

    fun resetLocationServiceability() {
        locationServiceable = null
        locationMessage = null
        currentCityId = null
        currentCityName = null
        currentLatitude = null
        currentLongitude = null
    }

    fun checkLocationServiceability(latitude: Double, longitude: Double) {
        if (isCheckingLocation) return  // Prevent multiple concurrent calls

        viewModelScope.launch {
            try {
                isCheckingLocation = true
                val token = dataStoreManager.getToken()
                val request = LocationCheckRequest(latitude, longitude)
                val response = apiService.checkLocationServiceability(token, request)
                
                // Store current location data
                currentLatitude = latitude
                currentLongitude = longitude
                
                // Check if location is serviceable based on response format
                locationServiceable = if (response.error == true) {
                    false // Not serviceable
                } else if (response.cityId != null && response.cityName != null) {
                    // Store city data for later use in address saving
                    currentCityId = response.cityId
                    currentCityName = response.cityName
                    true // Serviceable
                } else {
                    false // Unknown response format, assume not serviceable
                }
                
                locationMessage = if (response.error == true) {
                    response.message ?: "Sorry, we don't deliver to this location yet."
                } else if (response.cityId != null && response.cityName != null) {
                    "Great! We deliver to ${response.cityName}."
                } else {
                    "Unable to determine serviceability for this location."
                }
            } catch (e: Exception) {
                locationServiceable = false
                locationMessage = "Error checking location serviceability: ${e.localizedMessage}"
            } finally {
                isCheckingLocation = false
            }
        }
    }

    fun saveAddress(userInput: String, geoAddress: String, note: String) {
        if (isSaving) return  // Debounce
        
        // Check if we have all required data
        if (currentCityId == null || currentCityName == null || currentLatitude == null || currentLongitude == null) {
            // Handle error - missing location data
            return
        }

        viewModelScope.launch {
            try {
                isSaving = true
                val token = dataStoreManager.getToken()
                
                // Create address request with all required data
                val addressRequest = AddressRequest(
                    id = null, // New address
                    addressLine1 = userInput,
                    addressLine2 = geoAddress,
                    latitude = currentLatitude!!,
                    longitude = currentLongitude!!,
                    cityId = currentCityId!!,
                    cityName = currentCityName!!
                )
                
                val response = apiService.saveAddress(token, addressRequest)
                
                // Check if save was successful
                saveSuccess = response.error != true
                
            } catch (e: Exception) {
                // Handle save error
                saveSuccess = false
            } finally {
                isSaving = false
            }
        }
    }
}