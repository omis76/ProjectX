package com.onkar.projectx.screens

import androidx.compose.animation.core.animateDpAsState
import com.onkar.projectx.viewmodels.ProductsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.onkar.projectx.data.Product
import com.onkar.projectx.data.SubcategoryModel
import com.onkar.projectx.ui.theme.TextStyleSmallNormalGray
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldBlack
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldGray
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.ui_components.HomeProductView
import com.onkar.projectx.ui_components.PLPProductView
import com.onkar.projectx.ui_components.SearchBarView
import com.onkar.projectx.viewmodels.CartViewModel
import kotlin.math.roundToInt

@Composable
fun PLPScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    viewModel: ProductsViewModel
) {
    val state by viewModel.plpUiState.collectAsState()

    val itemOffsets = remember { mutableStateMapOf<String, Int>() }
    val density = LocalDensity.current

    val selectedOffsetPx = itemOffsets[state.selectedSubcategory?.id]
    val indicatorOffset by animateDpAsState(
        targetValue = with(density) { (selectedOffsetPx ?: 0).toDp() },
        label = "IndicatorOffset"
    )

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Something went wrong: ${state.error}")
            }
        }

        state.subcategories.isNotEmpty() -> {
            Column(Modifier.fillMaxSize()) {

                Row(Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .width(80.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(bottomEnd = 16.dp)
                            )
                            .padding(top = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    SearchBarView()
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxSize()) {
                    // LEFT: Subcategories
                    Box {
                        LazyColumn(
                            modifier = Modifier
                                .width(80.dp)
                                .fillMaxHeight()
                                .background(Color.White, shape = RoundedCornerShape(topEnd = 16.dp))
                                .padding(vertical = 8.dp)
                        ) {
                            items(state.subcategories) { subcategory ->
                                val isSelected = subcategory.id == state.selectedSubcategory?.id
                                SubcategoryItem(
                                    subcategory = subcategory,
                                    selected = isSelected,
                                    onClick = { viewModel.onSubcategorySelected(subcategory) },
                                    onPositioned = { y ->
                                        itemOffsets[subcategory.id] = y
                                    }
                                )
                            }
                        }

                        // Green vertical indicator
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .width(6.dp)
                                .offset(y = indicatorOffset)
                                .padding(top = 16.dp)
                                .height(56.dp)
                                .background(
                                    color = greenTop,
                                    shape = RoundedCornerShape(
                                        topStart = 4.dp,
                                        bottomStart = 4.dp
                                    )
                                )
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.products) { product ->
                            PLPProductView(
                                item = product,
                                viewModel = viewModel,
                                cartViewModel = cartViewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }

        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No subcategories or products available.")
            }
        }
    }
}

@Composable
fun SubcategoryItem(
    subcategory: SubcategoryModel,
    selected: Boolean,
    onClick: () -> Unit,
    onPositioned: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .width(80.dp)
            .padding(vertical = 6.dp)
            .onGloballyPositioned { coordinates ->
                val y = coordinates.positionInParent().y.roundToInt()
                onPositioned(y)
            }
            .clickable { onClick() },
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .width(70.dp)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selected) greenTop else Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(subcategory.image),
                    contentDescription = subcategory.name,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subcategory.name,
                style = if (selected) TextStyleSmallSemiBoldBlack else TextStyleSmallSemiBoldGray,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${product.unitSize} • ₹${product.sellingPrice}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    product.deliveryText,
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
        }
    }
}