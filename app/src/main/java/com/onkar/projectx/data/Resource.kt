package com.onkar.projectx.data

/**
 * A generic wrapper class that represents the state of data loading operations.
 * Provides a clean way to handle loading, success, and error states.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful operation with data
     */
    class Success<T>(data: T) : Resource<T>(data)
    
    /**
     * Represents an error state with optional message and data
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    
    /**
     * Represents a loading state with optional cached data
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

/**
 * Extension functions for easier handling of Resource states
 */
inline fun <T> Resource<T>.onSuccess(action: (value: T) -> Unit): Resource<T> {
    if (this is Resource.Success && data != null) {
        action(data)
    }
    return this
}

inline fun <T> Resource<T>.onError(action: (exception: String) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(message ?: "Unknown error")
    }
    return this
}

inline fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}