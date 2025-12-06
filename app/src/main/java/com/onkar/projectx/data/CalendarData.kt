package com.onkar.projectx.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

// Calendar UI Models
data class CalendarDay(
    val date: LocalDate,
    val dayOfWeek: String,
    val orderStatus: String? = null, // delivered, pending, packed, picked, not_delivered, no_subscription
    val itemCount: Int,
    val isFirstOfMonth: Boolean,
    val monthName: String
)

data class CalendarUiState(
    val days: List<CalendarDay> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now().plusDays(1),
    val dayDeliveries: List<DeliveryItem> = emptyList(),
    val dayOrder: DayOrderResponse? = null,
    val recommendedProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingDayOrder: Boolean = false
)

// API Response Models for Days Statuses
data class DayStatusResponse(
    val days: List<DayStatus>
)

data class DayStatus(
    val date: String,
    val status: String
)

// API Response Models for Day Order Details
data class DayOrderResponse(
    val date: String,
    val status: String,
    val products: List<OrderProduct>
)

data class OrderProduct(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("ordered_quantity")
    val orderedQuantity: Int,
    @SerializedName("delivered_quantity")
    val deliveredQuantity: Int,
    val price: Double,
    val amount: Double,
    val status: String
)