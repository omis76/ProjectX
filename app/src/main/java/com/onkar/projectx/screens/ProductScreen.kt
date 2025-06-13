package com.onkar.projectx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onkar.projectx.ui_components.StepperView
import com.onkar.projectx.ui_components.TopViewBasic
import com.onkar.projectx.viewmodels.ProductsViewModel

@Composable
fun ProductScreen(viewModel: ProductsViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopViewBasic("Products")
        ProductView(viewModel)
    }
}

@Composable
fun ProductView(viewModel: ProductsViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(viewModel.productList) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .background(color = Color.LightGray, shape = RoundedCornerShape(6.dp))
                    .padding(16.dp)
            ) {
                Text(text = item.name)
                Spacer(Modifier.weight(1f))
                StepperView(item, cartViewModel = viewModel())
            }
        }
    }
}
