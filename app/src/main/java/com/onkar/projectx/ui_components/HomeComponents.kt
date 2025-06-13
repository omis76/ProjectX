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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
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
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.HomeViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel

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
fun TopViewHome(navController: NavHostController, homeViewModel: HomeViewModel) {
    val uiState = homeViewModel.userUiState.collectAsState()
    var name = ""
    var balance = ""
    if (!uiState.value.isLoading) {
        name = uiState.value.addressLine
        balance = uiState.value.walletBalance
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(colors = listOf(Color.White, greenTop)))
            .padding(16.dp),
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
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text("7am tomorrow", style = TextStyleSmallNormalGray)
                Text(
                    "${name} \u25bc", style = TextStyleSmallBoldBlack
                )
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
                contentScale = ContentScale.FillBounds,
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
                    cartViewModel = cartViewModel
                )
            }

            LayoutType.GRID -> {
                BannerGrid(
                    banner.items,
                    columns = 4,
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
    visibleCount: Int = 3,  // Number of fully visible items in one view
    viewModel: ProductsViewModel,
    cartViewModel: CartViewModel,
    navController: NavHostController
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