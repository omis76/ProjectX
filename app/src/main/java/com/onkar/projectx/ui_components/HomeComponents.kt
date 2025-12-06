package com.onkar.projectx.ui_components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.onkar.projectx.data.Banner
import com.onkar.projectx.data.CategoryModel
import com.onkar.projectx.data.GenericItem
import com.onkar.projectx.data.HomeItem
import com.onkar.projectx.data.HomeSection
import com.onkar.projectx.data.LayoutType
import com.onkar.projectx.data.Product
import com.onkar.projectx.data.SubcategoryModel
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui.theme.TextStyleSmallBoldBlack
import com.onkar.projectx.ui.theme.TextStyleSmallNormalBlack
import com.onkar.projectx.ui.theme.TextStyleSmallNormalGray
import com.onkar.projectx.ui.theme.TextStyleSmallNormalGrayStrikeThrough
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldBlack
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.data.CustomerAddress
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.HomeViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel
import com.onkar.projectx.viewmodels.ProfileViewModel

@Composable
fun BannerItemSkeleton() {
    // Show a loading skeleton for banner items
    Column(modifier = Modifier.padding(16.dp)) {
        ShimmerEffect()
        ShimmerEffect()
        ShimmerEffect()
    }
}

@Composable
fun ShimmerEffect(
    itemCount: Int = 4 // Default 4 shimmer items
) {
    val transition = rememberInfiniteTransition()

    val shimmerAnimation by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    )

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val spacing = 8.dp
    val horizontalPadding = 16.dp

    val availableWidth = screenWidth - (horizontalPadding * 2)
    val itemWidth = ((availableWidth - (spacing * (itemCount - 1))) / itemCount) - 12.dp

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(itemCount) {
            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .padding(4.dp)
                    .aspectRatio(.8f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .graphicsLayer {
                        translationX = shimmerAnimation * 200f
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(30.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}


@Composable
fun TopViewHome(
    navController: NavHostController, 
    homeViewModel: HomeViewModel, 
    profileViewModel: ProfileViewModel
) {
    val uiState = homeViewModel.userUiState.collectAsState()
    var name = ""
    var balance = ""
    if (!uiState.value.isLoading) {
        name = uiState.value.addressLine
        balance = uiState.value.walletBalance
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(colors = listOf(Color.White, greenTop)))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Person",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(60.dp)
                        .background(color = MaterialTheme.colorScheme.onSurface, shape = CircleShape)
                        .clickable {
                            navController.navigate(Screen.Profile.route)
                        }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text("7am tomorrow", style = TextStyleSmallNormalGray)
                    // Show location below the time text
                    LocationSelectionComponent(profileViewModel)
                }
            }

            Row(
                modifier = Modifier
                    .background(color = Color.White, shape = RoundedCornerShape(24.dp))
                    .clickable {
                        navController.navigate(Screen.Wallet.route)
                    }
                    .border(width = 1.dp, shape = RoundedCornerShape(24.dp), color = Color.Gray)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Person",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "\u20b9 ${balance}",
                    style = TextStyleSmallBoldBlack
                )
            }
        }
    }
}

