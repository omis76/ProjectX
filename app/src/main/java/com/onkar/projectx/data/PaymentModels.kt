package com.onkar.projectx.data

import androidx.compose.ui.graphics.vector.ImageVector

data class PaymentMethod(
    val title: String,
    val icon: ImageVector,
    val method: String
)

data class PaymentType(
    val type: String,
    val methods: List<PaymentMethod>
)