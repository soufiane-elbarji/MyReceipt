package com.receiptreader.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Theme preferences manager using DataStore.
 * 
 * PRIVACY NOTE: All preferences are stored locally on the device.
 * No data is synced to any cloud service.
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemePreferences(private val context: Context) {
    
    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
    }
    
    /**
     * Observe dark mode preference as Flow.
     */
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: true // Default to dark mode
    }
    
    /**
     * Observe system theme preference as Flow.
     */
    val useSystemTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USE_SYSTEM_THEME] ?: false
    }
    
    /**
     * Toggle dark mode.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = enabled
        }
    }
    
    /**
     * Set whether to use system theme.
     */
    suspend fun setUseSystemTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_SYSTEM_THEME] = enabled
        }
    }
}
