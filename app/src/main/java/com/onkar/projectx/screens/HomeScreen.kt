package com.onkar.projectx.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.onkar.projectx.FloatingCartButtonWrapper
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.navigation.NavigationGraph
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.ui_components.BannerItemSkeleton
import com.onkar.projectx.ui_components.BannerItemView
import com.onkar.projectx.ui_components.BottomNavBar
import com.onkar.projectx.ui_components.SearchBarView
import com.onkar.projectx.ui_components.TopViewHome
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.HomeViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel


@Composable
fun HomeScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    viewModel: ProductsViewModel
) {
    Log.d("ui", "HomeScreen composed")
    val homeViewModel: HomeViewModel = hiltViewModel()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color.White, darkIcons = true)
    }
    LaunchedEffect(Unit) {
            viewModel.getCatalog(cartViewModel)
    }
    LaunchedEffect(Unit) {
        homeViewModel.fetchHomeDashboard()
        homeViewModel.fetchUserDashboard()
    }
    HomeViews(
        homeViewModel = homeViewModel,
        navController = navController,
        productsViewModel = viewModel,
        cartViewModel = cartViewModel
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeViews(
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    productsViewModel: ProductsViewModel,
    cartViewModel: CartViewModel
) {
    val listState = rememberLazyListState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val halfScreenHeight = screenHeight * 0.35f
    val scrollOffset = if (listState.firstVisibleItemIndex > 0)
        halfScreenHeight.value
    else
        listState.firstVisibleItemScrollOffset.toFloat().coerceIn(0f, halfScreenHeight.value)

    var bannerHeightDp =
        (halfScreenHeight.value - scrollOffset).coerceIn(72f, halfScreenHeight.value).dp
    val uiState by homeViewModel.homeUiState.collectAsState()

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        stickyHeader { TopViewHome(navController, homeViewModel) }
        item { TopBanner(adjustedHeight = 200.dp) }

        items(uiState.sections, key = { it.title }) { banner ->
            if (uiState.isLoading) {
                BannerItemSkeleton()
            } else {
                BannerItemView(
                    banner = banner,
                    viewModel = productsViewModel,
                    navController = navController,
                    cartViewModel = cartViewModel
                )
            }
        }
    }
}

@Composable
fun TopBanner(adjustedHeight: Dp, isLoading: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(adjustedHeight)
            .background(greenTop)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBarView()
        }
    }
}

