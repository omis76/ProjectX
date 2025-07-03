package com.onkar.projectx.navigation


import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.onkar.projectx.screens.CalendarScreen
import com.onkar.projectx.screens.CategoryScreen
import com.onkar.projectx.screens.HomeScreen
import com.onkar.projectx.screens.LandingScreen
import com.onkar.projectx.screens.MobileNumberScreen
import com.onkar.projectx.screens.OTPScreen
import com.onkar.projectx.screens.PLPScreen
import com.onkar.projectx.screens.SearchScreen
import com.onkar.projectx.screens.PaymentScreen
import com.onkar.projectx.screens.ProductDetailScreen
import com.onkar.projectx.screens.ProfileScreen
import com.onkar.projectx.screens.SplashScreen
import com.onkar.projectx.screens.WalletScreen
import com.onkar.projectx.viewmodels.AuthViewModel
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel
import com.onkar.projectx.viewmodels.WalletViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    productsViewModel: ProductsViewModel
) {

    Log.d("ComposableDebug", "Navigation Graph Composable called")
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Landing.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut()
        }) {

        composable(route = Screen.Landing.route) {
            LandingScreen(navController, authViewModel)
        }

        composable(route = Screen.Home.route, exitTransition = {
            // If navigating to ProductDetailScreen, avoid horizontal slide
            val targetRoute = targetState.destination.route ?: ""
            if (targetRoute.startsWith(Screen.ProductDetail.route)) {
                fadeOut() // or no animation if you prefer
            } else {
                slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut()
            }
        }, popEnterTransition = {
            val initialRoute = initialState.destination.route ?: ""
            if (initialRoute.startsWith(Screen.ProductDetail.route)) {
                fadeIn()
            } else {
                slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn()
            }
        }
        ) {

            Log.d("ComposableDebug", "Navigation Graph Home Screen Composable called")
            HomeScreen(
                navController, cartViewModel,
                viewModel = productsViewModel,
            )
        }

        composable(Screen.Category.route) {
            CategoryScreen(
                navController,
                viewModel = productsViewModel
            )
        }

        composable(Screen.Profile.route) { ProfileScreen() }

        composable(Screen.Wallet.route) { navBackStackEntry ->
            val walletViewModel: WalletViewModel = viewModel(navBackStackEntry)
            WalletScreen(navController, walletViewModel)
        }

        composable(Screen.Payment.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Wallet.route)
            }
            val walletViewModel: WalletViewModel = viewModel(parentEntry)
            PaymentScreen(navController, walletViewModel)
        }

        composable(Screen.Splash.route) {
            SplashScreen(navController, authViewModel)
        }
        composable(Screen.Login.route) {
            MobileNumberScreen(navController, authViewModel = authViewModel)
        }
        composable(Screen.OTP.route) {
            OTPScreen(navController, authViewModel = authViewModel)
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                navController,
                cartViewModel = cartViewModel,
                productsViewModel = productsViewModel
            )
        }
        composable(Screen.PLP.route) {
            PLPScreen(
                cartViewModel = cartViewModel,
                navController = navController,
                viewModel = productsViewModel
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                viewModel = productsViewModel
            )
        }


        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            enterTransition = {
                slideInVertically(initialOffsetY = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { it }) + fadeOut()
            },
            popEnterTransition = {
                slideInVertically(initialOffsetY = { it }) + fadeIn()
            },
            popExitTransition = {
                slideOutVertically(targetOffsetY = { it }) + fadeOut()
            }) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val product = productsViewModel.getProductById(productId)
            ProductDetailScreen(
                product = product, viewModel = productsViewModel, navController = navController
            )
        }
    }
}