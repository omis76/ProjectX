package com.onkar.projectx.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.onkar.projectx.ApiClient
import com.onkar.projectx.data.Product
import com.onkar.projectx.data.SubscriptionRequest
import com.onkar.projectx.ui.theme.BlackAndWhiteColorScheme
import com.onkar.projectx.ui.theme.TextStyleSmallNormalGrayStrikeThrough
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldBlack
import com.onkar.projectx.ui_components.DatePickerDialog
import com.onkar.projectx.ui_components.QuantityStepper
import com.onkar.projectx.ui_components.StepperView
import com.onkar.projectx.ui_components.TopViewBasic
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.OrderViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProductDetailScreen(
    product: Product?,
    viewModel: ProductsViewModel,
    navController: NavHostController
) {
    val scrollState = rememberScrollState()
    val selectedOption = remember { mutableStateOf("One time") }
    var selectedDate = remember { mutableStateOf<LocalDate?>(null) }
    val selectedDay = remember { mutableStateOf("") }
    val selectedDays = remember { mutableStateListOf<String>() }
    val quantity = remember { mutableStateOf(0) }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)) + fadeOut()
    ) {
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlackAndWhiteColorScheme.background)
            ) {
                Text("This product is not here")
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlackAndWhiteColorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {

                    val screenHeightDp = LocalConfiguration.current.screenHeightDp
                    val imageHeight = (1f * screenHeightDp / 3).dp

                    // Product Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(imageHeight)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "Close",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 16.dp)
                                .size(42.dp)
                                .rotate(45f)
                        )
                        // Load your image here
                        Text("Image Here", color = BlackAndWhiteColorScheme.onBackground)
                    }

                    // Product Info Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = product.name,
                                style = TextStyleSmallSemiBoldBlack
                            )
                            Text(
                                text = product.unitText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = BlackAndWhiteColorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "\u20b9" + product.offerPrice.toString(),
                                    style = TextStyleSmallSemiBoldBlack
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "\u20b9" + product.mrp.toString(),
                                    style = TextStyleSmallNormalGrayStrikeThrough
                                )
                            }
                        }

                        QuantityStepper(
                            quantity = quantity,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .height(32.dp)
                        )
                    }

                    Divider(
                        color = BlackAndWhiteColorScheme.secondary.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    DeliveryOptionsSection(
                        selectedOption = selectedOption,
                        selectedDate = selectedDate,
                        selectedDay = selectedDay,
                        selectedDays = selectedDays
                    )

                    Divider(
                        color = BlackAndWhiteColorScheme.secondary.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Text(
                        text = "product.description" ?: "No description available.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BlackAndWhiteColorScheme.onBackground,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(BlackAndWhiteColorScheme.background)
                        .padding(16.dp)
                ) {

                    val scope = rememberCoroutineScope()
                    val orderViewModel: OrderViewModel = hiltViewModel()
                    Button(
                        onClick = {
                            scope.launch {

                                val orderType =
                                    if (selectedOption.value == "One time") "one_time" else "subscription"

                                val startDate = if (orderType == "one_time")
                                    selectedDate.value ?: LocalDate.now()
                                else
                                    LocalDate.now()

                                val endDate = if (orderType == "one_time")
                                    startDate
                                else
                                    startDate.plusMonths(3)

                                val daysOfWeek = when (selectedOption.value) {
                                    "Once a week" -> listOf(selectedDay.value.lowercase())
                                    "Let me choose" -> selectedDays.map { it.lowercase() }
                                    else -> emptyList()
                                }

                                val request = SubscriptionRequest(
                                    product = product.id.toInt(),
                                    quantity = quantity.value,
                                    order_type = orderType,
                                    start_date = startDate.toString(),
                                    end_date = endDate.toString(),
                                    days_of_week = daysOfWeek
                                )

                                orderViewModel.submitOrder(request)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Delivery")
                    }
                }
            }
        }
    }
}


@Composable
fun DeliveryOptionsSection(
    selectedOption: MutableState<String>,
    selectedDate: MutableState<LocalDate?>,
    selectedDay: MutableState<String>,
    selectedDays: SnapshotStateList<String>
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = {
                selectedDate.value = it
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }


    val deliveryOptions = listOf("One time", "Once a week", "Let me choose")

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            deliveryOptions.forEach { option ->
                val isSelected = option == selectedOption.value
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) BlackAndWhiteColorScheme.primary.copy(alpha = 0.1f)
                    else Color.Transparent
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) BlackAndWhiteColorScheme.primary
                            else BlackAndWhiteColorScheme.secondary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedOption.value = option }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (isSelected) BlackAndWhiteColorScheme.primary else BlackAndWhiteColorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedOption.value) {
            "One time" -> {
                Text(
                    text = "Select delivery date",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BlackAndWhiteColorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = selectedDate.value?.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                        ?: "Choose Date",
                    modifier = Modifier
                        .clickable { showDatePicker = true }
                        .padding(12.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
            }

            "Once a week" -> {
                Text(
                    "Select a day",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BlackAndWhiteColorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    daysOfWeek.forEach { day ->
                        val isSelected = selectedDay.value == day

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSelected) BlackAndWhiteColorScheme.primary.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) BlackAndWhiteColorScheme.primary
                                    else BlackAndWhiteColorScheme.secondary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .clickable { selectedDay.value = day }
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) BlackAndWhiteColorScheme.primary else BlackAndWhiteColorScheme.onBackground
                            )
                        }
                    }
                }
            }

            "Let me choose" -> {
                Text(
                    "Select multiple days",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BlackAndWhiteColorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    daysOfWeek.forEach { day ->
                        val isSelected = selectedDays.contains(day)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSelected) BlackAndWhiteColorScheme.primary.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) BlackAndWhiteColorScheme.primary
                                    else BlackAndWhiteColorScheme.secondary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .clickable {
                                    if (isSelected) selectedDays.remove(day) else selectedDays.add(
                                        day
                                    )
                                }
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) BlackAndWhiteColorScheme.primary else BlackAndWhiteColorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}