package com.onkar.projectx.repository

import com.onkar.projectx.HomeApiService
import com.onkar.projectx.data.BasicResponse
import com.onkar.projectx.data.DataStoreManager
import com.onkar.projectx.data.Resource
import com.onkar.projectx.data.TokenResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository that handles authentication operations
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: HomeApiService,
    private val dataStore: DataStoreManager
) : AuthRepository {
    
    override fun sendOTP(phoneNumber: String): Flow<Resource<BasicResponse>> = flow {
        try {
            // Validate phone number
            if (phoneNumber.length != 10) {
                emit(Resource.Error("Please enter a valid 10-digit phone number"))
                return@flow
            }
            
            // Emit loading state
            emit(Resource.Loading())
            
            // Make API call
            val response = apiService.requestOTP(mapOf("phone" to phoneNumber))
            
            // Check if response has error
            if (!response.error) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message))
            }
            
        } catch (e: IOException) {
            emit(Resource.Error("Network error. Please check your connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to send OTP"))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }
    
    override fun verifyOTP(phoneNumber: String, otp: String): Flow<Resource<TokenResponse>> = flow {
        try {
            // Validate OTP
            if (otp.length != 5) {
                emit(Resource.Error("Please enter a valid 5-digit OTP"))
                return@flow
            }
            
            // Emit loading state
            emit(Resource.Loading())
            
            // Make API call
            val response = apiService.verifyOTP(
                mapOf(
                    "phone" to phoneNumber,
                    "otp" to otp
                )
            )
            
            // Check if response has error
            if (response.error.isNullOrEmpty()) {
                // Save token on successful verification
                saveAuthToken(response.token)
                markUserLoggedIn()
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.error))
            }
            
        } catch (e: IOException) {
            emit(Resource.Error("Network error. Please check your connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to verify OTP"))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return dataStore.isLoggedIn()
    }
    
    override suspend fun getAuthToken(): String? {
        return try {
            val token = dataStore.getToken()
            if (token.isNotEmpty()) token else null
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun saveAuthToken(token: String) {
        dataStore.setToken(token)
    }
    
    override suspend fun logout() {
        // Clear all stored authentication data
        dataStore.setToken("")
        dataStore.setLoggedIn(false)
        // Note: We don't reset first launch flag as it's a one-time setup
    }
    
    override suspend fun isFirstLaunch(): Boolean {
        return dataStore.isFirstLaunch()
    }
    
    override suspend fun markFirstLaunchCompleted() {
        dataStore.setFirstLaunch(false)
    }
    
    override suspend fun markUserLoggedIn() {
        dataStore.setLoggedIn(true)
    }
}