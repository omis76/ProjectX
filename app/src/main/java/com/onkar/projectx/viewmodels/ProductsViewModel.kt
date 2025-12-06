package com.onkar.projectx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.HomeApiService
import com.onkar.projectx.data.AppDataManager
import com.onkar.projectx.data.CategoryModel
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.Product
import com.onkar.projectx.data.SubcategoryModel
// import com.onkar.projectx.data.generateFakeCategoriesAndProducts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import android.util.Log
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val homeApiService: HomeApiService,
    private val appDataManager: AppDataManager
) : ViewModel() {
    private val _plpUiState = MutableStateFlow(PLPUiState())
    val plpUiState: StateFlow<PLPUiState> = _plpUiState

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    var productList = emptyList<Product>()
    var categoryList = emptyList<CategoryModel>()

    init {
        loadCachedData()
        observeDataChanges()
    }

    private fun loadCachedData() {
        // Load cached data immediately if available
        val cachedProducts = appDataManager.getAllProducts()
        val cachedCategories = appDataManager.getAllCategories()
        
        if (cachedProducts.isNotEmpty()) {
            productList = cachedProducts
            categoryList = cachedCategories
            _uiState.value = _uiState.value.copy(
                products = cachedProducts,
                categories = cachedCategories
            )
        }
    }

    private fun observeDataChanges() {
        // Observe changes to cached product data
        viewModelScope.launch {
            appDataManager.productsData.collectLatest { catalogResponse ->
                catalogResponse?.let { catalog ->
                    val flatProductList = mutableListOf<Product>()
                    catalog.categories.forEach { category ->
                        category.subcategories.forEach { subcategory ->
                            subcategory.products.forEach { product ->
                                product.subcategoryId = subcategory.id.toIntOrNull()
                                flatProductList.add(product)
                            }
                        }
                    }
                    
                    productList = flatProductList
                    categoryList = catalog.categories
                    _uiState.value = _uiState.value.copy(
                        products = flatProductList,
                        categories = catalog.categories,
                        error = null
                    )
                }
            }
        }
    }

    fun getCatalog(cartViewModel: CartViewModel) {
        Log.d("ProductsViewModel", "getCatalog called")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val token = dataStoreManager.getToken()
            Log.d(
                "ProductsViewModel",
                "Token retrieved: ${token.take(20)}..."
            ) // Log first 20 chars for security

            // Check if token is valid (should be in "Bearer {token}" format)

            try {
                Log.d("ProductsViewModel", "Making API call to get catalog")
                val response = homeApiService.getCatalog(token, cityId = 1)
                Log.d(
                    "ProductsViewModel",
                    "API response received: ${response.categories.size} categories"
                )
                if (response.error.isNullOrEmpty()) {
                    // Transform nested products structure to flat list
                    val flatProductList = mutableListOf<Product>()
                    response.categories.forEach { category ->
                        category.subcategories.forEach { subcategory ->
                            subcategory.products.forEach { product ->
                                // Set the subcategoryId for each product
                                product.subcategoryId = subcategory.id.toIntOrNull()
                                flatProductList.add(product)
                            }
                        }
                    }

                    productList = flatProductList
                    categoryList = response.categories
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = flatProductList,
                        categories = response.categories, error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, error = response.error
                    )
                }

            } catch (e: Exception) {
                Log.e("ProductsViewModel", "Error fetching catalog", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.localizedMessage ?: "something went wrong"
                )
            }

        }
    }

    fun getProductById(productId: Int): Product? {
        return productList.find { it.id == productId.toString() }
    }

    fun getProductById(productId: String): Product? {
        return productList.find { it.id.toString() == productId }
    }

    fun getProductsBySubcategory(subcategoryId: Int): List<Product> {
        return productList.filter { it.subcategoryId == subcategoryId }
    }

    fun getProductsBySubcategory(subcategoryId: String): List<Product> {
        return productList.filter { it.subcategoryId?.toString() == subcategoryId }
    }

    fun getAllSubcategories(): List<SubcategoryModel> {
        return categoryList.flatMap { it.subcategories }
    }

    fun getCategoriesWithSubcategories(): List<CategoryModel> {
        return categoryList
    }

    // Additional Methods for Future Use

    fun getProductsByCategory(categoryId: Int): List<Product> {
        val subcategoryIds = categoryList
            .find { it.id == categoryId.toString() }
            ?.subcategories
            ?.map { it.id }
            ?: emptyList()

        return productList.filter { it.subcategoryId.toString() in subcategoryIds }
    }

    fun getProductsByCategory(categoryId: String): List<Product> {
        val subcategoryIds = categoryList
            .find { it.id.toString() == categoryId }
            ?.subcategories
            ?.map { it.id }
            ?: emptyList()

        return productList.filter { it.subcategoryId.toString() in subcategoryIds }
    }

    fun searchProductsByName(query: String): List<Product> {
        return productList.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun searchProducts(query: String): List<Product> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) return productList

        val subcategoryMap = categoryList
            .flatMap { cat ->
                cat.subcategories.map { it.id to it.name }
            }
            .toMap()
        return productList.filter { product ->
            val productName = product.name.lowercase()
            val subcategoryName =
                subcategoryMap[product.subcategoryId.toString()]?.lowercase() ?: ""
            val categoryName = categoryList.firstOrNull { cat ->
                cat.subcategories.any { it.id == product.subcategoryId.toString() }
            }?.name?.lowercase() ?: ""

            fuzzyMatch(productName, normalizedQuery) ||
                    fuzzyMatch(subcategoryName, normalizedQuery) ||
                    fuzzyMatch(categoryName, normalizedQuery)
        }
    }

    private fun fuzzyMatch(text: String, query: String): Boolean {
        if (text.contains(query)) return true
        val distance = levenshtein(text, query)
        return distance <= 2
    }

    private fun levenshtein(lhs: String, rhs: String): Int {
        val m = lhs.length
        val n = rhs.length
        if (m == 0) return n
        if (n == 0) return m
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) {
            val c1 = lhs[i - 1]
            for (j in 1..n) {
                val c2 = rhs[j - 1]
                val cost = if (c1 == c2) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[m][n]
    }

    fun getRecommendedProducts(count: Int = 5): List<Product> {
        return productList.shuffled().take(count)
    }

    fun loadPLPByCategory(categoryId: Int) {
        val category = categoryList.find { it.id == categoryId.toString() } ?: return
        val subcategories = category.subcategories
        val firstSubcategory = subcategories.firstOrNull()
        val products = firstSubcategory?.let { getProductsBySubcategory(it.id) } ?: emptyList()

        _plpUiState.value = PLPUiState(
            category = category,
            selectedSubcategory = firstSubcategory,
            subcategories = subcategories,
            products = productList
        )
    }

    fun loadPLPByCategory(categoryId: String) {
        val category = categoryList.find { it.id.toString() == categoryId } ?: return
        val subcategories = category.subcategories
        val firstSubcategory = subcategories.firstOrNull()

        _plpUiState.value = PLPUiState(
            category = category,
            selectedSubcategory = firstSubcategory,
            subcategories = subcategories,
            products = productList
        )
    }

    fun loadPLPBySubcategory(subcategoryId: Int) {
        val parentCategory = categoryList.find { cat ->
            cat.subcategories.any { it.id == subcategoryId.toString() }
        } ?: return

        val subcategories = parentCategory.subcategories
        val selectedSubcategory = subcategories.find { it.id == subcategoryId.toString() }

        _plpUiState.value = PLPUiState(
            category = parentCategory,
            selectedSubcategory = selectedSubcategory,
            subcategories = subcategories,
            products = productList
        )
    }

    fun loadPLPBySubcategory(subcategoryId: String) {
        val subcategoryIdInt = subcategoryId.toIntOrNull()
        val parentCategory = categoryList.find { cat ->
            cat.subcategories.any { it.id.toString() == subcategoryId }
        } ?: return

        val subcategories = parentCategory.subcategories
        val selectedSubcategory = subcategories.find { it.id.toString() == subcategoryId }

        _plpUiState.value = PLPUiState(
            category = parentCategory,
            selectedSubcategory = selectedSubcategory,
            subcategories = subcategories,
            products = productList
        )
    }

    fun onSubcategorySelected(subcategory: SubcategoryModel) {
        _plpUiState.value = _plpUiState.value.copy(
            selectedSubcategory = subcategory,
            products = productList
        )
    }
}

data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<CategoryModel> = emptyList(),
    val error: String? = null
)

data class PLPUiState(
    val category: CategoryModel? = null,
    val selectedSubcategory: SubcategoryModel? = null,
    val subcategories: List<SubcategoryModel> = emptyList(),
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
