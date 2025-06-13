package com.onkar.projectx.screens


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui_components.OTPInputTextFields
import com.onkar.projectx.viewmodels.AuthViewModel

@Composable
fun OTPScreen(navController: NavHostController, authViewModel: AuthViewModel) {

    Log.d("ui", "OTPScreen composed")
    val state = authViewModel.uiState.collectAsState()
    val mobileNumber = state.value.mobile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Enter OTP",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Sent on your mobile number ${mobileNumber}",
            style = MaterialTheme.typography.labelLarge,
        )

        Spacer(modifier = Modifier.height(48.dp))
        val otpValues = remember { mutableStateListOf("", "", "", "", "") } // 5 OTP fields
        var isError by remember { mutableStateOf(false) }
        val onUpdateOtpValuesByIndex: (Int, String) -> Unit = { index, value ->
            otpValues[index] = value
            val otp = otpValues.joinToString("")
            authViewModel.onOtpChanged(otp)
        }
        val onOtpInputComplete: () -> Unit = {
            authViewModel.verifyOtp {
                authViewModel.markLoggedIn()
                navController.navigate(Screen.Home.route)
            }
        }

        OTPInputTextFields(
            otpLength = 5,
            otpValues = otpValues,
            onUpdateOtpValuesByIndex = onUpdateOtpValuesByIndex,
            onOtpInputComplete = onOtpInputComplete,
            isError = state.value.otpError != null
        )

        if (state.value.otpError != null) {
            Text(state.value.otpError ?: "", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row {
            if (state.value.timer > 0) {
                Text(
                    "Retry in ${state.value.timer}s",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                TextButton(onClick = authViewModel::resendOtp) {
                    Text(
                        "Resend",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                authViewModel.verifyOtp {
                    authViewModel.markLoggedIn()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }

                }
            },
            enabled = !state.value.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state.value.isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp))
            else Text("Submit")
        }
    }
}