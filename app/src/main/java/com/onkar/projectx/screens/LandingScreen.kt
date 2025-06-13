package com.onkar.projectx.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.viewmodels.AuthViewModel

@Composable
fun LandingScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    authViewModel.a = 100
    val startScreen by authViewModel.startScreen.collectAsState()

    LaunchedEffect(startScreen) {
        startScreen?.let {
            navigate(it, navController)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CenteredBox(modifier = Modifier.align(Alignment.Center))
    }
}

fun navigate(it: Screen, navController: NavHostController) {
    navController.navigate(it.route) {
        popUpTo(Screen.Landing.route) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun CenteredBox(modifier: Modifier) {
    Log.d("ui", "Box composed") // <- will only recompose if this part changes
    Box(
        modifier = modifier
            .size(100.dp)
            .background(color = Color.Transparent, shape = RoundedCornerShape(12.dp))
            .border(width = 4.dp, color = greenTop, shape = RoundedCornerShape(12.dp))
    )
}