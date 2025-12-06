package com.onkar.projectx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.viewmodels.AppInitViewModel

@Composable
fun AppLoadingScreen(
    navController: NavHostController,
    appInitViewModel: AppInitViewModel = hiltViewModel()
) {
    val initState by appInitViewModel.initState.collectAsState()

    LaunchedEffect(initState.isInitialized) {
        if (initState.isInitialized) {
            when {
                initState.shouldShowLogin -> {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AppLoading.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                initState.shouldShowProfile -> {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.AppLoading.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                initState.shouldShowHome -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.AppLoading.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                initState.errors.isNotEmpty() -> {
                    // On error, navigate to login for retry
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AppLoading.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(greenTop),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color.White,
                strokeWidth = 4.dp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Loading...",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            if (initState.errors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Something went wrong. Redirecting...",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}