package com.onkar.projectx.data

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataManager @Inject constructor(
    private val appDataRepository: AppDataRepository
) {
    
    // Expose reactive data streams
    val profileData: StateFlow<CustomerProfileResponse?> = appDataRepository.profileData
    val productsData: StateFlow<CatalogResponse?> = appDataRepository.productsData
    val homeData: StateFlow<HomeResponse?> = appDataRepository.homeData
    val banners: StateFlow<List<Banner>?> = appDataRepository.banners

    // Profile-related methods
    fun getCurrentUser(): CustomerProfileResponse? {
        return appDataRepository.getCachedProfile()
    }

    fun getUserAddresses(): List<CustomerAddress>? {
        return appDataRepository.getUserAddresses()
    }

    fun hasUserAddresses(): Boolean {
        return appDataRepository.hasUserAddresses()
    }

    fun getDefaultAddress(): CustomerAddress? {
        return appDataRepository.getDefaultAddress()
    }

    // Product-related methods
    fun getAllProducts(): List<Product> {
        return appDataRepository.getCachedProducts()?.let { catalog ->
            catalog.categories.flatMap { category ->
                category.subcategories.flatMap { subcategory ->
                    subcategory.products
                }
            }
        } ?: emptyList()
    }

    fun getAllCategories(): List<CategoryModel> {
        return appDataRepository.getCachedProducts()?.categories ?: emptyList()
    }

    fun getProductById(productId: String): Product? {
        return getAllProducts().find { it.id == productId }
    }

    fun getCategoryById(categoryId: String): CategoryModel? {
        return getAllCategories().find { it.id == categoryId }
    }

    fun getSubcategoryById(subcategoryId: String): SubcategoryModel? {
        return getAllCategories().flatMap { it.subcategories }
            .find { it.id == subcategoryId }
    }

    // Home-related methods
    fun getHomeSections(): List<HomeSection> {
        return appDataRepository.getCachedHome()?.items ?: emptyList()
    }

    fun getHomeBanners(): List<Banner> {
        return appDataRepository.getCachedBanners() ?: emptyList()
    }

    // Cache management
    fun isCacheValid(): Boolean {
        return appDataRepository.isCacheValid()
    }

    fun hasValidData(): Boolean {
        return appDataRepository.hasValidData()
    }

    fun clearData() {
        appDataRepository.clearData()
    }

    // Helper methods for common use cases
    fun isUserLoggedInWithAddresses(): Boolean {
        return getCurrentUser() != null && hasUserAddresses()
    }

    fun shouldRedirectToProfile(): Boolean {
        return getCurrentUser() != null && !hasUserAddresses()
    }

    fun shouldRedirectToLogin(): Boolean {
        return getCurrentUser() == null
    }
}