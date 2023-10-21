package com.finance.trade_learn.models

import com.finance.trade_learn.enums.TradeType


data class TradeState(
    var operationType : TradeType,
    var selectedPercent : SelectedPercent
)
enum class SelectedPercent (val value : Int){
    Percent25(1),
    Percent50(2),
    Percent75(3),
    Percent100(4)
}