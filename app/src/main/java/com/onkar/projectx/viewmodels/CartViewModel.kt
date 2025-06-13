package com.onkar.projectx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.data.DeliveryItem
import com.onkar.projectx.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CartViewModel : ViewModel() {

    // Internal cart items: Map<ProductId, Product>
    private val _cartItems = MutableStateFlow<Map<String, Product>>(emptyMap())
    val cartItems: StateFlow<Map<String, Product>> get() = _cartItems

    private val _billState = MutableStateFlow(BillUIState())
    val billState: StateFlow<BillUIState> get() = _billState

    // Add or update product in cart
    fun addProduct(product: Product) {
        _cartItems.value = _cartItems.value.toMutableMap().apply {
            val updatedProduct = product.copy(quantity = (product.quantity ?: 0) + 1)
            put(product.id, updatedProduct)
        }
        updateBillState()
    }

    // Remove product or decrease quantity
    fun removeProduct(product: Product) {
        _cartItems.value = _cartItems.value.toMutableMap().apply {
            val current = this[product.id]
            if (current != null) {
                if ((current.quantity ?: 0) > 1) {
                    put(product.id, current.copy(quantity = (current.quantity ?: 1) - 1))
                } else {
                    remove(product.id)
                }
            }
        }
        updateBillState()
    }

    // Get quantity of a specific product (default 0 if not present)
    fun getQuantity(productId: String): Int {
        return _cartItems.value[productId]?.quantity ?: 0
    }

    fun getProduct(productId: String): Product? {
        return _cartItems.value[productId]
    }

    fun getCartProductsWithQuantity(): List<Product> {
        return _cartItems.value.values.toList()
    }

    // Clear the whole cart
    fun clearCart() {
        _cartItems.value = emptyMap()
        updateBillState()
    }

    private fun updateBillState() {
        val items = _cartItems.value.values

        val mrpTotal = items.sumOf {
            val mrp = it.mrp
            mrp * (it.quantity ?: 1)
        }

        val sellingTotal = items.sumOf {
            (it.sellingPrice ?: 0.0) * (it.quantity ?: 1)
        }

        val baseAmount = sellingTotal

        val deliveryCharge = when {
            baseAmount <= 100 -> 10.0
            baseAmount <= 500 -> 20.0
            else -> 25.0
        }

        val handlingCharge = when {
            baseAmount <= 100 -> 10.0
            baseAmount <= 500 -> 20.0
            else -> 25.0
        }

        val totalToPay = baseAmount + deliveryCharge + handlingCharge

        _billState.value = BillUIState(
            mrpTotal = mrpTotal,
            sellingTotal = sellingTotal,
            deliveryCharge = deliveryCharge,
            handlingCharge = handlingCharge,
            totalToPay = totalToPay
        )
    }

    fun preloadDeliveryQuantities(deliveryItems: List<DeliveryItem>, products: List<Product>) {
        val updatedCart = _cartItems.value.toMutableMap()

        deliveryItems.forEach { delivery ->
            val productId = delivery.product
            if (!updatedCart.containsKey(productId)) {
                val matchingProduct = products.find { it.id == productId }
                if (matchingProduct != null) {
                    updatedCart[productId] = matchingProduct.copy(quantity = delivery.quantity)
                }
            }
        }

        _cartItems.value = updatedCart
        updateBillState()
    }

    fun syncTomorrowDeliveries(
        token: String,
        products: List<Product>,
        fetchDeliveries: suspend (String, String) -> List<DeliveryItem>
    ) {
        viewModelScope.launch {
            try {
                val tomorrow = LocalDate.now().plusDays(1).toString()
                val deliveries = fetchDeliveries(token, tomorrow)
                preloadDeliveryQuantities(deliveries, products)
            } catch (e: Exception) {
                // Optional: handle error
            }
        }
    }
}

data class BillUIState(
    val mrpTotal: Double = 0.0,
    val sellingTotal: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val handlingCharge: Double = 0.0,
    val totalToPay: Double = 0.0
)