package com.onkar.projectx.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.onkar.projectx.data.DayOrderResponse
import com.onkar.projectx.data.Product
import com.onkar.projectx.ui.theme.*

@Composable
fun CalendarBillUI(
    dayOrder: DayOrderResponse?,
    cartProducts: List<Product>,
    selectedDate: String
) {
    // Calculate totals from day orders
    val orderTotal = dayOrder?.products?.sumOf { it.amount } ?: 0.0
    
    // Calculate totals from additional cart items
    val cartTotal = cartProducts.sumOf { product ->
        product.sellingPrice.toDouble() * (product.quantity ?: 1)
    }
    
    // Combined totals
    val itemsTotal = orderTotal + cartTotal
    
    // Calculate charges based on total amount
    val deliveryCharge = when {
        itemsTotal <= 100 -> 10.0
        itemsTotal <= 500 -> 20.0
        else -> 25.0
    }
    
    val handlingCharge = when {
        itemsTotal <= 100 -> 10.0
        itemsTotal <= 500 -> 20.0
        else -> 25.0
    }
    
    val finalTotal = itemsTotal + deliveryCharge + handlingCharge
    
    // Calculate MRP total for display
    val orderMrpTotal = dayOrder?.products?.sumOf { 
        it.price * it.orderedQuantity 
    } ?: 0.0
    
    val cartMrpTotal = cartProducts.sumOf { product ->
        product.mrp.toDouble() * (product.quantity ?: 1)
    }
    
    val mrpTotal = orderMrpTotal + cartMrpTotal

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
            Text("Bill Details for $selectedDate", style = TextStyleSMediumBoldBlack)
            Spacer(Modifier.height(16.dp))
            
            // Show order items if present
            if (dayOrder != null && dayOrder.products.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Scheduled Orders", style = TextStyleSmallBoldBlack)
                    Text("₹${"%.2f".format(orderTotal)}", style = TextStyleSmallBoldBlack.copy(color = Color(0xFF4CAF50)))
                }
            }
            
            // Show cart items if present
            if (cartProducts.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Additional Items", style = TextStyleSmallBoldBlack)
                    Text("₹${"%.2f".format(cartTotal)}", style = TextStyleSmallBoldBlack.copy(color = Color(0xFF4CAF50)))
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Items Total", style = TextStyleSmallNormalGray)
                Row {
                    if (mrpTotal > itemsTotal) {
                        Text(
                            "₹${"%.2f".format(mrpTotal)}",
                            style = TextStyleSmallNormalGrayStrikeThrough,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text("₹${"%.2f".format(itemsTotal)}", style = TextStyleSmallNormalBlack)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Delivery Charge", style = TextStyleSmallNormalGray)
                Text("₹${"%.2f".format(deliveryCharge)}", style = TextStyleSmallNormalBlack)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Handling Charge", style = TextStyleSmallNormalGray)
                Text("₹${"%.2f".format(handlingCharge)}", style = TextStyleSmallNormalBlack)
            }

            Spacer(Modifier.height(8.dp))
            
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Gray.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Final Total", style = TextStyleSMediumBoldBlack)
                Text("₹${"%.2f".format(finalTotal)}", style = TextStyleSMediumBoldBlack)
            }
        }

        SineWaveBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
        
        val statusText = when(dayOrder?.status) {
            "delivered" -> "Order delivered successfully"
            "pending", "packed", "picked" -> "Amount will be deducted after delivery"
            "not_delivered" -> "Order was not delivered"
            else -> "Amount will be deducted from wallet after delivery"
        }
        
        Text(
            text = statusText,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = greenTop)
                .padding(bottom = 16.dp),
            style = TextStyleSmallBoldBlack,
            textAlign = TextAlign.Center
        )
    }
}