@Composable
fun HomeProductView(
    item: Product,
    viewModel: ProductsViewModel,
    itemWidth: Dp,
    cartViewModel: CartViewModel,
    navController: NavHostController
) {
    Column(modifier = Modifier.clickable {
        navController.navigate("productDetail/${item.id}")
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(.8f)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .clip(shape = RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = item.image,
                contentScale = ContentScale.Crop,
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(2.dp)
            ) {
                StepperView(item, cartViewModel = cartViewModel)
            }
            if (item.offer != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(color = greenTop, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp)
                        .clipToBounds(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        item.offer.offerPercentage.toString() + "%",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "OFF",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = -(8.dp)),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(item.name, style = TextStyleSmallSemiBoldBlack)
        Spacer(modifier = Modifier.height(4.dp))
        if (item.unitText != null) {
            Text(item.unitText, style = TextStyleSmallNormalGray)
            Spacer(modifier = Modifier.height(4.dp))
        }

        Row {
            Text(
                "\u20b9" + item.sellingPrice.toString(),
                style = TextStyleSmallNormalBlack
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "\u20b9" + item.sellingPrice.toString(),
                style = TextStyleSmallNormalGrayStrikeThrough
            )
        }

    }
}

@Composable
fun HomeBasicView(
    item: HomeItem,
    viewModel: ProductsViewModel,
    itemWidth: Dp,
    navController: NavHostController
) {
    var title = ""

    if (item.subcategory != null) {
        title = item.subcategory.name
    } else {
        if (item.generic != null) {
            title = item.generic.title
        }
    }

    var image = ""

    if (item.subcategory != null) {
        image = item.subcategory.image
    } else {
        if (item.generic != null) {
            image = item.generic.image_url
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
        if (item.subcategory != null) {
            viewModel.loadPLPBySubcategory(item.subcategory.id)
            navController.navigate(Screen.PLP.route)
        }
    }) {
        Box(
            modifier = Modifier
                .width(itemWidth)
                .aspectRatio(1f)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .clip(shape = RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = image,
                contentScale = ContentScale.FillBounds,
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            title,
            style = TextStyleSmallSemiBoldBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun BannerItemView(
    banner: HomeSection,
    viewModel: ProductsViewModel,
    navController: NavHostController,
    cartViewModel: CartViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = banner.title,
            style = TextStyleSmallSemiBoldBlack,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (banner.subtitle.isNotEmpty()) {
            Text(
                text = banner.subtitle,
                style = TextStyleSmallNormalGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (banner.layoutType) {
            LayoutType.HORIZONTAL -> {
                BannerItemViewWithPartialCell(
                    items = banner.items,
                    viewModel = viewModel,
                    navController = navController,
                    cartViewModel = cartViewModel,
                    visibleCount = 2// banner.spanCount?.takeIf { it > 0 } ?: 3
                )
            }

            LayoutType.GRID -> {
                BannerGrid(
                    banner.items,
                    columns = banner.spanCount?.takeIf { it > 0 } ?: 4,
                    viewModel,
                    navController = navController,
                    cartViewModel = cartViewModel
                )
            }

            else -> Unit
        }
    }
}

@Composable
fun BannerItemViewWithPartialCell(
    items: List<HomeItem>,
    viewModel: ProductsViewModel,
    cartViewModel: CartViewModel,
    navController: NavHostController,
    visibleCount: Int = 3  // Number of fully visible items in one view
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val spacing = 8.dp // Space between items
    val horizontalPadding = 16.dp // Horizontal padding on both sides

    val availableWidth = screenWidth - (horizontalPadding * 2)
    val itemWidth = ((availableWidth - (spacing * (visibleCount - 1))) / visibleCount) - 24.dp

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items(items) { item ->
            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .clickable {
                        //navController.navigate("${Screen.ProductDetail.route}/${item.id}")
                    }
            ) {
                if (item.product != null) {
                    HomeProductView(
                        item.product,
                        viewModel,
                        itemWidth,
                        cartViewModel,
                        navController
                    )
                } else {
                    HomeBasicView(item, viewModel, itemWidth, navController)
                }
            }
        }
    }
}

@Composable
fun BannerGrid(
    items: List<HomeItem>,
    columns: Int = 4,
    viewModel: ProductsViewModel,
    navController: NavHostController,
    cartViewModel: CartViewModel,
) {
    val spacing = 8.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalHorizontalPadding = 16.dp * 2 // Padding on both sides of the parent
    val availableWidth = screenWidth - totalHorizontalPadding
    val totalSpacing = spacing * (columns - 1)
    val itemWidth = (availableWidth - totalSpacing) / columns
    val rows = items.chunked(columns)

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .clickable {
                                //navController.navigate("${Screen.ProductDetail.route}/${item.id}")
                            }
                    ) {
                        if (item.product != null) {
                            HomeProductView(
                                item.product,
                                viewModel,
                                itemWidth,
                                cartViewModel,
                                navController
                            )
                        } else {
                            HomeBasicView(item, viewModel, itemWidth, navController)
                        }
                    }
                }

                // Fill the remaining cells in the row if there are fewer items
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.size(itemWidth))  // Fill remaining cells with empty space
                }
            }
        }
    }
}

@Composable
fun LocationSelectionComponent(profileViewModel: ProfileViewModel) {
    if (profileViewModel.selectedAddress != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { profileViewModel.showLocationDialog() }
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color.Green,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = profileViewModel.getAddressText(profileViewModel.selectedAddress),
                style = TextStyleSmallNormalGray,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Change",
                tint = Color.Gray,
                modifier = Modifier.size(12.dp)
            )
        }
    }

    // Location selection dialog
    if (profileViewModel.isLocationDialogVisible) {
        LocationSelectionDialog(profileViewModel)
    }
}

@Composable
fun LocationSelectionDialog(profileViewModel: ProfileViewModel) {
    val addresses = profileViewModel.customerProfile?.addresses ?: emptyList()

    AlertDialog(
        onDismissRequest = { profileViewModel.hideLocationDialog() },
        title = { Text("Select Delivery Location") },
        text = {
            Column {
                addresses.forEach { address ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { profileViewModel.selectAddress(address) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = if (profileViewModel.selectedAddress?.id == address.id) Color.Green else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = address.addressLine1,
                                style = TextStyleSmallSemiBoldBlack
                            )
                            Text(
                                text = "${address.addressLine2}, ${address.cityName}",
                                style = TextStyleSmallNormalGray
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { profileViewModel.hideLocationDialog() }) {
                Text("Done")
            }
        }
    )
}

@Composable
fun BannerCarousel(
    banners: List<Banner>,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return
    
    val pagerState = rememberPagerState(pageCount = { banners.size })
    
    Column(modifier = modifier) {
        // Banner carousel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 0.dp),
                pageSpacing = 0.dp,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                BannerItem(banner = banners[page])
            }
        }
        
        // Tab indicators
        if (banners.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(banners.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (pagerState.currentPage == index) 
                                    Color.White else Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                    if (index < banners.size - 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BannerItem(banner: Banner) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        // Banner image
        AsyncImage(
            model = banner.image_url,
            contentDescription = banner.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Gradient overlay for transparency at top, opaque at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent, // Transparent at top to blend with green
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.1f), // Slight darkening at bottom
                            Color.Black.copy(alpha = 0.3f)  // More darkening at bottom
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        
        // Banner text (if needed)
        if (banner.title.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = banner.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.8f),
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
            }
        }
    }
}