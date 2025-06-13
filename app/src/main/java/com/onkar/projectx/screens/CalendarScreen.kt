package com.onkar.projectx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.onkar.projectx.data.CalendarDay
import com.onkar.projectx.data.DeliveryItem
import com.onkar.projectx.data.Product
import com.onkar.projectx.mergeTodayDeliveriesWithCart
import com.onkar.projectx.ui.theme.TextStyleSmallBoldBlack
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldBlack
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldRed
import com.onkar.projectx.ui_components.BillUI
import com.onkar.projectx.ui_components.CalendarDayItem
import com.onkar.projectx.ui_components.CancellationPolicyUI
import com.onkar.projectx.ui_components.WideProductView
import com.onkar.projectx.viewmodels.CalendarViewModel
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel
import java.time.LocalDate


@Composable
fun CalendarScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    productsViewModel: ProductsViewModel,
    calendarViewModel: CalendarViewModel = hiltViewModel()
) {
    val calendarUiState by calendarViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        calendarViewModel.onDateSelected(LocalDate.now())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Horizontal Calendar
        HorizontalCalendarView(
            days = calendarUiState.days,
            selectedDate = calendarUiState.selectedDate,
            onDateSelected = { selectedDate ->
                calendarViewModel.onDateSelected(selectedDate)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Label: All Items for Tomorrow
        Text(
            text = "All items for tomorrow",
            style = TextStyleSmallBoldBlack,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val isTomorrow = calendarUiState.selectedDate == LocalDate.now().plusDays(1)
        val deliveriesToShow = mergeTodayDeliveriesWithCart(
            deliveries = calendarUiState.dayDeliveries,
            cartProducts = cartViewModel.getCartProductsWithQuantity(),
            isTomorrow = isTomorrow
        )

        LaunchedEffect(key1 = deliveriesToShow, key2 = productsViewModel.uiState.value.products) {
            cartViewModel.preloadDeliveryQuantities(
                deliveryItems = deliveriesToShow,
                products = productsViewModel.uiState.value.products
            )
        }

        DeliverySection(
            isLoading = calendarUiState.isLoading,
            deliveries = deliveriesToShow,
            products = productsViewModel.uiState.value.products,
            cartViewModel = cartViewModel,
            productsViewModel = productsViewModel
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Missed something?", style = TextStyleSmallSemiBoldBlack)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add more items", color = Color.Red, style = TextStyleSmallSemiBoldRed)
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Label: Recommended Products
        Text(
            text = "Recommended Products",
            style = TextStyleSmallBoldBlack,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List of Recommended Products
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(calendarUiState.recommendedProducts) { product ->
                //ProductItemView(product = product, cartViewModel = cartViewModel)
            }
        }

        BillUI(cartViewModel)

        Spacer(Modifier.height(8.dp))

        CancellationPolicyUI()
    }
}

@Composable
fun DeliverySection(
    isLoading: Boolean,
    deliveries: List<DeliveryItem>,
    products: List<Product>,
    cartViewModel: CartViewModel,
    productsViewModel: ProductsViewModel
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (deliveries.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No deliveries scheduled", style = TextStyleSmallSemiBoldBlack)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp)),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(deliveries) { index, delivery ->
                val product = products.find { it.id == delivery.product }
                if (product != null) {
                    val productWithQty = product.copy(quantity = delivery.quantity)
                    WideProductView(productWithQty, cartViewModel, productsViewModel)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (index < deliveries.size - 1) DividerSlice()
                } else {
                    Text(
                        "Product ${delivery.product} not found",
                        style = TextStyleSmallSemiBoldRed,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalCalendarView(
    days: List<CalendarDay>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedDate) {
        val todayIndex = days.indexOfFirst { it.date == selectedDate }
        if (todayIndex >= 0) {
            listState.animateScrollToItem(todayIndex)
        }
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(days) { index, day ->

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (day.isFirstOfMonth) {
                    // Month label rotated properly
                    Box(
                        modifier = Modifier
                            .height(72.dp) // Fixed height for consistency
                            .width(24.dp)  // Minimum width after rotation
                            .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                            .graphicsLayer {
                                rotationZ = -90f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.monthName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(2.dp),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

                CalendarDayItem(
                    day = day,
                    isSelected = day.date == selectedDate,
                    onClick = { onDateSelected(day.date) }
                )

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}