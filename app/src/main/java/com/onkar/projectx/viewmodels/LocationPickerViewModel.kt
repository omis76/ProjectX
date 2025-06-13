package com.onkar.projectx.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationPickerViewModel : ViewModel() {

    var isSaving by mutableStateOf(false)
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    fun resetState() {
        saveSuccess = false
    }

    fun saveAddress(userInput: String, geoAddress: String, note: String) {
        if (isSaving) return  // Debounce

        viewModelScope.launch {
            isSaving = true
            delay(1000)  // Simulated network or DB delay
            saveSuccess = true
            isSaving = false
        }
    }
}