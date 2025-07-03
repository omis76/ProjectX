package com.onkar.projectx.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val title: String) {
    object Home : Screen("meatio", Icons.Default.Home, "Home")
    object Calendar : Screen("calendar", Icons.Default.Call, "Calendar")
    object Category : Screen("categories", Icons.Default.DateRange, "categories")
    object Reorder : Screen("reorder", Icons.Default.Call, "Reorder")
    object PLP : Screen("plp", Icons.Default.Home, "PLP")
    object Landing : Screen("landing", Icons.Default.Home, "Landing")
    object Search : Screen("search", Icons.Default.Search, "Search")


    object ProductDetail : Screen("productDetail", Icons.Default.DateRange, "ProductDetail")
    object Profile : Screen("profile", Icons.Default.Person, "Profile")
    object Wallet : Screen("wallet", Icons.Default.Email, "Wallet")
    object Payment : Screen("payment", Icons.Default.Email, "Payment")
    object Splash : Screen("splash", Icons.Default.Home, "Splash")
    object Login : Screen("login", Icons.Default.Home, "Login")
    object OTP : Screen("otp", Icons.Default.Home, "OTP")
}


val hideBottomBarRoutes = listOf(
    Screen.Wallet.route,
    Screen.Payment.route,
    Screen.Splash.route,
    Screen.Login.route,
    Screen.OTP.route,
    Screen.ProductDetail.route,
    Screen.PLP.route,
    Screen.Search.route,
    Screen.Landing.route
)

val hideFloatingCartButtonRoutes = listOf(
    Screen.Wallet.route,
    Screen.Payment.route,
    Screen.Splash.route,
    Screen.Login.route,
    Screen.OTP.route,
    Screen.ProductDetail.route,
    Screen.Landing.route
)