package com.finance.trade_learn.models.coin_gecko

import com.finance.trade_learn.models.SearchCoinItem

data class CoinDetail(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: String,
    val market_cap_rank: Double,
    val fully_diluted_valuation: String,
    val total_volume: String,
    val high_24h: String,
    val low_24h: String,
    val price_change_24h: String,
    val price_change_percentage_24h: Double,
    val market_cap_change_24h: String,
    val market_cap_change_percentage_24h: Double,
    val circulating_supply: String,
    val total_supply: String,
    val max_supply: String,
    val ath: String,
    val ath_change_percentage: Double,
    val ath_date: String,
    val atl: Double,
    val atl_change_percentage: String,
    val atl_date: String,
    val last_updated: String

)


data class CoinInfoList(
    val id: String,
    val symbol: String,
    val name: String
) : SearchCoinItem()