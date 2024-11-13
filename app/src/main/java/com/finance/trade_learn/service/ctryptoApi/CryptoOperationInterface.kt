package com.finance.trade_learn.service.ctryptoApi

import com.finance.trade_learn.models.ScoreBoardItem
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoOperationInterface {
    @GET("/markets")
    suspend fun getLocalCoinList(): Response<WrapResponse<List<CoinDetail>?>>

    @GET("/scoreBoard")
    suspend fun getScoreBoard(
        @Query("requestedUserId") requestedUserId : Int?,
    ): Response<WrapResponse<List<ScoreBoardItem>?>>
}