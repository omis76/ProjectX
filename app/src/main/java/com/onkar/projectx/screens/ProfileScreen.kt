package com.onkar.projectx.screens

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.accompanist.permissions.rememberPermissionState
import com.onkar.projectx.navigation.Screen
import com.onkar.projectx.ui.theme.BlackAndWhiteColorScheme
import com.onkar.projectx.ui_components.TopViewBasic
import com.onkar.projectx.viewmodels.LocationPickerViewModel
import com.onkar.projectx.viewmodels.LocationViewModel
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun ProfileScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        TopViewBasic("Profile", navController)
        LocationPickerScreen(navController = navController)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPickerScreen(
    navController: NavHostController,
    viewModel: LocationPickerViewModel = hiltViewModel()
) {
    var selectedLocation by remember {
        mutableStateOf(LatLng(28.520383, 77.376360)) // Default to Delhi
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 14f)
    }

    var isMoving by remember { mutableStateOf(false) }

    // Track camera movement state
    LaunchedEffect(cameraPositionState.isMoving) {
        isMoving = cameraPositionState.isMoving
        if (!isMoving) {
            selectedLocation = cameraPositionState.position.target
        }
    }

    // Location Permission Request
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Check and request permissions
    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    // Animate location marker and map visuals
    val animatedOffsetY by animateDpAsState(
        targetValue = if (isMoving) (-24).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "offsetAnim"
    )

    val animatedColor by animateColorAsState(
        targetValue = if (isMoving) Color.Red else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 500),
        label = "colorAnim"
    )

    val animatedRotation by animateFloatAsState(
        targetValue = if (isMoving) -15f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "rotationAnim"
    )

    var expanded by remember { mutableStateOf(false) }

    // To disable map interaction when the footer is expanded
    val mapUiSettings = MapUiSettings(
        myLocationButtonEnabled = !expanded,
        scrollGesturesEnabled = !expanded,
        zoomGesturesEnabled = !expanded,
        tiltGesturesEnabled = !expanded
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = permissionState.status.isGranted),
            uiSettings = mapUiSettings
        )

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location Marker",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = animatedOffsetY)
                .rotate(animatedRotation)
                .size(48.dp),
            tint = animatedColor
        )

        // Show address and button together at the bottom
        if (permissionState.status.isGranted) {
            LocationAndActionFooter(
                latLng = selectedLocation,
                modifier = Modifier.align(Alignment.BottomCenter),
                viewModel = viewModel,
                navController = navController,
                expanded = expanded,    // Pass the expanded state
                onExpandToggle = { expanded = !expanded })
        }
    }
}

@Composable
fun LocationAndActionFooter(
    latLng: LatLng,
    modifier: Modifier,
    viewModel: LocationPickerViewModel,
    navController: NavHostController,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    val OutlinedTextFieldColorScheme = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BlackAndWhiteColorScheme.onPrimary,
        unfocusedBorderColor = BlackAndWhiteColorScheme.secondary,
        focusedLabelColor = BlackAndWhiteColorScheme.onPrimary,
        unfocusedLabelColor = BlackAndWhiteColorScheme.secondary,
        cursorColor = BlackAndWhiteColorScheme.primary,
        disabledTextColor = BlackAndWhiteColorScheme.onSurface
    )

    val context = LocalContext.current
    var address by remember { mutableStateOf("Fetching Address...") }
    var userAddress by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    // Fetch geocoded address
    LaunchedEffect(latLng) {
        val geocoder = Geocoder(context)
        val fetchedAddress = withTimeoutOrNull(3000) {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        }

        address = if (fetchedAddress.isNullOrEmpty()) {
            "Unable to fetch address"
        } else {
            fetchedAddress[0].getAddressLine(0) ?: "No address available"
        }
    }

    // Reset serviceability state when location changes
    LaunchedEffect(latLng) {
        viewModel.resetLocationServiceability()
    }

    // Show snackbar if address fails
    LaunchedEffect(address) {
        if (address == "Unable to fetch address") {
            snackbarHostState.showSnackbar("Failed to fetch location. Please try again.")
        }
    }

    // Navigate to home after successful save
    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            // Wait a moment for user to see success message
            kotlinx.coroutines.delay(1500)
            // Navigate to Home screen and clear back stack
            navController.navigate(Screen.Category.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = modifier) {
        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState, modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp)
                )
                .padding(16.dp)
                .imePadding(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Geocoded Address:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (expanded) {
                    IconButton(
                        onClick = {
                            onExpandToggle()
                            viewModel.resetState()
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Repick Location",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            )

            // Display location serviceability status
            viewModel.locationServiceable?.let { isServiceable ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            color = if (isServiceable) Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isServiceable) Icons.Default.LocationOn else Icons.Default.Refresh,
                        contentDescription = if (isServiceable) "Serviceable" else "Not Serviceable",
                        tint = if (isServiceable) Color.Green else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = viewModel.locationMessage ?: "Checking serviceability...",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isServiceable) Color.Green else Color.Red
                    )
                }
            } ?: run {
                // Show loading state while checking serviceability
                if (viewModel.isCheckingLocation) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                color = Color.Gray.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Checking",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Checking serviceability...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!expanded) {
                Button(
                    onClick = {
                        if (viewModel.locationServiceable == null) {
                            // Check serviceability first
                            viewModel.checkLocationServiceability(latLng.latitude, latLng.longitude)
                        } else if (viewModel.locationServiceable == true) {
                            // Location is serviceable, open bottom sheet
                            onExpandToggle()
                        }
                        // If locationServiceable == false, do nothing (button stays disabled)
                    }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !viewModel.isCheckingLocation && viewModel.locationServiceable != false
                ) {
                    Text(
                        when {
                            viewModel.isCheckingLocation -> "Checking Location..."
                            viewModel.locationServiceable == true -> "Pick This Location"
                            viewModel.locationServiceable == false -> "Location Not Serviceable"
                            else -> "Check Location"
                        }
                    )
                }
            } else {
                OutlinedTextField(
                    value = userAddress,
                    onValueChange = { userAddress = it },
                    label = { Text("Your Address") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldColorScheme
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldColorScheme
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.weight(1f)) // Removed "Repick" from here

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.saveAddress(userAddress, address, note)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = userAddress.isNotBlank() && !viewModel.isSaving
                    ) {
                        when {
                            viewModel.isSaving -> Text("Saving...")
                            viewModel.saveSuccess -> Text("Saved ✅")
                            else -> Text("Save Location")
                        }
                    }
                }
            }
        }
    }
}