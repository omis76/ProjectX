package com.onkar.projectx.repository

import com.onkar.projectx.data.Product
import com.onkar.projectx.data.Resource
import com.onkar.projectx.data.SubscriptionRequest
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for product-related operations
 */
interface ProductRepository {
    
    /**
     * Search products by query string
     */
    fun searchProducts(query: String): Flow<Resource<List<Product>>>
    
    /**
     * Get products by subcategory
     */
    fun getProductsBySubcategory(subcategoryId: String): Flow<Resource<List<Product>>>
    
    /**
     * Get product by ID
     */
    fun getProductById(productId: String): Flow<Resource<Product?>>
    
    /**
     * Create order or subscription
     */
    fun createOrderOrSubscription(request: SubscriptionRequest): Flow<Resource<Unit>>
    
    /**
     * Get all products (cached)
     */
    fun getAllProducts(): Flow<Resource<List<Product>>>
    
    /**
     * Refresh product data from network
     */
    fun refreshProducts(): Flow<Resource<List<Product>>>
}