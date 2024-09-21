package com.finance.trade_learn.utils

import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.modelsConvector.Percent
import com.finance.trade_learn.view.wallet.format
import java.util.*
import kotlin.collections.ArrayList



fun transformationCoinItemDTO(list: List<CoinDetail>): ArrayList<CoinsHome> {
    val listItem = arrayListOf<CoinsHome>()
    for (i in list) {
        val coinImage=i.image
        val coinName = i.name.uppercase(Locale.getDefault()) + " / USD"
        val coinSymbol = i.symbol.uppercase(Locale.getDefault()) + " / USD"
        val coinPrice = i.current_price?.format(2).toString().ifEmpty { "0.0" }
        val percenteChange: Percent = if (i.price_change_24h==null){
            Percent("0.0","+","%")
        } else {
            percentageChange(i.price_change_percentage_24h.toString())
        }

        val coinPercenteChange = percenteChange.raise + (percenteChange.percentChange.toString() + "0000").subSequence(0, 4).toString() + "%"

        val item = CoinsHome(
            id = i.id,
            coinName,
            coinSymbol,
            coinPrice,
            coinPercenteChange,
            coinImage,
            i.market_cap,
            total_volume = i.total_volume,)
        listItem.add(item)
    }
    return listItem
}


fun percentageChange(coinPrice: String): Percent {
    return when (coinPrice.subSequence(0, 1)) {
        "-" -> Percent(coinPrice.subSequence(1, coinPrice.length).toString().toDouble().format(2), "-")
        else -> Percent(coinPrice.subSequence(0, coinPrice.length).toString().toDouble().format(2), "+")
    }
}