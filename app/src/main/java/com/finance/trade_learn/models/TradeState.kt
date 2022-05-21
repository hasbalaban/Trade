package com.finance.trade_learn.models

import com.finance.trade_learn.enums.TradeType


data class TradeState(
    var operationType : TradeType,
    var selectedPercent : SelectedPercent
)
enum class SelectedPercent {
    Percent25,Percent50,Percent75,Percent100
}