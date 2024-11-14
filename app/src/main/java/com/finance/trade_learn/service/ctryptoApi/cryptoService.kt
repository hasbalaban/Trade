package com.finance.trade_learn.service.ctryptoApi

import com.finance.trade_learn.models.ScoreBoardItem
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.service.user.client
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class cryptoService() {

    //val localBaseUrl = "http://10.0.2.2:8080"
    val localBaseUrl = "https://learn-trade-d43b9356970c.herokuapp.com"
    var localRetrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(localBaseUrl)
        .build()
        .create(CryptoOperationInterface::class.java)

    suspend fun getCoinList(): Response<WrapResponse<List<CoinDetail>?>> {
        return localRetrofit.getLocalCoinList()
    }

    suspend fun getScoreBoard(requestedUserId : Int?): Response<WrapResponse<List<ScoreBoardItem>?>> {
        return localRetrofit.getScoreBoard(requestedUserId = requestedUserId)
    }

}