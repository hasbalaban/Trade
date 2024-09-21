package com.finance.trade_learn.models.coin_gecko

import com.google.gson.annotations.SerializedName

data class CoinDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("sm")
    val symbol: String,

    @SerializedName("n")
    val name: String,

    @SerializedName("im")
    val image: String,

    @SerializedName("cp")
    val current_price: Double?,

    @SerializedName("mc")
    val market_cap: String,

    @SerializedName("tv")
    val total_volume: String,

    @SerializedName("pc")
    val price_change_24h: Double?,

    @SerializedName("pcp")
    val price_change_percentage_24h: Double?,

    @SerializedName("mcr")
    val market_cap_rank: Double?,


    val fully_diluted_valuation: String?,
    val high_24h: String?,
    val low_24h: String?,
    val market_cap_change_24h: String,
    val market_cap_change_percentage_24h: Double?,
    val circulating_supply: String?,
    val total_supply: String?,
    val max_supply: String?,
    val ath: String?,
    val ath_change_percentage: Double?,
    val ath_date: String?,
    val atl: Double?,
    val atl_change_percentage: String?,
    val atl_date: String?,
    val last_updated: String?
)