package com.onkar.projectx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui_components.TopViewBasic
import com.onkar.projectx.viewmodels.WalletViewModel

@Composable
fun WalletScreen(navController: NavHostController, walletViewModel: WalletViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopViewBasic("Wallet", navController)
        WalletView(modifier = Modifier.weight(1f), walletViewModel)
        Button(
            onClick = { navController.navigate(Screen.Payment.route) },
            enabled = walletViewModel.amount
                .toDoubleOrNull()
                ?.let { it > 0 }
                ?: false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Recharge Wallet",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WalletView(modifier: Modifier, viewModel: WalletViewModel) {
    val scrollState = rememberScrollState()
    val amount = viewModel.amount

    val suggestions = listOf("100", "200", "300", "500", "1000", "1500")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(
                text = "Recharge Wallet",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )


            OutlinedTextField(
                value = amount,
                onValueChange = { value ->
                    if (value.all { it.isDigit() }) {
                        viewModel.setAmount(value)
                    }
                },
                placeholder = { Text("Enter amount") },
                leadingIcon = { Text("₹", style = MaterialTheme.typography.titleLarge) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Black
                )
            )

            Text("Suggested", style = MaterialTheme.typography.bodyMedium)

            FlowRow {
                suggestions.forEach { suggestion ->
                    OutlinedButton(
                        onClick = { viewModel.setAmount(suggestion) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(4.dp)
                            .defaultMinSize(minWidth = 80.dp)
                    ) {
                        Text("₹$suggestion")
                    }
                }
            }
        }
    }
}