package com.finance.trade_learn.depency_injection

import android.content.Context
import com.finance.trade_learn.database.DatabaseDao
import com.finance.trade_learn.database.dataBaseService
import com.finance.trade_learn.utils.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModules {

    @Provides
    @Singleton
    fun provideSharedPreferences (@ApplicationContext context: Context) = SharedPreferencesManager(context)

    @Provides
    @Singleton
    fun provideContext (@ApplicationContext context: Context) = context

    @Provides
    @Singleton
    fun provideDatabase (@ApplicationContext context: Context) : DatabaseDao = dataBaseService.invoke(context).databaseDao()

}