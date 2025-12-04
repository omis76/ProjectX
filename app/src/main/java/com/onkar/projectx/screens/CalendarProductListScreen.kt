package com.onkar.projectx.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onkar.projectx.R
import com.onkar.projectx.ui.theme.red
import com.onkar.projectx.ui.theme.TextStyleSmallBoldWhite
import java.util.Locale
import androidx.navigation.NavHostController
import com.onkar.projectx.data.Product
import com.onkar.projectx.ui.theme.TextStyleSmallBoldBlack
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldBlack
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarProductListScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    productsViewModel: ProductsViewModel,
    selectedDate: String
) {
    val productsUiState by productsViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter products based on search query
    val filteredProducts = remember(productsUiState.products, searchQuery) {
        if (searchQuery.isBlank()) {
            productsUiState.products
        } else {
            productsUiState.products.filter { product ->
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    // Format date for display
    val formattedDate = remember(selectedDate) {
        try {
            val date = LocalDate.parse(selectedDate)
            date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        } catch (e: Exception) {
            selectedDate
        }
    }
    
    // Set selected date in cart view model
    LaunchedEffect(selectedDate) {
        cartViewModel.setSelectedDate(selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Column {
                    Text(
                        "Add Products",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "For $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search products...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Results Count
        Text(
            text = "${filteredProducts.size} products found",
            style = TextStyleSmallSemiBoldBlack,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Loading State
        if (productsUiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // Error State
        else if (productsUiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = productsUiState.error ?: "Something went wrong",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        // Products Grid
        else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts) { product ->
                    CalendarProductCard(
                        product = product,
                        cartViewModel = cartViewModel,
                        selectedDate = selectedDate
                    )
                }
            }
        }

        // Bottom Section - Cart Summary
        val cartItemsForDate = cartViewModel.getCartItemsForDate(selectedDate)
        if (cartItemsForDate.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${cartItemsForDate.size} items added for $formattedDate",
                        style = TextStyleSmallBoldBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done Adding Products")
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarProductCard(
    product: Product,
    cartViewModel: CartViewModel,
    selectedDate: String
) {
    val quantity by remember(selectedDate, product.id) {
        derivedStateOf { cartViewModel.getQuantityForDate(product.id, selectedDate) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Create a wrapper that uses date-specific cart operations
        val dateSpecificCartViewModel = remember(cartViewModel, selectedDate) {
            object {
                fun addProduct(product: Product) {
                    cartViewModel.addProductForDate(product, selectedDate)
                }
                
                fun removeProduct(product: Product) {
                    cartViewModel.removeProductForDate(product, selectedDate)
                }
                
                fun getQuantity(productId: String): Int {
                    return cartViewModel.getQuantityForDate(productId, selectedDate)
                }
            }
        }
        
        // Use a simplified product card for calendar context
        CalendarSpecificProductView(
            product = product,
            quantity = quantity,
            onAddProduct = { dateSpecificCartViewModel.addProduct(product) },
            onRemoveProduct = { dateSpecificCartViewModel.removeProduct(product) }
        )
    }
}

@Composable
fun CalendarSpecificProductView(
    product: Product,
    quantity: Int,
    onAddProduct: () -> Unit,
    onRemoveProduct: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Product Image placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Image", style = MaterialTheme.typography.bodySmall)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Product Name
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Product Unit
        Text(
            text = "${product.unitSize} ${product.unitType}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Price
        Text(
            text = "₹${product.sellingPrice}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // StepperView - matches PLPProductView pattern
        CalendarStepperView(
            quantity = quantity,
            onAddProduct = onAddProduct,
            onRemoveProduct = onRemoveProduct,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarStepperView(
    quantity: Int,
    onAddProduct: () -> Unit,
    onRemoveProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpanded = quantity > 0

    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = { fadeIn() with fadeOut() },
        label = "CalendarStepperTransition"
    ) { expanded ->
        if (expanded) {
            // Stepper with quantity controls
            Row(
                modifier = modifier
                    .height(32.dp)
                    .background(red, RoundedCornerShape(6.dp))
                    .border(0.5.dp, color = Color.White, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_remove_24),
                    contentDescription = "Remove",
                    modifier = Modifier.clickable { onRemoveProduct() },
                    tint = Color.White
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = String.format(Locale.ENGLISH, "%02d", quantity.coerceAtMost(99)),
                    style = TextStyleSmallBoldWhite
                )

                Spacer(Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.clickable { onAddProduct() },
                    tint = Color.White
                )
            }
        } else {
            // Add button
            Box(
                modifier = modifier
                    .size(32.dp)
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .border(0.5.dp, color = red, shape = RoundedCornerShape(6.dp))
                    .clickable { onAddProduct() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}