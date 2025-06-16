package com.onkar.projectx.ui_components

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.IconButton
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import coil.compose.AsyncImage
import com.google.android.material.datepicker.MaterialDatePicker
import com.onkar.projectx.data.CalendarDay
import com.onkar.projectx.data.Product
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui.theme.TextStyleSmallNormalBlack
import com.onkar.projectx.ui.theme.TextStyleSmallNormalGray
import com.onkar.projectx.ui.theme.TextStyleSmallNormalGrayStrikeThrough
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldBlack
import com.onkar.projectx.ui.theme.red
import com.onkar.projectx.ui.theme.red2
import com.onkar.projectx.viewmodels.CartViewModel
import com.onkar.projectx.viewmodels.ProductsViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import com.onkar.projectx.R
import com.onkar.projectx.screens.DividerSlice
import com.onkar.projectx.ui.theme.TextStyleSMediumBoldBlack
import com.onkar.projectx.ui.theme.TextStyleSmallBoldBlack
import com.onkar.projectx.ui.theme.TextStyleSmallBoldWhite
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldGray
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.viewmodels.CalendarViewModel
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(Screen.Home, Screen.Calendar, Screen.Category, Screen.Reorder)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column {
        // Top shadow gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f), Color.Transparent
                            )
                        )
                    )
                })

        // BottomNavigation itself
        BottomNavigation(
            backgroundColor = Color.White, elevation = 0.dp, // no default shadow
            modifier = Modifier.height(72.dp)
        ) {
            items.forEach { screen ->
                val selected = currentRoute == screen.route
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (selected) Color(0xFF4CAF50) else Color.DarkGray
                        )
                    }, label = {
                        Text(
                            text = screen.title,
                            color = if (selected) Color(0xFF4CAF50) else Color.DarkGray
                        )
                    }, selected = selected, onClick = {
                        if (!selected) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }, alwaysShowLabel = true
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StepperView(
    tProduct: Product, modifier: Modifier = Modifier, cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()


    var count = cartItems[tProduct.id]?.quantity ?: 0
    var product = cartItems[tProduct.id] ?: tProduct

    val isExpanded = count > 0

    AnimatedContent(
        targetState = isExpanded, transitionSpec = {
            fadeIn() with fadeOut()
        }, label = "StepperCompactTransition"
    ) { expanded ->
        if (expanded) {
            Row(
                modifier = modifier
                    .height(32.dp)
                    .background(red, RoundedCornerShape(6.dp))
                    .border((0.5).dp, color = Color.White, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_remove_24),
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        cartViewModel.removeProduct(product)
                    },
                    tint = Color.White
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = String.format(Locale.ENGLISH, "%02d", count.coerceAtMost(99)),
                    style = TextStyleSmallBoldWhite
                )

                Spacer(Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        cartViewModel.addProduct(product)
                    },
                    tint = Color.White
                )
            }
        } else {
            Box(
                modifier = modifier
                    .size(32.dp)
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .border((0.5).dp, color = red, shape = RoundedCornerShape(6.dp))
                    .clickable {
                        cartViewModel.addProduct(product)
                    }, contentAlignment = Alignment.Center
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


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuantityStepper(
    quantity: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val count = quantity.value
    val isExpanded = count > 0

    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = { fadeIn() with fadeOut() },
        label = "StepperTransition"
    ) { expanded ->
        if (expanded) {
            Row(
                modifier = modifier
                    .height(32.dp)
                    .background(Color.Red, RoundedCornerShape(6.dp))
                    .border(0.5.dp, Color.White, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_remove_24),
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        if (count > 0) quantity.value = count - 1
                    },
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = count.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        quantity.value = count + 1
                    },
                    tint = Color.White
                )
            }
        } else {
            Box(
                modifier = modifier
                    .size(32.dp)
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .border(0.5.dp, Color.Red, RoundedCornerShape(6.dp))
                    .clickable {
                        quantity.value = 1
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun TopViewBasic(title: String, navController: NavHostController? = null) {
    val colorScheme = MaterialTheme.colorScheme
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(colorScheme.surface, darkIcons = false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navController != null) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            color = colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
    }
}


@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit, onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val fragmentManager = (activity as AppCompatActivity).supportFragmentManager

    DisposableEffect(Unit) {
        val picker = MaterialDatePicker.Builder.datePicker().setTitleText("Select delivery date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

        picker.addOnPositiveButtonClickListener { selectedDateMillis ->
            val selectedDate =
                Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.systemDefault())
                    .toLocalDate()
            onDateSelected(selectedDate)
        }

        picker.addOnDismissListener {
            onDismiss()
        }

        picker.show(fragmentManager, "DATE_PICKER")

        onDispose {
            picker.dismiss()
        }
    }
}

fun Context.findActivity(): ComponentActivity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is ComponentActivity) return ctx
        ctx = ctx.baseContext
    }
    throw IllegalStateException("Activity not found in context chain.")
}


