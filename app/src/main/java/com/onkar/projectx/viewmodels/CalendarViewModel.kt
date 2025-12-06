package com.onkar.projectx.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.HomeApiService
import com.onkar.projectx.data.CalendarDay
import com.onkar.projectx.data.CalendarUiState
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.DayStatus
import com.onkar.projectx.data.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val apiService: HomeApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        generateCalendar()
        fetchDaysStatuses()
        onDateSelected(LocalDate.now().plusDays(1))
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
                    orderStatus = null, // Will be populated by fetchDaysStatuses
                    itemCount = 0, // Will be calculated from order data
                    isFirstOfMonth = date.dayOfMonth == 1,
                    monthName = date.month.name.take(3)
                )
            )
        }
        _uiState.update { it.copy(days = days, selectedDate = today) }
    }

    private fun fetchDaysStatuses() {
        viewModelScope.launch {
            try {
                val token = dataStoreManager.getToken()
                Log.d("CalendarViewModel", "Fetching days statuses")
                val response = apiService.getDaysStatuses(token)
                
                // Update calendar days with status information
                val updatedDays = _uiState.value.days.map { calendarDay ->
                    val dayStatus = response.days.find { 
                        it.date == calendarDay.date.toString() 
                    }
                    calendarDay.copy(
                        orderStatus = dayStatus?.status,
                        itemCount = if (dayStatus?.status == "no_subscription") 0 else 1
                    )
                }
                
                _uiState.update { 
                    it.copy(days = updatedDays)
                }
                Log.d("CalendarViewModel", "Updated ${updatedDays.size} days with status")
                
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "Failed to fetch days statuses", e)
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update {
            it.copy(selectedDate = date, isLoadingDayOrder = true)
        }

        viewModelScope.launch {
            val token = dataStoreManager.getToken()
            try {
                val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                Log.d("CalendarViewModel", "Fetching day order for: $dateString")
                
                val dayOrderResponse = apiService.getDayOrder(token, dateString)
                
                _uiState.update {
                    it.copy(
                        dayOrder = dayOrderResponse,
                        dayDeliveries = emptyList(), // Legacy deliveries removed
                        isLoadingDayOrder = false
                    )
                }
                
                Log.d("CalendarViewModel", "Fetched day order with ${dayOrderResponse.products.size} products")
                
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "Failed to fetch day order", e)
                _uiState.update {
                    it.copy(isLoadingDayOrder = false)
                }
            }
        }
    }

    fun refreshDaysStatuses() {
        fetchDaysStatuses()
    }
}