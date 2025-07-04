package com.onkar.projectx.repository

import com.onkar.projectx.data.BasicResponse
import com.onkar.projectx.data.Resource
import com.onkar.projectx.data.TokenResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication-related operations.
 * Handles OTP sending, verification, and token management.
 */
interface AuthRepository {
    
    /**
     * Sends OTP to the provided phone number
     */
    fun sendOTP(phoneNumber: String): Flow<Resource<BasicResponse>>
    
    /**
     * Verifies OTP and returns authentication token
     */
    fun verifyOTP(phoneNumber: String, otp: String): Flow<Resource<TokenResponse>>
    
    /**
     * Checks if user is currently logged in
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * Gets the current authentication token
     */
    suspend fun getAuthToken(): String?
    
    /**
     * Saves authentication token securely
     */
    suspend fun saveAuthToken(token: String)
    
    /**
     * Logs out the user and clears stored data
     */
    suspend fun logout()
    
    /**
     * Checks if this is the first app launch
     */
    suspend fun isFirstLaunch(): Boolean
    
    /**
     * Marks first launch as completed
     */
    suspend fun markFirstLaunchCompleted()
    
    /**
     * Marks user as logged in
     */
    suspend fun markUserLoggedIn()
}