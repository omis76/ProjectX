package com.onkar.projectx.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.viewModelScope
import com.onkar.projectx.ApiClient
import com.onkar.projectx.data.PaymentMethod
import com.onkar.projectx.data.PaymentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalletViewModel : ViewModel() {

    private val _paymentTypes = mutableStateListOf<PaymentType>()
    val paymentTypes: List<PaymentType> get() = _paymentTypes

    private val _amount = mutableStateOf("0.0")
    val amount: String get() = _amount.value

    fun setAmount(newAmount: String) {
        _amount.value = newAmount
    }

    var isLoading by mutableStateOf(false)
        private set

    fun generateDummyPaymentData() {
        if (_paymentTypes.isNotEmpty()) return

        isLoading = true
        viewModelScope.launch {
            try {
                val apiData = ApiClient.homeApi.getPaymentMethods()
                if (apiData.isNotEmpty()) {
                    _paymentTypes.addAll(apiData)
                    isLoading = false
                    return@launch
                }
            } catch (_: Exception) {
                // ignore errors and fallback to demo data
            }

            delay(1500)
            _paymentTypes.addAll(
                listOf(
                    PaymentType(
                        type = "UPI",
                        methods = listOf(
                            PaymentMethod("GPay", Icons.Default.Send, "gpay@upi"),
                            PaymentMethod("PhonePe", Icons.Default.Phone, "phonepe@upi"),
                            PaymentMethod("Paytm", Icons.Default.DateRange, "paytm@upi")
                        )
                    ),
                    PaymentType(
                        type = "Cards",
                        methods = listOf(
                            PaymentMethod("Visa", Icons.Default.DateRange, "**** 1234"),
                            PaymentMethod("Mastercard", Icons.Default.AccountBox, "**** 5678")
                        )
                    ),
                    PaymentType(
                        type = "Wallets",
                        methods = listOf(
                            PaymentMethod("Amazon Pay", Icons.Default.CheckCircle, "Amazon Wallet"),
                            PaymentMethod("Mobikwik", Icons.Default.Delete, "Mobikwik Wallet")
                        )
                    )
                )
            )
            isLoading = false
        }
    }
}