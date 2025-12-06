package com.onkar.projectx.repository

import android.util.Log
import com.onkar.projectx.HomeApiService
import com.onkar.projectx.data.*
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
            
            // Fetch from network using new Dashboard API
            val token = getAuthToken()
            val dashboardResponse = apiService.getDashboardHome(token)
            
            // Also fetch catalog to map product/subcategory IDs
            val catalogResponse = try {
                apiService.getCatalog(token)
            } catch (e: Exception) {
                Log.e("HomeRepository", "Failed to fetch catalog: ${e.message}")
                null
            }
            
            // Convert DashboardResponse to HomeResponse
            val homeResponse = convertDashboardToHomeResponse(dashboardResponse, catalogResponse)
            
            // Cache the response
            setCacheData(CACHE_KEY_HOME_DASHBOARD, homeResponse)
            
            // Store banner items separately for future use
            storeBannerItems(dashboardResponse)
            
            // Emit success
            emit(Resource.Success(homeResponse))
            
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
    
    private fun convertDashboardToHomeResponse(
        dashboard: DashboardResponse,
        catalog: CatalogResponse?
    ): HomeResponse {
        val sections = mutableListOf<HomeSection>()
        val bannerItems = mutableListOf<Banner>()
        
        // Create a map for quick product/subcategory lookup
        val productMap = catalog?.categories?.flatMap { it.subcategories }
            ?.flatMap { it.products }
            ?.associateBy { it.id.toIntOrNull() ?: -1 } ?: emptyMap()
            
        val subcategoryMap = catalog?.categories?.flatMap { it.subcategories }
            ?.associateBy { it.id.toIntOrNull() ?: -1 } ?: emptyMap()
        
        // Process dashboard items
        dashboard.items?.sortedBy { it.sequence }?.forEach { section ->
            when (section.type) {
                "FULL_BANNER" -> {
                    // Store banner items for future use
                    section.subItems?.forEach { subItem ->
                        // Use hardcoded URL for now since API URL might not be correct
                        bannerItems.add(
                            Banner(
                                title = subItem.genericText ?: "Featured Banner",
                                subtitle = "",
                                image_url = "https://assets.epicurious.com/photos/62f16ed5fe4be95d5a460eed/1:1/w_1920,c_limit/RoastChicken_RECIPE_080420_37993.jpg"
                            )
                        )
                    }
                }
                else -> {
                    // Convert to HomeSection
                    val homeItems = section.subItems?.sortedBy { it.sequence }?.mapNotNull { subItem ->
                        when {
                            subItem.productId != null -> {
                                // Map product ID to actual product
                                productMap[subItem.productId]?.let { product ->
                                    HomeItem(product = product, subcategory = null, generic = null)
                                }
                            }
                            subItem.subcategoryId != null -> {
                                // Map subcategory ID to actual subcategory
                                subcategoryMap[subItem.subcategoryId]?.let { subcategory ->
                                    HomeItem(product = null, subcategory = subcategory, generic = null)
                                }
                            }
                            !subItem.genericImageUrl.isNullOrEmpty() -> {
                                // Create generic item
                                HomeItem(
                                    product = null,
                                    subcategory = null,
                                    generic = GenericItem(
                                        title = subItem.genericText ?: "",
                                        image_url = subItem.genericImageUrl,
                                        action = subItem.genericAction,
                                        actionParameter = subItem.genericActionParameter
                                    )
                                )
                            }
                            else -> null
                        }
                    } ?: emptyList()
                    
                    if (homeItems.isNotEmpty()) {
                        sections.add(
                            HomeSection(
                                title = section.title,
                                subtitle = section.subtitle ?: "",
                                layoutType = when (section.layoutType) {
                                    "GRID" -> LayoutType.GRID
                                    else -> LayoutType.HORIZONTAL
                                },
                                items = homeItems,
                                spanCount = section.spanCount ?: 4
                            )
                        )
                    }
                }
            }
        }
        
        return HomeResponse(
            banners = bannerItems,
            items = sections
        )
    }
    
    private fun storeBannerItems(dashboard: DashboardResponse) {
        // Store FULL_BANNER items separately for future use
        val bannerSections = dashboard.items?.filter { it.type == "FULL_BANNER" } ?: emptyList()
        if (bannerSections.isNotEmpty()) {
            setCacheData(CACHE_KEY_BANNER_ITEMS, bannerSections)
            Log.d("HomeRepository", "Stored ${bannerSections.size} banner sections for future use")
        }
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
        private const val CACHE_KEY_BANNER_ITEMS = "banner_items"
    }
}