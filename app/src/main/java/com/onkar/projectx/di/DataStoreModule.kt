package com.onkar.projectx.di

import android.content.Context
import com.onkar.projectx.data.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthsModule {

    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }
}