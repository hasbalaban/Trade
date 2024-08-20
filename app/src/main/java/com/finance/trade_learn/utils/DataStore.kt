package com.finance.trade_learn.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreKeys {
    object BooleanKeys {
        val isRememberMeSelected = booleanPreferencesKey("isRememberMeSelected")
    }

    object StringKeys {
        val email = stringPreferencesKey("email")
        val password = stringPreferencesKey("password")
    }
}

suspend fun Context.saveBooleanPreference(key: Preferences.Key<Boolean>, value: Boolean) {
    dataStore.edit { preferences ->
        preferences[key] = value
    }
}

suspend fun Context.saveStringPreference(key: Preferences.Key<String>, value: String) {
    dataStore.edit { preferences ->
        preferences[key] = value
    }
}


fun Context.readStringPreference(
    key: Preferences.Key<String>,
    defaultValue: String = ""
): Flow<String> {
    return dataStore.data
        .map { preferences ->
            preferences[key] ?: defaultValue
        }
}

suspend fun Context.clearSpecificPreference(key: Preferences.Key<String>) {
    dataStore.edit { preferences ->
        preferences.remove(key)
    }
}



suspend fun Context.clearAllPreferences() {
    dataStore.edit { preferences ->
        preferences.clear()
    }
}

