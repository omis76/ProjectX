package com.onkar.projectx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.ApiClient
import com.onkar.projectx.data.CalendarDay
import com.onkar.projectx.data.CalendarUiState
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        generateCalendar()
        onDateSelected(LocalDate.now())
    }

    private fun generateCalendar() {
        val today = LocalDate.now().plusDays(1)
        val days = mutableListOf<CalendarDay>()
        for (i in -7..7) {
            val date = today.plusDays(i.toLong())
            days.add(
                CalendarDay(
                    date = date,
                    dayOfWeek = date.dayOfWeek.name.take(3), // FRI, SAT, etc.
                    itemCount = (1..5).random(),
                    isFirstOfMonth = date.dayOfMonth == 1,
                    monthName = date.month.name.take(3)
                )
            )
        }
        _uiState.update { it.copy(days = days, selectedDate = today) }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update {
            it.copy(selectedDate = date, isLoading = true)
        }

        viewModelScope.launch {
            val token = dataStoreManager.getToken()
            try {
                val response = ApiClient.homeApi.getDeliveriesForDate(
                    token = token,
                    date = date.toString()
                )
                _uiState.update {
                    it.copy(dayDeliveries = response, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}