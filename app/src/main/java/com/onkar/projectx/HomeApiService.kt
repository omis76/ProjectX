package com.onkar.projectx

import com.onkar.projectx.data.AddressRequest
import com.onkar.projectx.data.AddressResponse
import com.onkar.projectx.data.BasicResponse
import com.onkar.projectx.data.CatalogResponse
import com.onkar.projectx.data.CustomerProfileResponse
import com.onkar.projectx.data.DeliveryItem
import com.onkar.projectx.data.HomeResponse
import com.onkar.projectx.data.LocationCheckRequest
import com.onkar.projectx.data.LocationCheckResponse
import com.onkar.projectx.data.SubscriptionRequest
import com.onkar.projectx.data.TokenResponse
import com.onkar.projectx.data.UserDashboardResponse
import com.onkar.projectx.data.PaymentType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface HomeApiService {
    @GET("api/dashboard/home/")
    suspend fun getHomeData(
        @Header("Authorization") token: String
    ): HomeResponse

    @GET("api/dashboard/user-dashboard/")
    suspend fun getUserDashboard(
        @Header("Authorization") token: String
    ): UserDashboardResponse

    @POST("api/v1/auth/send-otp/")
    suspend fun requestOTP(
        @Body request: Map<String, String>
    ): BasicResponse

    @POST("api/v1/auth/verify-otp/")
    suspend fun verifyOTP(
        @Body request: Map<String, String>
    ): TokenResponse

    @GET("api/v1/catalog/")
    suspend fun getCatalog(
        @Header("Authorization") token: String,
        @Query("city_id") cityId: Int = 1 // Default to city_id=1
    ): CatalogResponse

    @GET("api/orders/my-deliveries/")
    suspend fun getDeliveriesForDate(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): List<DeliveryItem>

    @POST("api/orders/subscriptions/")
    suspend fun createOrderOrSubscription(
        @Header("Authorization") token: String,
        @Body request: SubscriptionRequest
    ): Response<Unit>

    @GET("api/payment/methods")
    suspend fun getPaymentMethods(): List<PaymentType>

    @POST("api/v1/customers/check_location/")
    suspend fun checkLocationServiceability(
        @Header("Authorization") token: String,
        @Body request: LocationCheckRequest
    ): LocationCheckResponse

    @POST("api/v1/customers/address/")
    suspend fun saveAddress(
        @Header("Authorization") token: String,
        @Body request: AddressRequest
    ): AddressResponse

    @GET("api/v1/customers/profile/")
    suspend fun getCustomerProfile(
        @Header("Authorization") token: String
    ): CustomerProfileResponse
}

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/")
        .addConverterFactory(GsonConverterFactory.create()) // or GsonConverterFactory
        .build()

    val homeApi: HomeApiService = retrofit.create(HomeApiService::class.java)
}