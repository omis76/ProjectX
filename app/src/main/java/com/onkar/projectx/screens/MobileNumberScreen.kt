package com.onkar.projectx.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.viewmodels.AuthViewModel

@Composable
fun MobileNumberScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    Log.d("ui", "MobileNumberScreen composed")
    val state = authViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = greenTop)
                .padding(bottom = 16.dp)
                .weight(0.6f)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // scrolls on keyboard open
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                "Sign up or Login",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = state.value.mobile,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        authViewModel.onMobileChanged(it)
                    }
                },
                label = { Text("Mobile Number") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                isError = state.value.mobileError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    errorTextColor = Color.Black,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Black
                )
            )
            if (state.value.mobileError != null) {
                Text(
                    text = state.value.mobileError ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    authViewModel.sendOTP {
                        navController.navigate(Screen.OTP.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Bigger button
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), // change this if you have `.action`
                enabled = !state.value.isLoading
            ) {
                if (state.value.isLoading)
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                else
                    Text("Proceed", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "By continuing you agree to our T&C and Privacy Policy",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}