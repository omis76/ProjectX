package com.onkar.projectx.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.data.AddressState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    private val _uiState = mutableStateOf(AddressState())
    val uiState: State<AddressState> = _uiState

    fun prefillAddress(address: String) {
        _uiState.value = _uiState.value.copy(address = address, isEditing = true)
    }

    fun updateAddress(newAddress: String) {
        _uiState.value = _uiState.value.copy(address = newAddress)
    }

    fun updateNote(newNote: String) {
        _uiState.value = _uiState.value.copy(note = newNote)
    }

    fun saveAddress() {
        _uiState.value = _uiState.value.copy(isSaving = true)
        viewModelScope.launch {
            delay(2000) // simulate saving
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    fun reset() {
        _uiState.value = AddressState()
    }
}