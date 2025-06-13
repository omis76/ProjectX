package com.onkar.projectx.data

data class AddressState(
    val address: String = "",
    val note: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)