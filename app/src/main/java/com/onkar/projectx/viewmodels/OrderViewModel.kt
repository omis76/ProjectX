package com.onkar.projectx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.ApiClient
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.SubscriptionRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _orderResult = MutableStateFlow<Result<Boolean>?>(null)
    val orderResult: StateFlow<Result<Boolean>?> = _orderResult

    fun submitOrder(request: SubscriptionRequest) {
        viewModelScope.launch {
            try {
                val token = dataStoreManager.getToken()
                val response = ApiClient.homeApi.createOrderOrSubscription(
                    token = token,
                    request = request
                )
                _orderResult.value = Result.success(response.isSuccessful)
            } catch (e: Exception) {
                _orderResult.value = Result.failure(e)
            }
        }
    }
}