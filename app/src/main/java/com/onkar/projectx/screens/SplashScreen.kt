package com.onkar.projectx.screens


import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui.theme.greenBottom
import com.onkar.projectx.ui.theme.greenMiddle
import com.onkar.projectx.ui.theme.greenTop
import com.onkar.projectx.ui.theme.greenTop2
import com.onkar.projectx.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    Log.d("ui", "SplashScreen composed")
    val textList = listOf(
        "Text chosen by Mazhar",
        "Text chosen by Asif",
        "Text chosen by Akash And Onkar but why is the question"
    )
    var currentIndex by remember { mutableStateOf(0) }

    // Function to change the text
    fun changeText() {
        if (currentIndex < textList.size - 1) {
            currentIndex++
        } else {
            currentIndex = 0
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(color = greenTop)
                .padding(bottom = 16.dp)
        ) {
            Text(
                "SKIP",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clickable {
                        authViewModel.markFirstLaunchDone()
                        navController.navigate(Screen.Login.route)
                    }
                    .padding(horizontal = 32.dp, vertical = 12.dp)
                    .align(Alignment.TopEnd),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                greenTop,
                                greenTop2,
                                greenMiddle,
                                greenBottom,
                                MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Main Text 1",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Main Text 2",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    AnimatedContent(targetState = textList[currentIndex], transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { 1000 }, // From the right side
                            animationSpec = tween(500)
                        ) with slideOutHorizontally(
                            targetOffsetX = { -1000 }, // To the left side
                            animationSpec = tween(500)
                        )
                    }) { targetText ->
                        Text(
                            text = targetText,
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        textList.forEachIndexed { index, _ ->
                            Dot(
                                isActive = index == currentIndex,
                                index = index,
                                totalDots = textList.size
                            )
                        }
                    }
                }
                val rotationAngle by animateFloatAsState(
                    targetValue = if (currentIndex == 2) 180f else 135f, // 0f when it's the last text, else 135f
                    animationSpec = tween(500) // Adjust animation duration as needed
                )
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(end = 40.dp)
                        .size(58.dp)
                        .background(color = MaterialTheme.colorScheme.error, shape = CircleShape)
                        .clickable {
                            if (currentIndex == (textList.size - 1)) {
                                authViewModel.markFirstLaunchDone()
                                navController.navigate(Screen.Login.route)
                            } else {
                                changeText()
                            }
                        }
                        .padding(8.dp)
                        .rotate(rotationAngle)
                )
            }
        }
    }
}

@Composable
fun Dot(isActive: Boolean, index: Int, totalDots: Int) {
    val dotSize by animateDpAsState(
        targetValue = if (isActive) 60.dp else 20.dp,
        animationSpec = tween(300) // Adjust the duration to your preference
    )

    val dotColor =
        if (isActive) Color.Red else MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .width(dotSize)
            .height(3.dp)
            .padding(horizontal = 2.dp)
            .background(dotColor, shape = CircleShape)
    )
}