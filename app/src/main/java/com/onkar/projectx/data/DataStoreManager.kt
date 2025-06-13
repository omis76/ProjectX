package com.onkar.projectx.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject


private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val TOKEN = stringPreferencesKey("token")
    }

    suspend fun isFirstLaunch(): Boolean {
        return dataStore.data.first()[FIRST_LAUNCH] ?: true
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.first()[LOGGED_IN] ?: false
    }

    suspend fun setFirstLaunch(value: Boolean) {
        dataStore.edit { it[FIRST_LAUNCH] = value }
    }

    suspend fun setLoggedIn(value: Boolean) {
        dataStore.edit { it[LOGGED_IN] = value }
    }

    suspend fun getToken(): String {
        return "Token ${dataStore.data.first()[TOKEN]}"
    }

    suspend fun setToken(value: String) {
        dataStore.edit { it[TOKEN] = value }
    }
}