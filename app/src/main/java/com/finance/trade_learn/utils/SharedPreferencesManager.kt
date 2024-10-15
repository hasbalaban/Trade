package com.finance.trade_learn.utils

import android.content.Context

class SharedPreferencesManager(context: Context) {
    private var packName = "com.finance.trade_learn"
    private val sharedPreferencesManager =
        context.getSharedPreferences(packName, Context.MODE_PRIVATE)

    fun addSharedPreferencesString(keyName: String, value: String) {
        sharedPreferencesManager.edit().putString(keyName, value).apply()
    }

    fun getSharedPreferencesString(keyName: String, defaultValue: String = "bitcoin") =
        sharedPreferencesManager.getString(keyName, defaultValue).toString()

    fun addSharedPreferencesLong(keyName: String, value: Long) {
        sharedPreferencesManager.edit().putLong(keyName, value).apply()
    }

    fun getSharedPreferencesLong(keyName: String, i: Long) =
        sharedPreferencesManager.getLong(keyName, 0L)

    fun addSharedPreferencesBoolen(keyName: String, value: Boolean) {
        sharedPreferencesManager.edit().putBoolean(keyName, value).apply()
    }

    fun getSharedPreferencesBoolen(keyName: String, defaultValue : Boolean = false) =
        sharedPreferencesManager.getBoolean(keyName, defaultValue)

}