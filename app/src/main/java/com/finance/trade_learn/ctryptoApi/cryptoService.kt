package com.finance.trade_learn.ctryptoApi

import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import io.reactivex.Single
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

    fun selectedCoinToTrade(coinName: String): Single<List<CoinDetail>> {
        return retrofit.getSelectedCoinToTradeCoinGecko(ids = coinName.lowercase())
    }

    fun getCoinGecko(page: Int): Single<List<CoinDetail>> {
        return retrofit.getCoinGeckoData(page = page)
    }

    fun getSelectedCoinToTradeCoinGecko(ids : String): Single<List<CoinDetail>> {
        return retrofit.getSelectedCoinToTradeCoinGecko(ids = ids)
    }

    fun getCoinList(): Single<List<CoinInfoList>> {
        return retrofit.getCoinList()
    }

}