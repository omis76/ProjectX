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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.onkar.projectx.ui.theme.BlackAndWhiteColorScheme
import com.onkar.projectx.ui_components.TopViewBasic
import com.onkar.projectx.viewmodels.LocationPickerViewModel
import com.onkar.projectx.viewmodels.LocationViewModel
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        TopViewBasic("Profile")
        LocationPickerScreen()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPickerScreen(viewModel: LocationPickerViewModel = viewModel()) {
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

    // Show snackbar if address fails
    LaunchedEffect(address) {
        if (address == "Unable to fetch address") {
            snackbarHostState.showSnackbar("Failed to fetch location. Please try again.")
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

            Spacer(modifier = Modifier.height(8.dp))

            if (!expanded) {
                Button(
                    onClick = onExpandToggle, modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick This Location")
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