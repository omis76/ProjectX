package com.onkar.projectx

import com.onkar.projectx.data.DeliveryItem
import com.onkar.projectx.data.Product
import java.time.LocalDate

fun mergeTodayDeliveriesWithCart(
    deliveries: List<DeliveryItem>,
    cartProducts: List<Product>,
    isTomorrow: Boolean
): List<DeliveryItem> {
    if (!isTomorrow) return deliveries

    // Add cart items as pseudo-deliveries if not already scheduled
    val deliveryProductIds = deliveries.map { it.product }.toSet()
    val extraCartDeliveries = cartProducts
        .filter { it.id.toIntOrNull() != null && !deliveryProductIds.contains(it.id) }
        .map {
            DeliveryItem(
                subscription = -1,
                product = it.id,
                quantity = it.quantity ?: 1,
                status = "in_cart",
                delivery_date = LocalDate.now().toString()
            )
        }

    return deliveries + extraCartDeliveries
}