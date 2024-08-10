package com.finance.trade_learn.ctryptoApi

import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoOperationInterface {

    @GET("coins/markets")
    fun getCoinGeckoData(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("per_page") perPage: Int = 250,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
    ): Single<List<CoinDetail>>

    @GET("/allMarket")
    fun getLocalCoinList(): Single<List<CoinDetail>>


    @GET("coins/markets")
    fun getSelectedCoinToTradeCoinGecko(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("ids") ids: String,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
    ): Single<List<CoinDetail>>



    @GET("coins/list")
    fun getCoinList(): Single<List<CoinInfoList>>


}