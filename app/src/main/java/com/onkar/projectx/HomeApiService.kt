package com.onkar.projectx

import com.onkar.projectx.data.BasicResponse
import com.onkar.projectx.data.CatalogResponse
import com.onkar.projectx.data.DeliveryItem
import com.onkar.projectx.data.HomeResponse
import com.onkar.projectx.data.SubscriptionRequest
import com.onkar.projectx.data.TokenResponse
import com.onkar.projectx.data.UserDashboardResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

    @POST("api/customer/send-otp/")
    suspend fun requestOTP(
        @Body request: Map<String, String>
    ): BasicResponse

    @POST("api/customer/verify-otp/")
    suspend fun verifyOTP(
        @Body request: Map<String, String>
    ): TokenResponse

    @GET("api/catalog/")
    suspend fun getCatalog(
        @Header("Authorization") token: String
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
}

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/")
        .addConverterFactory(GsonConverterFactory.create()) // or GsonConverterFactory
        .build()

    val homeApi: HomeApiService = retrofit.create(HomeApiService::class.java)
}