package com.onkar.projectx.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataRepository @Inject constructor() {

    private val _profileData = MutableStateFlow<CustomerProfileResponse?>(null)
    val profileData: StateFlow<CustomerProfileResponse?> = _profileData

    private val _productsData = MutableStateFlow<CatalogResponse?>(null)
    val productsData: StateFlow<CatalogResponse?> = _productsData

    private val _homeData = MutableStateFlow<HomeResponse?>(null)
    val homeData: StateFlow<HomeResponse?> = _homeData

    private val _banners = MutableStateFlow<List<Banner>?>(null)
    val banners: StateFlow<List<Banner>?> = _banners

    private var lastUpdatedTime: Long = 0
    private val cacheValidityDuration = 5 * 60 * 1000L // 5 minutes in milliseconds

    fun updateAppData(
        profile: CustomerProfileResponse?,
        products: CatalogResponse?,
        home: HomeResponse?
    ) {
        _profileData.value = profile
        _productsData.value = products
        _homeData.value = home
        
        // Extract banners from home data
        _banners.value = home?.banners
        
        lastUpdatedTime = System.currentTimeMillis()
    }

    fun clearData() {
        _profileData.value = null
        _productsData.value = null
        _homeData.value = null
        _banners.value = null
        lastUpdatedTime = 0
    }

    fun isCacheValid(): Boolean {
        return System.currentTimeMillis() - lastUpdatedTime < cacheValidityDuration
    }

    fun hasValidData(): Boolean {
        return isCacheValid() && 
               _profileData.value != null && 
               _productsData.value != null && 
               _homeData.value != null
    }

    // Convenience methods to access cached data
    fun getCachedProfile(): CustomerProfileResponse? = _profileData.value
    fun getCachedProducts(): CatalogResponse? = _productsData.value
    fun getCachedHome(): HomeResponse? = _homeData.value
    fun getCachedBanners(): List<Banner>? = _banners.value

    // Get user's addresses from cached profile
    fun getUserAddresses(): List<CustomerAddress>? {
        return _profileData.value?.addresses
    }

    // Check if user has addresses
    fun hasUserAddresses(): Boolean {
        return getUserAddresses()?.isNotEmpty() == true
    }

    // Get first address (nearest/default)
    fun getDefaultAddress(): CustomerAddress? {
        return getUserAddresses()?.firstOrNull()
    }
}