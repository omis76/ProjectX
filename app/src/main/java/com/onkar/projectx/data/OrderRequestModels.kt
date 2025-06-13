package com.onkar.projectx.data

data class OneTimeOrderRequest(
    val product: String,
    val quantity: Int,
    val delivery_date: String
)

data class SubscriptionRequest(
    val product: Int,
    val quantity: Int,
    val order_type: String, // "one_time" or "subscrption"
    val start_date: String, // yyyy-MM-dd
    val end_date: String,   // yyyy-MM-dd
    val days_of_week: List<String>
)