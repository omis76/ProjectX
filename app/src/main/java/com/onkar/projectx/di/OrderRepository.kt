package com.onkar.projectx.di

import com.onkar.projectx.ApiClient
import com.onkar.projectx.data.DeliveryItem

object OrderRepository {
    suspend fun getDeliveriesForDate(token: String, date: String): List<DeliveryItem> {
        return ApiClient.homeApi.getDeliveriesForDate(token, date)
    }
}