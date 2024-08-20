package com.finance.trade_learn.utils

import androidx.lifecycle.MutableLiveData
import com.finance.trade_learn.models.enumPriceChange
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.modelsConvector.Percent
import com.finance.trade_learn.models.DataForHomePage
import java.util.*
import kotlin.collections.ArrayList



class ConvertOperation(
    val t: List<CoinDetail>,
    var lastItems: List<CoinsHome>?
){

    var change = enumPriceChange.notr
    var ListOfCrypto = MutableLiveData<ArrayList<CoinsHome>>()
    fun convertDataToUse(): DataForHomePage {


        val ListItem = arrayListOf<CoinsHome>()
        val ListItemForCompare = arrayListOf<CoinsHome>()

        for ((position, i) in t.withIndex()) {
            val last = lastItems?.getOrNull(position)?.CoinPrice?.toDouble()
            val new = i.current_price ?: 0.0

            //control for state of change
            if (last != null) {
                change = if (last > new) enumPriceChange.negative
                else if (new > last) enumPriceChange.pozitive
                else enumPriceChange.notr
            }

            val coinImage=i.image
            val coinName = i.name.uppercase(Locale.getDefault()) + " / USD"
            val coinSymbol = i.symbol.uppercase(Locale.getDefault()) + " / USD"
            val coinPrice = (i.current_price.toString()+"00000000").subSequence(0, 8).toString()
            var percenteChange: Percent?=null
            percenteChange = if (i.price_change_24h==null){
                Percent(0.0,"+","%")
            } else {
                percenteChange(i.price_change_percentage_24h.toString())
            }

            val coinPercenteChange = percenteChange.raise + (percenteChange.percentChange
                .toString() + "0000").subSequence(0, 4).toString() + "%"

            val item = CoinsHome(
                id = i.id,
                coinName,
                coinSymbol,
                coinPrice,
                coinPercenteChange,
                coinImage,
                change,
                i.market_cap,
                total_volume = i.total_volume)

            val coinPercenteChangeCompare = percenteChange.percentChange
            val coinPriceCompare = i.current_price.toString()
            val itemCompare =
                CoinsHome(
                    id = i.id,
                    coinName,
                    coinSymbol,
                    coinPriceCompare,
                    coinPercenteChangeCompare.toString(),
                    coinImage,
                    enumPriceChange.notr,
                    marketCap = i.market_cap,
                    total_volume = i.total_volume
                )
            ListItemForCompare.add(itemCompare)
            ListItem.add(item)
        }

        ListOfCrypto.value = ListItem
        lastItems = ListItemForCompare
        return DataForHomePage(lastItems ?: emptyList(), ListOfCrypto.value ?: ArrayList())
    }

    private fun percenteChange(coinPrice: String): Percent {
        return when (coinPrice.subSequence(0, 1)) {
            "-" -> Percent(coinPrice.subSequence(1, coinPrice.length).toString().toDouble(), "-")
            else -> Percent(coinPrice.subSequence(0, coinPrice.length).toString().toDouble(), "+")
        }
    }

}