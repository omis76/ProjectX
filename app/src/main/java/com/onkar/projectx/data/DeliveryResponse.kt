package com.onkar.projectx.data

data class DeliveryItem(
    val subscription: Int,
    val product: String,
    val quantity: Int,
    val status: String,
    val delivery_date: String
)