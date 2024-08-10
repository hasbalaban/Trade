package com.finance.trade_learn.ctryptoApi

import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import com.finance.trade_learn.models.handleResponse
import com.finance.trade_learn.utils.Constants
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class cryptoService() {
    val BaseUrlCoinGecko = "https://api.coingecko.com/api/v3/"
    var retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BaseUrlCoinGecko)
        .build()
        .create(CryptoOperationInterface::class.java)


    val localBaseUrl = "http://10.0.2.2:8080"
    var localRetrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(localBaseUrl)
        .build()
        .create(CryptoOperationInterface::class.java)

    fun selectedCoinToTrade(coinName: String): Single<List<CoinDetail>> {
        return retrofit.getSelectedCoinToTradeCoinGecko(ids = coinName.lowercase())
    }

    suspend fun getCoinList(page: Int): Response<WrapResponse<List<CoinDetail>?>> {
        return if (Constants.SHOULD_BE_LOCAL_REQUEST) localRetrofit.getLocalCoinList()
        else Response.success(retrofit.getCoinGeckoData(page = page).body().handleResponse())
    }


    fun getSelectedCoinToTradeCoinGecko(ids : String): Single<List<CoinDetail>> {
        return retrofit.getSelectedCoinToTradeCoinGecko(ids = ids)
    }

    fun getCoinList(): Single<List<CoinInfoList>> {
        return retrofit.getCoinList()
    }


}