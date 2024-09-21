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
)