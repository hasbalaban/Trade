package com.finance.trade_learn.models.watchList

import com.finance.trade_learn.base.BaseViewModel.Companion.allCryptoItems
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.modelsConvector.Percent
import com.finance.trade_learn.utils.percentageChange
import com.finance.trade_learn.view.wallet.format
import java.util.Locale


data class WatchListRequestItem (
    val userId : Int,
    val itemId :String,
    val isRemoved : Boolean
)

data class WatchListItem (
    val itemId :String,
    val addedTime : Long
)

fun List<WatchListItem>?.toCoinHome() : List<CoinsHome> {
    val mappedList = this?.map { watchListItem ->
        val currentItemInfo = allCryptoItems.value.firstOrNull { it.id == watchListItem.itemId }
        if (currentItemInfo == null) null
        else {

            val percenteChange: Percent = if (currentItemInfo.price_change_24h == null) {
                Percent("0.0", "+")
            } else {
                percentageChange(currentItemInfo.price_change_percentage_24h.toString())
            }

            val coinPercenteChange = percenteChange.raise + percenteChange.percentChange.format(2) + "%"

            CoinsHome(
                id = currentItemInfo.id,
                CoinName = currentItemInfo.name.uppercase(Locale.getDefault()) + " / USD",
                coinSymbol = currentItemInfo.symbol.uppercase(Locale.getDefault()) + " / USD",
                CoinPrice = currentItemInfo.current_price?.format(2) ?: "0.0",
                CoinChangePercente = coinPercenteChange,
                CoinImage = currentItemInfo.image,
                marketCap = currentItemInfo.market_cap,
                total_volume = currentItemInfo.total_volume,
            )
        }
    }?.mapNotNull { it } ?: emptyList()

    return mappedList
}