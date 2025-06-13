package com.onkar.projectx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.onkar.projectx.data.CategoryModel
import com.onkar.projectx.data.SubcategoryModel
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui.theme.TextStyleSmallNormalGray
import com.onkar.projectx.ui.theme.TextStyleSmallSemiBoldBlack
import com.onkar.projectx.viewmodels.ProductsViewModel

@Composable
fun CategoryScreen(navController: NavHostController, viewModel: ProductsViewModel) {
    val uiState = viewModel.uiState.collectAsState().value

    // Show loading spinner while data is being fetched
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Show error message if there's an error
    if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: ${uiState.error}", color = Color.Red)
        }
        return
    }

    // Display categories once data is loaded
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(uiState.categories) { category ->
            CategoryItem(category = category, viewModel, navController)
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryModel,
    viewModel: ProductsViewModel,
    navController: NavHostController
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Category title and subtitle
        Text(text = category.name, style = TextStyleSmallSemiBoldBlack)
        Text(
            text = category.description,
            style = TextStyleSmallNormalGray,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        val itemsPerRow = 4
        val numberOfRows =
            (category.subcategories.size + itemsPerRow - 1) / itemsPerRow  // Round up to get number of rows

        // Calculate the total height of the grid
        val gridHeight =
            numberOfRows * 120.dp // Assuming 120.dp as the height of each item (adjust this as needed)


        // Subcategories grid with a fixed height
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight) // Fixed height for the grid
            ) {
                items(category.subcategories) { subcategory ->
                    SubcategoryItems(
                        subcategory = subcategory,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        DividerSlice()
    }
}

@Composable
fun SubcategoryItems(
    subcategory: SubcategoryModel, viewModel: ProductsViewModel,
    navController: NavHostController
) {
    // Each subcategory item with an image and name
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                viewModel.loadPLPBySubcategory(subcategoryId = subcategory.id)
                navController.navigate(Screen.PLP.route)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = subcategory.image,
            contentDescription = subcategory.name,
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subcategory.name, style = TextStyleSmallSemiBoldBlack)
    }
}

@Composable
fun DividerSlice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent, // Thinning on the left
                        Color.Gray.copy(alpha = 0.8f), // Middle - wide
                        Color.Transparent // Thinning on the right
                    )
                )
            )
    )
}