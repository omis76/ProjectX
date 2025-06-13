package com.onkar.projectx.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.onkar.projectx.data.PaymentMethod
import com.onkar.projectx.data.PaymentType
import com.onkar.projectx.ui_components.TopViewBasic
import com.onkar.projectx.viewmodels.WalletViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaymentScreen(navController: NavHostController, walletViewModel: WalletViewModel) {
    val amount = walletViewModel.amount
    val paymentTypes = walletViewModel.paymentTypes
    val isLoading = walletViewModel.isLoading

    LaunchedEffect(Unit) {
        walletViewModel.generateDummyPaymentData()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                stickyHeader {
                    TopViewBasic("Payment")
                    AmountStickyHeader(amount)
                }

                items(paymentTypes) { paymentType ->
                    PaymentTypeSection(paymentType)
                }
            }
        }
    }
}

@Composable
fun AmountStickyHeader(amount: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        tonalElevation = 4.dp,
        shadowElevation = 2.dp
    ) {
        Text(
            text = "Recharge Amount: ₹$amount",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun PaymentTypeSection(paymentType: PaymentType) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = paymentType.type,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        paymentType.methods.forEach { method ->
            PaymentMethodItem(method)
        }
    }
}

@Composable
fun PaymentMethodItem(method: PaymentMethod) {
    Surface(
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        onClick = {
            // TODO: handle payment click
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = method.icon,
                contentDescription = method.title,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(method.title, style = MaterialTheme.typography.bodyLarge)
                Text(method.method, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}