package com.finance.trade_learn.view.trade


    sealed class TradePageUiState<out T> {
        object Loading : TradePageUiState<Nothing>()
        data class Data<T>(val data: T) : TradePageUiState<T>()
        data class Error(val message: String) : TradePageUiState<Nothing>()
    }

