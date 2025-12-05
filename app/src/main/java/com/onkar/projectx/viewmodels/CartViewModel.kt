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

    // Date-based cart items: Map<Date, Map<ProductId, CartItem>>
    private val _dateBasedCartItems = MutableStateFlow<Map<String, Map<String, CartItem>>>(emptyMap())
    val dateBasedCartItems: StateFlow<Map<String, Map<String, CartItem>>> get() = _dateBasedCartItems
    
    // Legacy cart items for backward compatibility (defaults to tomorrow)
    private val _cartItems = MutableStateFlow<Map<String, Product>>(emptyMap())
    val cartItems: StateFlow<Map<String, Product>> get() = _cartItems

    // Selected date for calendar context
    private val _selectedDate = MutableStateFlow(LocalDate.now().plusDays(1).toString())
    val selectedDate: StateFlow<String> get() = _selectedDate

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
            val mrp = it.mrp.toDouble()
            mrp * (it.quantity ?: 1)
        }

        val sellingTotal = items.sumOf {
            (it.sellingPrice.toDouble() ?: 0.0) * (it.quantity ?: 1)
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

    // ===== DATE-SPECIFIC CART OPERATIONS =====
    
    // Set selected date for calendar context
    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }
    
    // Add product for specific date
    fun addProductForDate(product: Product, deliveryDate: String? = null) {
        val targetDate = deliveryDate ?: LocalDate.now().plusDays(1).toString()
        
        val currentDateCart = _dateBasedCartItems.value[targetDate]?.toMutableMap() ?: mutableMapOf()
        val existingItem = currentDateCart[product.id]
        
        val newCartItem = if (existingItem != null) {
            existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            CartItem(
                product = product,
                quantity = 1,
                deliveryDate = targetDate
            )
        }
        
        currentDateCart[product.id] = newCartItem
        
        val updatedCartItems = _dateBasedCartItems.value.toMutableMap()
        updatedCartItems[targetDate] = currentDateCart
        _dateBasedCartItems.value = updatedCartItems
        
        // Also update legacy cart if it's for tomorrow (backward compatibility)
        if (targetDate == LocalDate.now().plusDays(1).toString()) {
            val updatedProduct = product.copy(quantity = newCartItem.quantity)
            _cartItems.value = _cartItems.value.toMutableMap().apply {
                put(product.id, updatedProduct)
            }
        }
        
        updateBillStateForDate(targetDate)
    }
    
    // Remove product for specific date
    fun removeProductForDate(product: Product, deliveryDate: String? = null) {
        val targetDate = deliveryDate ?: LocalDate.now().plusDays(1).toString()
        
        val currentDateCart = _dateBasedCartItems.value[targetDate]?.toMutableMap() ?: return
        val existingItem = currentDateCart[product.id] ?: return
        
        if (existingItem.quantity > 1) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity - 1)
            currentDateCart[product.id] = updatedItem
            
            // Update legacy cart for tomorrow
            if (targetDate == LocalDate.now().plusDays(1).toString()) {
                val updatedProduct = product.copy(quantity = updatedItem.quantity)
                _cartItems.value = _cartItems.value.toMutableMap().apply {
                    put(product.id, updatedProduct)
                }
            }
        } else {
            currentDateCart.remove(product.id)
            
            // Update legacy cart for tomorrow
            if (targetDate == LocalDate.now().plusDays(1).toString()) {
                _cartItems.value = _cartItems.value.toMutableMap().apply {
                    remove(product.id)
                }
            }
        }
        
        val updatedCartItems = _dateBasedCartItems.value.toMutableMap()
        if (currentDateCart.isEmpty()) {
            updatedCartItems.remove(targetDate)
        } else {
            updatedCartItems[targetDate] = currentDateCart
        }
        _dateBasedCartItems.value = updatedCartItems
        
        updateBillStateForDate(targetDate)
    }
    
    // Get quantity for specific product and date
    fun getQuantityForDate(productId: String, deliveryDate: String? = null): Int {
        val targetDate = deliveryDate ?: LocalDate.now().plusDays(1).toString()
        return _dateBasedCartItems.value[targetDate]?.get(productId)?.quantity ?: 0
    }
    
    // Get all cart items for specific date
    fun getCartItemsForDate(deliveryDate: String): Map<String, CartItem> {
        return _dateBasedCartItems.value[deliveryDate] ?: emptyMap()
    }
    
    // Get cart products for specific date (for UI compatibility)
    fun getCartProductsForDate(deliveryDate: String): List<Product> {
        return getCartItemsForDate(deliveryDate).values.map { cartItem ->
            cartItem.product.copy(quantity = cartItem.quantity)
        }
    }
    
    // Clear cart for specific date
    fun clearCartForDate(deliveryDate: String) {
        val updatedCartItems = _dateBasedCartItems.value.toMutableMap()
        updatedCartItems.remove(deliveryDate)
        _dateBasedCartItems.value = updatedCartItems
        
        // Also clear legacy cart if it's tomorrow
        if (deliveryDate == LocalDate.now().plusDays(1).toString()) {
            _cartItems.value = emptyMap()
        }
        
        updateBillStateForDate(deliveryDate)
    }
    
    // Update bill calculation for specific date
    private fun updateBillStateForDate(deliveryDate: String) {
        val items = getCartItemsForDate(deliveryDate).values
        
        val mrpTotal = items.sumOf { cartItem ->
            val mrp = cartItem.product.mrp.toDouble()
            mrp * cartItem.quantity
        }
        
        val sellingTotal = items.sumOf { cartItem ->
            (cartItem.product.sellingPrice.toDouble() ?: 0.0) * cartItem.quantity
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
        
        // Also update legacy cart bill if it's tomorrow
        if (deliveryDate == LocalDate.now().plusDays(1).toString()) {
            updateBillState()
        }
    }
}

data class CartItem(
    val product: Product,
    val quantity: Int,
    val deliveryDate: String
)

data class BillUIState(
    val mrpTotal: Double = 0.0,
    val sellingTotal: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val handlingCharge: Double = 0.0,
    val totalToPay: Double = 0.0
)