fun Modifier.verticalGradientBackground(start: Color, end: Color): Modifier {
    return this.background(Brush.verticalGradient(colors = listOf(start, end)))
}

@Composable
fun CalendarDayItem(
    day: CalendarDay, isSelected: Boolean, onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 4.dp)) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = day.dayOfWeek,
            style = TextStyleSmallNormalGray,
            color = if (!isSelected) MaterialTheme.colorScheme.primary else Color.White,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = day.date.dayOfMonth.toString(),
            style = TextStyleSmallSemiBoldBlack,
            color = if (!isSelected) MaterialTheme.colorScheme.primary else Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${day.itemCount} items",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .background(
                    color = Color.Green,
                    shape = RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp)
                )
                .padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun WideProductView(
    product: Product, cartViewModel: CartViewModel, productsViewModel: ProductsViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.image,
                contentScale = ContentScale.FillBounds,
                contentDescription = "product Image",
                modifier = Modifier
                    .size(60.dp)
                    .background(color = Color.Gray, shape = RoundedCornerShape(12.dp))
                    .clip(shape = RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    product.name, style = TextStyleSmallSemiBoldBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    product.unitText, style = TextStyleSmallNormalGray
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StepperView(product, cartViewModel = cartViewModel)
            Row {
                Text(
                    "\u20b9" + product.sellingPrice.toString(), style = TextStyleSmallNormalBlack
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "\u20b9" + product.sellingPrice.toString(),
                    style = TextStyleSmallNormalGrayStrikeThrough
                )
            }
        }
    }
}


@Composable
fun FloatingCartButton(
    cartItemCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = red, shape = RoundedCornerShape(40.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)

        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("View Cart", color = Color.White)
            Text("$cartItemCount items", color = Color.White)
        }
        Spacer(modifier = Modifier.width(24.dp))
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = "Cart",
            tint = Color.White,
            modifier = Modifier
                .background(
                    color = red2, shape = CircleShape
                )
                .size(36.dp)
        )
    }
}

@Composable
fun PLPProductView(
    item: Product,
    viewModel: ProductsViewModel,
    cartViewModel: CartViewModel,
    navController: NavHostController
) {
    Column(modifier = Modifier.clickable {
        navController.navigate("productDetail/${item.id}")
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(.95f)
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
        Text(item.unitText, style = TextStyleSmallNormalGray)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Text(
                "\u20b9" + item.sellingPrice.toString(), style = TextStyleSmallNormalBlack
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
fun SearchBarView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .border(
                1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.surface
        )
        Text(
            "Search for products",
            style = TextStyleSmallNormalGray,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
fun BillUI(cartViewModel: CartViewModel) {
    val bill by cartViewModel.billState.collectAsState()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .clip(shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text("Bill Details", style = TextStyleSMediumBoldBlack)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Items Total", style = TextStyleSmallNormalGray)
                Row {
                    Text(
                        "₹${"%.2f".format(bill.mrpTotal)}",
                        style = TextStyleSmallNormalGrayStrikeThrough
                    )
                    Text("₹${"%.2f".format(bill.sellingTotal)}", style = TextStyleSmallNormalBlack)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Delivery Charge", style = TextStyleSmallNormalGray)
                Row {
                    Text(
                        "₹${"%.2f".format(bill.deliveryCharge)}",
                        style = TextStyleSmallNormalBlack
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Handling Charge", style = TextStyleSmallNormalGray)
                Row {
                    Text(
                        "₹${"%.2f".format(bill.handlingCharge)}",
                        style = TextStyleSmallNormalBlack
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            DividerSlice()
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Final Total", style = TextStyleSMediumBoldBlack)
                Row {
                    Text("₹${"%.2f".format(bill.totalToPay)}", style = TextStyleSMediumBoldBlack)
                }
            }
        }

        SineWaveBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
        Text(
            "Amount will be deducted from wallet after delivery",
            modifier = Modifier
                .fillMaxWidth()
                .background(color = greenTop)
                .padding(bottom = 16.dp),
            style = TextStyleSmallBoldBlack,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CancellationPolicyUI() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Cancellation Policy", style = TextStyleSmallBoldBlack)
        Spacer(Modifier.height(16.dp))
        Text(
            "Orders cannot be cancelled after midnight or once packed for delivery. In case of unexpected delays, a refund will be provided, if applicable",
            style = TextStyleSmallSemiBoldGray
        )
    }
}

@Composable
fun SineWaveBackground(modifier: Modifier) {

    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )


    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val amplitude = height / 8
        val frequency = 0.07f

        val path = Path().apply {
            moveTo(0f, height / 2)

            for (x in 0 until width.toInt()) {
                val y = height / 2 + amplitude * sin(frequency * x)//- phase)

                lineTo(x.toFloat(), y.toFloat())
            }

            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = path, color = greenTop, style = Fill
        )
    }
}