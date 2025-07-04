package com.onkar.projectx.repository

import com.onkar.projectx.HomeApiService
import com.onkar.projectx.data.CatalogResponse
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.DeliveryItem
import com.onkar.projectx.data.HomeResponse
import com.onkar.projectx.data.Resource
import com.onkar.projectx.data.UserDashboardResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of HomeRepository that handles API calls and caching
 */
@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val apiService: HomeApiService,
    private val dataStore: DataStoreManager
) : HomeRepository {

    // Simple in-memory cache for demo purposes
    // In production, consider using Room database or other persistent storage
    private val cache = ConcurrentHashMap<String, Any>()
    private val cacheTimestamps = ConcurrentHashMap<String, Long>()
    
    // Cache validity duration (5 minutes)
    private val cacheValidityDuration = 5 * 60 * 1000L
    
    private suspend fun getAuthToken(): String {
        return dataStore.getToken()
    }
    
    private fun isCacheValid(key: String): Boolean {
        val timestamp = cacheTimestamps[key] ?: return false
        return System.currentTimeMillis() - timestamp < cacheValidityDuration
    }
    
    private fun setCacheData(key: String, data: Any) {
        cache[key] = data
        cacheTimestamps[key] = System.currentTimeMillis()
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun <T> getCacheData(key: String): T? {
        return if (isCacheValid(key)) {
            cache[key] as? T
        } else {
            cache.remove(key)
            cacheTimestamps.remove(key)
            null
        }
    }
    
    override fun getHomeDashboard(): Flow<Resource<HomeResponse>> = flow {
        try {
            // Emit loading state with cached data if available
            val cachedData = getCacheData<HomeResponse>(CACHE_KEY_HOME_DASHBOARD)
            emit(Resource.Loading(cachedData))
            
            // Fetch from network
            val token = getAuthToken()
            val response = apiService.getHomeData(token)
            
            // Cache the response
            setCacheData(CACHE_KEY_HOME_DASHBOARD, response)
            
            // Emit success
            emit(Resource.Success(response))
            
        } catch (e: IOException) {
            // Network error - emit cached data if available
            val cachedData = getCacheData<HomeResponse>(CACHE_KEY_HOME_DASHBOARD)
            if (cachedData != null) {
                emit(Resource.Error("Network error. Showing cached data.", cachedData))
            } else {
                emit(Resource.Error("Network error. Please check your connection."))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }
    
    override fun getUserDashboard(): Flow<Resource<UserDashboardResponse>> = flow {
        try {
            // Emit loading state with cached data if available
            val cachedData = getCacheData<UserDashboardResponse>(CACHE_KEY_USER_DASHBOARD)
            emit(Resource.Loading(cachedData))
            
            // Fetch from network
            val token = getAuthToken()
            val response = apiService.getUserDashboard(token)
            
            // Cache the response
            setCacheData(CACHE_KEY_USER_DASHBOARD, response)
            
            // Emit success
            emit(Resource.Success(response))
            
        } catch (e: IOException) {
            // Network error - emit cached data if available
            val cachedData = getCacheData<UserDashboardResponse>(CACHE_KEY_USER_DASHBOARD)
            if (cachedData != null) {
                emit(Resource.Error("Network error. Showing cached data.", cachedData))
            } else {
                emit(Resource.Error("Network error. Please check your connection."))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }
    
    override fun getCatalog(): Flow<Resource<CatalogResponse>> = flow {
        try {
            // Emit loading state with cached data if available
            val cachedData = getCacheData<CatalogResponse>(CACHE_KEY_CATALOG)
            emit(Resource.Loading(cachedData))
            
            // Fetch from network
            val token = getAuthToken()
            val response = apiService.getCatalog(token)
            
            // Cache the response
            setCacheData(CACHE_KEY_CATALOG, response)
            
            // Emit success
            emit(Resource.Success(response))
            
        } catch (e: IOException) {
            // Network error - emit cached data if available
            val cachedData = getCacheData<CatalogResponse>(CACHE_KEY_CATALOG)
            if (cachedData != null) {
                emit(Resource.Error("Network error. Showing cached data.", cachedData))
            } else {
                emit(Resource.Error("Network error. Please check your connection."))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }
    
    override fun getDeliveriesForDate(date: String): Flow<Resource<List<DeliveryItem>>> = flow {
        try {
            val cacheKey = "${CACHE_KEY_DELIVERIES}_$date"
            
            // Emit loading state with cached data if available
            val cachedData = getCacheData<List<DeliveryItem>>(cacheKey)
            emit(Resource.Loading(cachedData))
            
            // Fetch from network
            val token = getAuthToken()
            val response = apiService.getDeliveriesForDate(token, date)
            
            // Cache the response
            setCacheData(cacheKey, response)
            
            // Emit success
            emit(Resource.Success(response))
            
        } catch (e: IOException) {
            // Network error - emit cached data if available
            val cacheKey = "${CACHE_KEY_DELIVERIES}_$date"
            val cachedData = getCacheData<List<DeliveryItem>>(cacheKey)
            if (cachedData != null) {
                emit(Resource.Error("Network error. Showing cached data.", cachedData))
            } else {
                emit(Resource.Error("Network error. Please check your connection."))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }
    
    override suspend fun clearCache() {
        cache.clear()
        cacheTimestamps.clear()
    }
    
    override fun refreshHomeDashboard(): Flow<Resource<HomeResponse>> = flow {
        try {
            // Clear existing cache for this endpoint
            cache.remove(CACHE_KEY_HOME_DASHBOARD)
            cacheTimestamps.remove(CACHE_KEY_HOME_DASHBOARD)
            
            // Emit loading state
            emit(Resource.Loading())
            
            // Fetch fresh data from network
            val token = getAuthToken()
            val response = apiService.getHomeData(token)
            
            // Cache the fresh response
            setCacheData(CACHE_KEY_HOME_DASHBOARD, response)
            
            // Emit success
            emit(Resource.Success(response))
            
        } catch (e: IOException) {
            emit(Resource.Error("Network error. Please check your connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }
    
    companion object {
        private const val CACHE_KEY_HOME_DASHBOARD = "home_dashboard"
        private const val CACHE_KEY_USER_DASHBOARD = "user_dashboard"
        private const val CACHE_KEY_CATALOG = "catalog"
        private const val CACHE_KEY_DELIVERIES = "deliveries"
    }
}