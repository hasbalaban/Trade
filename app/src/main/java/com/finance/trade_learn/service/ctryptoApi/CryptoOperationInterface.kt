package com.finance.trade_learn.service.ctryptoApi

import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import retrofit2.Response
import retrofit2.http.GET

interface CryptoOperationInterface {
    @GET("/markets")
    suspend fun getLocalCoinList(): Response<WrapResponse<List<CoinDetail>?>>
}