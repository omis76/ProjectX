package com.onkar.projectx.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.HomeApiService
import com.onkar.projectx.data.AppDataRepository
import com.onkar.projectx.data.CatalogResponse
import com.onkar.projectx.data.CustomerProfileResponse
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.DashboardResponse
import com.onkar.projectx.data.HomeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppInitViewModel @Inject constructor(
    private val apiService: HomeApiService,
    private val dataStoreManager: DataStoreManager,
    private val appDataRepository: AppDataRepository
) : ViewModel() {

    private val _initState = MutableStateFlow(AppInitState())
    val initState: StateFlow<AppInitState> = _initState

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                _initState.update { it.copy(isLoading = true) }
                Log.d("AppInit", "Starting app initialization...")

                // Check if user is logged in first
                val isLoggedIn = dataStoreManager.isLoggedIn()
                val token = dataStoreManager.getToken()

                if (!isLoggedIn || token.isEmpty() || token == "null") {
                    Log.d("AppInit", "User not logged in, redirecting to login")
                    appDataRepository.clearData()
                    _initState.update { 
                        it.copy(
                            isLoading = false,
                            isInitialized = true,
                            shouldShowLogin = true
                        )
                    }
                    return@launch
                }

                // Check if we have valid cached data
                if (appDataRepository.hasValidData()) {
                    Log.d("AppInit", "Using cached app data")
                    val profileData = appDataRepository.getCachedProfile()
                    val shouldShowHome = appDataRepository.hasUserAddresses()
                    
                    _initState.update {
                        it.copy(
                            isLoading = false,
                            profileData = profileData,
                            productsData = appDataRepository.getCachedProducts(),
                            homeData = appDataRepository.getCachedHome(),
                            isInitialized = true,
                            shouldShowHome = shouldShowHome,
                            shouldShowProfile = profileData != null && !shouldShowHome
                        )
                    }
                    return@launch
                }

                Log.d("AppInit", "User logged in, loading app data in parallel...")

                // Load all critical data in parallel
                val profileDeferred = async { loadProfile() }
                val productsDeferred = async { loadProducts() }
                val homeDeferred = async { loadHome() }

                // Wait for all API calls to complete
                val profileData = profileDeferred.await()
                val productsData = productsDeferred.await()
                val homeData = homeDeferred.await()

                // Store data in repository for caching
                appDataRepository.updateAppData(profileData, productsData, homeData)

                // Analyze profile data to determine navigation
                val shouldShowHome = profileData?.let { profile ->
                    !profile.addresses.isNullOrEmpty()
                } ?: false

                _initState.update {
                    it.copy(
                        isLoading = false,
                        profileData = profileData,
                        productsData = productsData,
                        homeData = homeData,
                        isInitialized = true,
                        shouldShowHome = shouldShowHome,
                        shouldShowProfile = profileData != null && !shouldShowHome
                    )
                }

                Log.d("AppInit", "App initialization completed successfully")
                Log.d("AppInit", "Profile: ${profileData != null}, Products: ${productsData != null}, Home: ${homeData != null}")
                Log.d("AppInit", "Should show home: $shouldShowHome")

            } catch (e: Exception) {
                Log.e("AppInit", "App initialization failed", e)
                _initState.update {
                    it.copy(
                        isLoading = false,
                        errors = it.errors + e.localizedMessage.orEmpty(),
                        isInitialized = true,
                        shouldShowHome = false
                    )
                }
            }
        }
    }

    private suspend fun loadProfile(): CustomerProfileResponse? {
        return try {
            val token = dataStoreManager.getToken()
            Log.d("AppInit", "Loading profile...")
            val profile = apiService.getCustomerProfile(token)
            Log.d("AppInit", "Profile loaded: ${profile.addresses?.size} addresses")
            profile
        } catch (e: Exception) {
            Log.e("AppInit", "Failed to load profile", e)
            null
        }
    }

    private suspend fun loadProducts(): CatalogResponse? {
        return try {
            val token = dataStoreManager.getToken()
            Log.d("AppInit", "Loading products catalog...")
            val catalog = apiService.getCatalog(token)
            Log.d("AppInit", "Products loaded: ${catalog.categories.size} categories")
            catalog
        } catch (e: Exception) {
            Log.e("AppInit", "Failed to load products", e)
            null
        }
    }

    private suspend fun loadHome(): HomeResponse? {
        return try {
            val token = dataStoreManager.getToken()
            Log.d("AppInit", "Loading home dashboard...")
            val dashboardResponse = apiService.getDashboardHome(token)
            
            // Convert DashboardResponse to HomeResponse (reuse existing logic from HomeRepository)
            val homeResponse = convertDashboardToHome(dashboardResponse)
            Log.d("AppInit", "Home loaded: ${homeResponse.items.size} sections")
            homeResponse
        } catch (e: Exception) {
            Log.e("AppInit", "Failed to load home", e)
            null
        }
    }

    private fun convertDashboardToHome(dashboard: DashboardResponse): HomeResponse {
        // Simple conversion - can be enhanced later
        val banners = dashboard.items?.filter { it.type == "FULL_BANNER" }?.map { section ->
            com.onkar.projectx.data.Banner(
                title = section.title,
                subtitle = section.subtitle ?: "",
                image_url = "https://assets.epicurious.com/photos/62f16ed5fe4be95d5a460eed/1:1/w_1920,c_limit/RoastChicken_RECIPE_080420_37993.jpg"
            )
        } ?: emptyList()

        val sections = dashboard.items?.filter { it.type != "FULL_BANNER" }?.map { section ->
            com.onkar.projectx.data.HomeSection(
                title = section.title,
                subtitle = section.subtitle ?: "",
                layoutType = when (section.layoutType) {
                    "GRID" -> com.onkar.projectx.data.LayoutType.GRID
                    else -> com.onkar.projectx.data.LayoutType.HORIZONTAL
                },
                items = emptyList(), // Will be populated when products are mapped
                spanCount = section.spanCount ?: 4
            )
        } ?: emptyList()

        return HomeResponse(
            banners = banners,
            items = sections
        )
    }

    fun retry() {
        Log.d("AppInit", "Retrying app initialization...")
        initializeApp()
    }

    fun clearErrors() {
        _initState.update { it.copy(errors = emptyList()) }
    }
}

data class AppInitState(
    val isLoading: Boolean = true,
    val profileData: CustomerProfileResponse? = null,
    val productsData: CatalogResponse? = null,
    val homeData: HomeResponse? = null,
    val errors: List<String> = emptyList(),
    val isInitialized: Boolean = false,
    val shouldShowHome: Boolean = false,
    val shouldShowLogin: Boolean = false,
    val shouldShowProfile: Boolean = false
)