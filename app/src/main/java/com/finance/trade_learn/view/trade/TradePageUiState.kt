package com.finance.trade_learn.view.trade

data class TradePageUiState <T>(
    val isLoading: Boolean = false,
    val data: T? = null,
)

