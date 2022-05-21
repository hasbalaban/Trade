package com.finance.trade_learn.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.finance.trade_learn.viewModel.ViewModelCurrentTrade


@Suppress("UNCHECKED_CAST")
class CurrentTradeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelCurrentTrade(context) as T
    }
}

