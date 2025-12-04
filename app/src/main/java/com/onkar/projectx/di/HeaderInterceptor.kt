package com.onkar.projectx.di

import com.onkar.projectx.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val modifiedRequest = originalRequest.newBuilder()
            .header("x-source", "app")
            .header("x-device", "android")
            .header("x-app-version", BuildConfig.VERSION_NAME)
            .header("Content-Type", "application/json")
            .build()
        
        return chain.proceed(modifiedRequest)
    }
}