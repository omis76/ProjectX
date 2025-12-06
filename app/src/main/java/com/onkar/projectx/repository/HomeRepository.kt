package com.onkar.projectx.repository

import com.onkar.projectx.data.CatalogResponse
import com.onkar.projectx.data.HomeResponse
import com.onkar.projectx.data.Resource
import com.onkar.projectx.data.UserDashboardResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for home-related data operations.
 * Provides abstraction between ViewModels and data sources.
 */
interface HomeRepository {
    
    /**
     * Fetches home dashboard data with caching and offline support
     */
    fun getHomeDashboard(): Flow<Resource<HomeResponse>>
    
    /**
     * Fetches user dashboard data
     */
    fun getUserDashboard(): Flow<Resource<UserDashboardResponse>>
    
    /**
     * Fetches catalog data with category and product information
     */
    fun getCatalog(): Flow<Resource<CatalogResponse>>
    
    
    /**
     * Clears cached data (useful for refresh operations)
     */
    suspend fun clearCache()
    
    /**
     * Forces a refresh from network (ignores cache)
     */
    fun refreshHomeDashboard(): Flow<Resource<HomeResponse>>
}