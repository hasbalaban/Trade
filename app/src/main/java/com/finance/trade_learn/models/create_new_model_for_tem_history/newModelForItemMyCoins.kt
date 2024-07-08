package com.finance.trade_learn.models.create_new_model_for_tem_history

import java.math.BigDecimal

data class NewModelForItemHistory(
    var CoinName: String,
    var CoinAmount: BigDecimal,
    var Total: BigDecimal,
    val Image: String


)