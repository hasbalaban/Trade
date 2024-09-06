package com.finance.trade_learn.service.ctryptoApi

import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoOperationInterface {

    @GET("coins/markets")
    suspend fun getCoinGeckoData(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("per_page") perPage: Int = 250,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
    ): Response<List<CoinDetail>>

    @GET("/allMarket")
    suspend fun getLocalCoinList(): Response<WrapResponse<List<CoinDetail>?>>

    @GET("coins/markets")
    suspend fun getSelectedCoinToTradeCoinGecko(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("ids") ids: String,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
    ): Single<List<CoinDetail>>

}