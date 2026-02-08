package com.myreceipt.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Preferences manager for theme settings. Uses DataStore for persistent preferences storage. */
class ThemePreferences(private val context: Context) {
    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
    }

    val isDarkMode: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[IS_DARK_MODE] ?: true // Default to dark mode
            }

    val useSystemTheme: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[USE_SYSTEM_THEME] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[IS_DARK_MODE] = enabled }
    }

    suspend fun setUseSystemTheme(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[USE_SYSTEM_THEME] = enabled }
    }
}
