package com.finance.trade_learn.view.market.marketViewStates

import com.finance.trade_learn.models.FilterType

data class MarketPageUiState (
    val searchText : String = "",
    val filterType: FilterType = FilterType.Default,
    val isFocused : Boolean = false
)