package com.onkar.projectx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.ApiClient
import com.onkar.projectx.data.CategoryModel
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.Product
import com.onkar.projectx.data.SubcategoryModel
import com.onkar.projectx.data.generateFakeCategoriesAndProducts
import com.onkar.projectx.di.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductsViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel() {
    private val _plpUiState = MutableStateFlow(PLPUiState())
    val plpUiState: StateFlow<PLPUiState> = _plpUiState

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    var productList = emptyList<Product>()
    var categoryList = emptyList<CategoryModel>()


    fun getCatalog(cartViewModel: CartViewModel) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val token = dataStoreManager.getToken()
            if (token.isEmpty() || token.equals("Token")) {

            } else {
                try {
                    val response = ApiClient.homeApi.getCatalog(token)
                    if (response.error.isNullOrEmpty()) {
                        productList = response.products
                        categoryList = response.categories
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            products = response.products,
                            categories = response.categories, error = null
                        )

                        cartViewModel.syncTomorrowDeliveries(
                            token = token,
                            products = response.products,
                            fetchDeliveries = OrderRepository::getDeliveriesForDate
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, error = response.error
                        )
                    }

                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, error = e.localizedMessage ?: "something went wrong"
                    )
                }
            }
        }
    }

    fun getProductById(productId: String): Product? {
        return productList.find { it.id == productId }
    }

    fun getProductsBySubcategory(subcategoryId: String): List<Product> {
        return productList.filter { it.subcategoryId == subcategoryId }
    }

    fun getAllSubcategories(): List<SubcategoryModel> {
        return categoryList.flatMap { it.subcategories }
    }

    fun getCategoriesWithSubcategories(): List<CategoryModel> {
        return categoryList
    }

    // Additional Methods for Future Use

    fun getProductsByCategory(categoryId: String): List<Product> {
        val subcategoryIds = categoryList
            .find { it.id == categoryId }
            ?.subcategories
            ?.map { it.id }
            ?: emptyList()

        return productList.filter { it.subcategoryId in subcategoryIds }
    }

    fun searchProductsByName(query: String): List<Product> {
        return productList.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun getRecommendedProducts(count: Int = 5): List<Product> {
        return productList.shuffled().take(count)
    }

    fun loadPLPByCategory(categoryId: String) {
        val category = categoryList.find { it.id == categoryId } ?: return
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

    fun loadPLPBySubcategory(subcategoryId: String) {
        val parentCategory = categoryList.find { cat ->
            cat.subcategories.any { it.id == subcategoryId }
        } ?: return

        val subcategories = parentCategory.subcategories
        val selectedSubcategory = subcategories.find { it.id == subcategoryId }
        val products = getProductsBySubcategory(subcategoryId)

        _plpUiState.value = PLPUiState(
            category = parentCategory,
            selectedSubcategory = selectedSubcategory,
            subcategories = subcategories,
            products = productList
        )
    }

    fun onSubcategorySelected(subcategory: SubcategoryModel) {
        val products = getProductsBySubcategory(subcategory.id)
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
