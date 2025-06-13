package com.onkar.projectx

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.navigation.NavigationGraph
import com.onkar.projectx.navigation.hideBottomBarRoutes
import com.onkar.projectx.navigation.hideFloatingCartButtonRoutes
import com.onkar.projectx.ui.theme.ProjectXTheme
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.ui_components.BottomNavBar
import com.onkar.projectx.ui_components.FloatingCartButton
import com.onkar.projectx.viewmodels.AuthViewModel
import com.onkar.projectx.viewmodels.AuthViewModelFactory
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectXTheme {
                val navController = rememberAnimatedNavController()
                val cartViewModel: CartViewModel = viewModel()
                SetStatusBarColor()
                MainScreen(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    authViewModel = hiltViewModel(),
                    productsViewModel = hiltViewModel()
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    productsViewModel: ProductsViewModel
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val shouldShowBottomBar = hideBottomBarRoutes.none { currentRoute?.startsWith(it) == true }
    val showFloatingCartButton =
        hideFloatingCartButtonRoutes.none { currentRoute?.startsWith(it) == true }


    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        bottomBar = {
            if (shouldShowBottomBar) BottomNavBar(navController = navController)

        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(
                navController = navController,
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
                productsViewModel = productsViewModel
            )
            if (showFloatingCartButton) FloatingCartButtonWrapper(
                cartViewModel,
                navController,
                Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun SetStatusBarColor() {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(greenTop, darkIcons = true)
    }
}

@Composable
fun FloatingCartButtonWrapper(
    cartViewModel: CartViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    if (cartItems.isNotEmpty()) {
        FloatingCartButton(
            cartItemCount = cartItems.size,
            onClick = { /* navController.navigate(Screen.Cart.route) */ },
            modifier = modifier
                .padding(bottom = 72.dp)
        )
    }
}