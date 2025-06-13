package com.onkar.projectx.data

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val dayOfWeek: String,
    val itemCount: Int,
    val isFirstOfMonth: Boolean,
    val monthName: String
)

data class CalendarUiState(
    val days: List<CalendarDay> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now().plusDays(1),
    val dayDeliveries: List<DeliveryItem> = emptyList(),
    val recommendedProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false
)