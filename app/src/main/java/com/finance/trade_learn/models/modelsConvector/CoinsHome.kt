package com.finance.trade_learn.models.modelsConvector

import com.finance.trade_learn.enums.enumPriceChange

data class CoinsHome(
    val CoinName: String,
    val coinSymbol: String,
    val CoinPrice: String,
    val CoinChangePercente: String,
    val CoinImage: String,
    val raise: enumPriceChange,
    val marketCap : String,
    val total_volume : String,
)